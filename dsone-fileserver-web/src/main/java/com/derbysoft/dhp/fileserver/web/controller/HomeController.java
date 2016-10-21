package com.derbysoft.dhp.fileserver.web.controller;

import com.derbysoft.dhp.fileserver.core.server.PhantomjsClient;
import com.derbysoft.dhp.fileserver.core.util.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @author neo.fei {neocxf@gmail.com}
 */
@Controller
@RequestMapping("/")
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    public static final String PHANTOMJSAPPLICATION = System.getProperty("PHANTOMJSAPPLICATION", "/usr/local/bin/phantomjs");
    public static final String PHANTOMJSSCRIPT = System.getProperty("PHANTOMJSSCRIPT", "/usr/local/bin/phantom-renderer.js");


    @RequestMapping(value = "/demo", method = RequestMethod.GET)
    public @ResponseBody String demo() {
        return "hello world";
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String printWelcome(ModelMap model) {
        System.out.println(" going to redirect to hello.jsp");
        model.addAttribute("message", "Spring 3 MVC Hello World");
        return "hello";
    }

    @RequestMapping(value = "/hello/{name:.+}", method = RequestMethod.GET)
    public ModelAndView hello(@PathVariable("name") String name) {

        ModelAndView model = new ModelAndView();
        model.addObject("msg", name);

        model.setViewName("hello");

        return model;

    }
    @RequestMapping(value = "/convert/html/{fileName}.{fileType}", method = RequestMethod.GET)
    public void convertUrlToHtml(HttpServletRequest request, HttpServletResponse response,
                                 @PathVariable("fileName") String fileName,
                                 @PathVariable("fileType") String fileType,
                                 @RequestParam("url") String url) throws IOException {

        String targetFileName = fileName + "." + fileType;

        new PhantomjsClient("/usr/bin/phantomjs", "/home/fei/workspace/highcharts-export-server/tutorials/rasterize.js", url, targetFileName, "1920px");

        String longFileName = TempDir.getOutputDir().toAbsolutePath().toString() + File.separator +  targetFileName;

        response.setHeader("Content-type", "application/pdf");

        ServletOutputStream servletOut = response.getOutputStream();

        File inputFile = new File(longFileName);
        byte[] data = new byte[(int) inputFile.length()];
        FileInputStream fis = new FileInputStream(inputFile);
        fis.read(data, 0, data.length);
        fis.close();


        servletOut.write(data);
        servletOut.flush();
        servletOut.close();

        System.err.println(url);

    }

    @RequestMapping(value = "/convert/html/{fileType}", method = RequestMethod.POST)
    public void convertHtml(HttpServletRequest request, HttpServletResponse response,
                            @PathVariable("fileType") String fileType) throws IOException {

        int jobId = getJobId();

        String HTML_FILE_PATH = "/tmp/request-" + jobId + ".html";
        String OUTPUT_FILE_PATH = "/tmp/graphic-" + jobId + "." + fileType;

        if (fileType.equals("png")) {
            response.setHeader("Content-type", "image/png");
        } else if (fileType.equals("pdf")) {
            response.setHeader("Content-type", "application/pdf");
        }

        ServletOutputStream servletOut = response.getOutputStream();

        try {
            String queryString = request.getParameter("content");

            FileOutputStream fileOut = new FileOutputStream(HTML_FILE_PATH);
            OutputStreamWriter writer = new OutputStreamWriter(fileOut, "UTF-8");

            writer.write(queryString);
            writer.close();

            Process process = Runtime.getRuntime().exec(PHANTOMJSAPPLICATION + " " + PHANTOMJSSCRIPT + " " + HTML_FILE_PATH + " " + OUTPUT_FILE_PATH);

            int exitStatus = process.waitFor();  //do a wait here to prevent it running for ever

            if (exitStatus != 0) {
                logger.error("EXIT-STATUS - " + process.toString());
            }

            response.setHeader("Content-Disposition", "attachment; filename=graphic-" + jobId + "." + fileType);

            File inputFile = new File(OUTPUT_FILE_PATH);

            byte[] data = new byte[(int) inputFile.length()];
            FileInputStream fis = new FileInputStream(inputFile);
            fis.read(data, 0, data.length);
            fis.close();

            servletOut.write(data);
            servletOut.flush();
            servletOut.close();

        } catch (InterruptedException ex) {
            logger.error("Process Interrupted. ", ex);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception ex) {
            logger.error("An internal error occurred. ", ex);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        releaseJobId(jobId);
    }

    private void releaseJobId(int jobId) {

    }

    private int getJobId() {
        return 0;
    }
}
