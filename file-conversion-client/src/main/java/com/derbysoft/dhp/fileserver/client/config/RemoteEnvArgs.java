package com.derbysoft.dhp.fileserver.client.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author neo.fei {neocxf@gmail.com}
 */
@PropertySource("classpath:remote-env.properties")
@Component
public class RemoteEnvArgs {
    /**
     *  PDF Transformer url
     */
    private final String pdfTransformerAddr;

    @Autowired
    public RemoteEnvArgs(@Value("${PDF_TRANSFORMER_ADDR}") String pdf_transfromer_addr) {
        this.pdfTransformerAddr = pdf_transfromer_addr;
    }

    public String getPdfTransformerAddr() {
        return this.pdfTransformerAddr;
    }
}
