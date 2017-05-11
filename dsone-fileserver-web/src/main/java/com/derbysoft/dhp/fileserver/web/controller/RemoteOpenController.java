package com.derbysoft.dhp.fileserver.web.controller;

import com.derbysoft.dhp.fileserver.api.FileServerGatewayConstants;
import com.derbysoft.dhp.fileserver.core.util.FileUtilsWrapper;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.derbysoft.dhp.fileserver.core.util.FileUtilsWrapper.createPrefixDailyName;

/**
 * @author neo.fei {neocxf@gmail.com}
 */
@Controller
public class RemoteOpenController {
    private static final Logger logger = LoggerFactory.getLogger(RemoteOpenController.class);

    @ApiOperation(value = " transform the given html source string into a given format file ",
            notes = "  <ul> <li> the caller must provide their html file as a stream of string and post as <strong>content</strong> field </li> " +
                    "  <li> the operation will be synchronized, the result may be cached, so the caller should provide the <strong>fileName</strong> field if they want the different output, otherwise, the result will be all the same based on the cache impl </li></ul>",
            response = Void.class)
    @RequestMapping(value = FileServerGatewayConstants.REMOTE_CONVERTER_ENTRANCE, method = RequestMethod.POST)
    public void handleTheTransformation(@ApiParam(value = "the content of the html source (including various html tags)", required = true) @RequestParam(value = "content") String content,
                                        @ApiParam(value = "the type of generated file that you need, default is pdf", allowableValues = "pdf, png, jpeg")   @RequestParam(value = "type", required = false, defaultValue = "pdf") String extension,
                                        @ApiParam(value = "the filename of the generated file, default is '_default'")    @RequestParam(value = "fileName", required = false, defaultValue = "_default") String rawFileName,
                                         HttpServletRequest req,
                                        HttpServletResponse resp
    ) throws IOException, ServletException {
        String fileName = (rawFileName.equals("_default") ? createPrefixDailyName("_report") : rawFileName) + ".html";
        String htmlUrl = FileUtilsWrapper.storeFile(content, fileName);
        String requestUrl = FileServerGatewayConstants.CONVERTER_ENTRANCE + extension + "?url=" + htmlUrl;
        logger.trace(" going to forward the request to " + requestUrl);
        req.getRequestDispatcher(requestUrl).forward(req, resp);
    }

    @ApiOperation(value = " local test for the pdf converter interface, <br/> note that the file should be html format and lies in the exact server of this interface ")
    @RequestMapping(value = FileServerGatewayConstants.REMOTE_CONVERTER_ENTRANCE_LOCAL, method = RequestMethod.POST)
    public void handleTheTransformationWithLocalFile(@ApiParam(value = "the html file that gonna transform", required = true) @RequestParam(value="file") MultipartFile file,
                                                     @ApiParam(value = "the type of generated file that you need, default is pdf", allowableValues = "pdf, png, jpeg")   @RequestParam(value = "type", required = false, defaultValue = "pdf") String extension,
                                                     @ApiParam(value = "the filename of the generated file, default is '_default'")    @RequestParam(value = "fileName", required = false, defaultValue = "_default") String rawFileName,
                                                     @ApiParam(value = "the default on-load resolve time for the conversion of html file to target file type, default time is 200, max is 5000, min is 100")  @RequestParam(value = "resolveTime", required = false, defaultValue = "200") int resolveTime,
                                                     @ApiParam(value = "the converter output size")  @RequestParam(value = "outputSize", required = false, defaultValue = "A4") OutputSize size,
                                                     HttpServletRequest req,
                                                     HttpServletResponse resp
    ) throws IOException, ServletException {
        String fileName = (rawFileName.equals("_default") ? createPrefixDailyName("_report") : rawFileName) + ".html";
        String htmlUrl = FileUtilsWrapper.storeFile(file.getBytes(), fileName);
        String requestUrl = FileServerGatewayConstants.CONVERTER_ENTRANCE + extension + "?url=" + htmlUrl + "&resolveTime=" + resolveTime + "&outputSize=" + size;
        logger.trace(" going to forward the request to " + requestUrl);
        req.getRequestDispatcher(requestUrl).forward(req, resp);
    }


    @ApiOperation(value = " simple Restful interface")
    @RequestMapping(value = "/public/hello", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE} )
    public @ResponseBody ResponseEntity<GreetingData> welcome(@RequestParam("name") String name, @RequestParam("msg") String msg) {
        return new ResponseEntity<GreetingData>(new GreetingData(name, msg), HttpStatus.OK);
    }

    /**
     *  fake data for interface test
     */
    private static class GreetingData {
        private String name;
        private String msg;

        public GreetingData() {
        }

        public GreetingData(String name, String msg) {
            this.name = name;
            this.msg = msg;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }
}
