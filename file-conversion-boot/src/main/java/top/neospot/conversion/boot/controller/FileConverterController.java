package top.neospot.conversion.boot.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import top.neospot.conversion.api.FileServerGatewayConstants;
import top.neospot.conversion.api.util.OutputSize;
import top.neospot.conversion.core.server.PhantomjsClient;
import top.neospot.conversion.core.server.PhantomjsClient.ConverterConfig;
import top.neospot.conversion.core.server.PhantomjsClient.FileConverterKey;
import top.neospot.conversion.core.server.PhantomjsClient.PhantomjsResponse;
import top.neospot.conversion.core.server.PhantomjsClient.ResponseEntity;
import top.neospot.conversion.core.server.PhantomjsServiceExecutor;
import top.neospot.conversion.core.util.FileUtilsWrapper;
import top.neospot.conversion.core.util.MimeType;
import top.neospot.conversion.core.util.TempDir;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * @author neo.fei {neocxf@gmail.com}
 */
@Controller
public class FileConverterController {
    private static final Logger logger = LoggerFactory.getLogger(FileConverterController.class);

    @Autowired
    TempDir tempDir;

    @Autowired
    ObjectFactory<PhantomjsClient> objectFactory;

    @Autowired
    PhantomjsServiceExecutor serviceExecutor;

    @ApiOperation(value = " transform the given url page into a given format file ",
            notes = "  <ul> <li> the caller must provide their valid url (html url) and post as <strong>url</strong> field </li> " +
                    "  <li> the operation will be synchronized, the result may be cached, so the caller should provide the <strong>fileName</strong> field if they want the different output, otherwise, the result will be all the same based on the cache impl </li></ul>",
            response = Void.class)
    @RequestMapping(value = FileServerGatewayConstants.CONVERTER_ENTRANCE + "{fileType}", method = {RequestMethod.GET, RequestMethod.POST})
    public void convertUrlToHtml(HttpServletRequest request, HttpServletResponse response,
                                 @ApiParam(value = "the type of generated file that you need, default is pdf", allowableValues = "pdf, png, jpeg")  @PathVariable("fileType") String fileType,
                                 @ApiParam(value = "the filename of the generated file, default is '_default'")  @RequestParam(value = "fileName", required = false, defaultValue = "_default") String fileName,
                                 @ApiParam(value = "the default on-load resolve time for the conversion of html file to target file type, default time is 200, max is 5000, min is 100")  @RequestParam(value = "resolveTime", required = false, defaultValue = "200") int resolveTime,
                                 @ApiParam(value = "the converter output size")  @RequestParam(value = "outputSize", required = false, defaultValue = "A4") OutputSize size,
                                 @ApiParam(value = "the zoomFactor for the conversion, the remote client should not set this, behind the scene, the parameter has already been set. Right now, only for demo purpose")  @RequestParam(value = "zoomFactor", required = false, defaultValue = "-1") float _zoomFactor,
                                 @ApiParam(value = "the valid http url address", required = true) @RequestParam("url") String url) throws IOException, InterruptedException, TimeoutException, ExecutionException {
        String targetFileName = "";

        MimeType mimeType = MimeType.get(fileType); // return the MimeType enum object. if the filetype is unknown, return the PNG

        String fileExtension = mimeType.getExtension();

//        if (! RegexUtils.isValidUrl(url)) {
//            throw new IllegalArgumentException("illegal url: {} " + url);
//        }

        if (fileName.equals("_default")) {
            targetFileName = FileUtilsWrapper.createRandomFileNameWithExtension(fileExtension);
        } else
            targetFileName = fileName + "." + fileExtension;

        String outputSize = OutputSize.val(size);
        float zoomFactor = OutputSize.zoomFactor(size);

        if (_zoomFactor > 0)
            zoomFactor = _zoomFactor;

        ConverterConfig config = new ConverterConfig(url, targetFileName, resolveTime, outputSize, zoomFactor);
        FileConverterKey key = new FileConverterKey(url, fileExtension, resolveTime, outputSize);

        ResponseEntity<PhantomjsResponse>  entity = serviceExecutor.execute(config, key);

        int responseCode = entity.getStatusCode();

        if (responseCode != 200) {
            logger.error("PhantomjsClient call failed, with the EXIT-STATUS - " + responseCode); // error handling, should throw un-checked exception
        }

        PhantomjsResponse phantomjsResponse = entity.of(PhantomjsResponse.class);

        targetFileName = phantomjsResponse.getFileName(); // get the cached file name

        String longFileName = TempDir.getDownloadLink(targetFileName);

        if (mimeType == MimeType.PDF) {
            response.setHeader("Content-type", mimeType.getType() + "; charset=utf-8");
        } else
            response.setHeader("Content-type", mimeType.getType());

        /*
         *  if we want the file type to be downloadable, you can switch the following one line on
         */
//        response.setHeader("Content-Disposition", "attachment; filename=" + targetFileName);

        ServletOutputStream servletOut = response.getOutputStream();

        byte[] data = FileUtils.readFileToByteArray(new File(longFileName));

        servletOut.write(data);
        servletOut.flush();
        servletOut.close();
    }

}
