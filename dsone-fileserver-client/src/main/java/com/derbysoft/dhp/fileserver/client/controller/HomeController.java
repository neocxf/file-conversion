package com.derbysoft.dhp.fileserver.client.controller;

import com.derbysoft.dhp.fileserver.api.filter.ServletStringWrapper;
import com.derbysoft.dhp.fileserver.api.http.HttpClientAdapter;
import com.derbysoft.dhp.fileserver.api.util.OutputSize;
import com.derbysoft.dhp.fileserver.client.config.RemoteEnvArgs;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.impl.client.AbstractResponseHandler;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author neo.fei {neocxf@gmail.com}
 */
@Controller
public class HomeController {

    @Autowired
    private HttpClientAdapter httpClientAdapter;

    @Autowired
    private RemoteEnvArgs remoteEnvArgs;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String demo(ModelMap model) {
        return "report";
    }

    @RequestMapping(value = "/output-report", method = RequestMethod.GET)
    public void outputReport(@RequestParam(value = "type", required = false, defaultValue = "pdf") String extension , HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        HttpServletResponseWrapper responseWrapper = new ServletStringWrapper(resp);

        req.getRequestDispatcher("/report").include(req, responseWrapper);

        String content = responseWrapper.toString();

        List<NameValuePair> reqParams = new ArrayList<>();
        reqParams.add(new BasicNameValuePair("fileName", String.valueOf(System.currentTimeMillis())));
        reqParams.add(new BasicNameValuePair("content", content));
        reqParams.add(new BasicNameValuePair("resolveTime", "200"));
        reqParams.add(new BasicNameValuePair("outputSize", OutputSize.A4.val()));



        httpClientAdapter.handlePostRequest(remoteEnvArgs.getPdfTransformerAddr(), reqParams, new AbstractResponseHandler<Object>() {
            @Override
            public Object handleEntity(HttpEntity entity) throws IOException {
                entity.writeTo(resp.getOutputStream());
                return null;
            }
        });

    }

    @RequestMapping(value = "/report", method = RequestMethod.GET)
    public String report(ModelMap model, HttpServletRequest req, HttpServletResponse resp) {
        model.addAttribute("message", "Hello, this file is actually a report file");
        return "report";
    }

}
