package com.derbysoft.dhp.fileserver.web.controller;

import com.derbysoft.dhp.fileserver.api.filter.ServletStringWrapper;
import com.derbysoft.dhp.fileserver.core.util.FileUtilsWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

import static com.derbysoft.dhp.fileserver.core.util.FileUtilsWrapper.createPrefixDailyName;

/**
 * @author neo.fei {neocxf@gmail.com}
 */
@Controller
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public @ResponseBody String demo() {
        return "Welcome to WESTERN world !!!";
    }

    @RequestMapping(value = "/output-report", method = RequestMethod.GET)
    public void outputReport(@RequestParam(value = "type", required = false, defaultValue = "pdf") String extension , HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        HttpServletResponseWrapper responseWrapper = new ServletStringWrapper(resp);

        req.getRequestDispatcher("/report").include(req, responseWrapper);

        String fileName = createPrefixDailyName("_report") + ".html";

        String htmlUrl = FileUtilsWrapper.storeFile(responseWrapper.toString(), fileName);

        String requestUrl = "/converter/html/" + extension + "?url=" + htmlUrl;

        req.getRequestDispatcher(requestUrl).forward(req, resp);
    }

    @RequestMapping(value = "/report", method = RequestMethod.GET)
    public String report(ModelMap model, HttpServletRequest req, HttpServletResponse resp) {
        model.addAttribute("message", "Hello, this file is actually a report file");
        return "report";
    }


}
