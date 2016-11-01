package com.derbysoft.dhp.fileserver.web.controller;

import com.derbysoft.dhp.fileserver.api.FileServerGatewayConstants;
import com.derbysoft.dhp.fileserver.api.cache.ObjectFactory;
import com.derbysoft.dhp.fileserver.api.util.RegexUtils;
import com.derbysoft.dhp.fileserver.core.server.PhantomjsClient;
import com.derbysoft.dhp.fileserver.core.server.PhantomjsClient.ConverterConfig;
import com.derbysoft.dhp.fileserver.core.server.PhantomjsClient.PhantomjsResponse;
import com.derbysoft.dhp.fileserver.core.server.PhantomjsClient.ResponseEntity;
import com.derbysoft.dhp.fileserver.core.server.PhantomjsServiceExecutor;
import com.derbysoft.dhp.fileserver.core.util.FileUtilsWrapper;
import com.derbysoft.dhp.fileserver.core.util.MimeType;
import com.derbysoft.dhp.fileserver.core.util.TempDir;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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

    @RequestMapping(value = FileServerGatewayConstants.CONVERTER_ENTRANCE + "{fileType}", method = {RequestMethod.GET, RequestMethod.POST})
    public void convertUrlToHtml(HttpServletRequest request, HttpServletResponse response,
                                 @PathVariable("fileType") String fileType,
                                 @RequestParam(value = "fileName", required = false, defaultValue = "_default") String fileName,
                                 @RequestParam("url") String url) throws IOException, InterruptedException, TimeoutException, ExecutionException {
        String targetFileName = "";

        MimeType mimeType = MimeType.get(fileType); // return the MimeType enum object. if the filetype is unknown, return the PNG

        String fileExtension = mimeType.getExtension();

        if (! RegexUtils.isValidUrl(url)) {
            throw new IllegalArgumentException("illegal url: {} " + url);
        }

        if (fileName.equals("_default")) {
            targetFileName = FileUtilsWrapper.createRandomFileNameWithExtension(fileExtension);
        } else
            targetFileName = fileName + "." + fileExtension;

        ConverterConfig config = new ConverterConfig(url, targetFileName);

        ResponseEntity<PhantomjsResponse>  entity = serviceExecutor.execute(config, url);

        int responseCode = entity.getStatusCode();

        if (responseCode != 200) {
            logger.error("EXIT-STATUS - "); // error handling, should throw un-checked exception
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
