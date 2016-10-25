package com.derbysoft.dhp.fileserver.web.controller;

import com.derbysoft.dhp.fileserver.core.server.ObjectFactory;
import com.derbysoft.dhp.fileserver.core.server.PhantomjsClient;
import com.derbysoft.dhp.fileserver.core.server.PhantomjsClientCache;
import com.derbysoft.dhp.fileserver.core.server.ServicePoolExecutor;
import com.derbysoft.dhp.fileserver.core.util.FileUtilsWrapper;
import com.derbysoft.dhp.fileserver.core.util.MimeType;
import com.derbysoft.dhp.fileserver.core.util.TempDir;
import com.google.gson.Gson;
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
import java.io.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author neo.fei {neocxf@gmail.com}
 */
@Controller
@RequestMapping("/converter")
public class FileConverterController {
    private static final Logger logger = LoggerFactory.getLogger(FileConverterController.class);

    @Autowired
    TempDir tempDir;

    @Autowired
    ObjectFactory<PhantomjsClient> objectFactory;

    @Autowired
    ServicePoolExecutor servicePoolExecutor;

    @RequestMapping(value = "/html/{fileType}", method = RequestMethod.GET)
    public void convertUrlToHtml(HttpServletRequest request, HttpServletResponse response,
                                 @PathVariable("fileType") String fileType,
                                 @RequestParam(value = "fileName", required = false, defaultValue = "_default") String fileName,
                                 @RequestParam("url") String url) throws IOException, InterruptedException, TimeoutException, ExecutionException {

        String targetFileName = "";

        MimeType mimeType = MimeType.get(fileType); // return the MimeType enum object. if the filetype is unknown, return the PNG

        String fileExtension = mimeType.getExtension();

        String cacheFileName = PhantomjsClientCache.get(url);
        if (cacheFileName.equals(PhantomjsClientCache.DEFAULT_FILENAME)) { // without the cache

            if (fileName.equals("_default")) {
                targetFileName = FileUtilsWrapper.createRandomFileNameWithExtension(fileExtension);
            } else
                targetFileName = fileName + "." + fileExtension;

            PhantomjsClient.ConverterConfig config = new PhantomjsClient.ConverterConfig(url, targetFileName);

            Gson gson = new Gson();

            String jsonStr = gson.toJson(config);

            System.out.println(jsonStr);

            Future<PhantomjsClient.ResponseEntity> future = servicePoolExecutor.submit(jsonStr);

            int responseCode = future.get().getStatusCode();

            System.out.println(" responseCode: {} " + responseCode);
//            int exitStatus = client.await();  //do a wait here to prevent it running for ever

            if (responseCode != 200) {
                logger.error("EXIT-STATUS - "); // error handling
            } else {
                PhantomjsClientCache.store(url, targetFileName);
            }
        } else {
            targetFileName = cacheFileName;
        }

        String longFileName = TempDir.getDownloadLink(targetFileName);

        if (mimeType == MimeType.PDF) {
            response.setHeader("Content-type", mimeType.getType() + "; charset=utf-8");
        } else
            response.setHeader("Content-type", mimeType.getType());

        /**
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
