package com.derbysoft.dhp.fileserver.web.controller;

import com.derbysoft.dhp.fileserver.api.FileServerGatewayConstants;
import com.derbysoft.dhp.fileserver.core.util.FileUtilsWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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

    @RequestMapping(value = FileServerGatewayConstants.REMOTE_CONVERTER_ENTRANCE, method = RequestMethod.POST)
    public void handleTheTransformation(@RequestParam(value = "content") String content,
                                        @RequestParam(value = "type", required = false, defaultValue = "pdf") String extension,
                                        @RequestParam(value = "fileName", required = false, defaultValue = "_default") String rawFileName,
                                        HttpServletRequest req,
                                        HttpServletResponse resp
    ) throws IOException, ServletException {
        String fileName = (rawFileName.equals("_default") ? createPrefixDailyName("_report") : rawFileName) + ".html";
        String htmlUrl = FileUtilsWrapper.storeFile(content, fileName);
        String requestUrl = FileServerGatewayConstants.CONVERTER_ENTRANCE + extension + "?url=" + htmlUrl;
        logger.debug(" going to forward the request to " + requestUrl);
        req.getRequestDispatcher(requestUrl).forward(req, resp);
    }

}
