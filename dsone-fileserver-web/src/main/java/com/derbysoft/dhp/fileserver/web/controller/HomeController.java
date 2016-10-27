package com.derbysoft.dhp.fileserver.web.controller;

import com.derbysoft.dhp.fileserver.core.util.FileUtilsWrapper;
import com.derbysoft.dhp.fileserver.web.filter.ServletStringWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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

    @RequestMapping(value = "/demo", method = RequestMethod.GET)
    public @ResponseBody String demo() {
        return "hello world";
    }

    @RequestMapping(value = "/output-report", method = RequestMethod.GET)
    public void printWelcome(@RequestParam(value = "type", required = false, defaultValue = "pdf") String extension , HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        HttpServletResponseWrapper responseWrapper = new ServletStringWrapper(resp);

        req.getRequestDispatcher("/report").include(req, responseWrapper);

        String fileName = createPrefixDailyName("_report") + ".html";

        String htmlUrl = FileUtilsWrapper.storeFile(responseWrapper.toString(), fileName);

        // TODO: file cache to be introduced. the cache for this type hold for one day.

        String requestUrl = "/converter/html/" + extension + "?url=" + htmlUrl;

        req.getRequestDispatcher(requestUrl).forward(req, resp);
    }

    @RequestMapping(value = "/report", method = RequestMethod.GET)
    public String report(ModelMap model, HttpServletRequest req, HttpServletResponse resp) {
        model.addAttribute("message", "Hello, this file is actually a report file");
        return "report";
    }

    @RequestMapping(value = "/hello/{name:.+}", method = RequestMethod.GET)
    public ModelAndView hello(@PathVariable("name") String name) {

        ModelAndView model = new ModelAndView();
        model.addObject("msg", name);

        model.setViewName("hello");

        return model;

    }

}
