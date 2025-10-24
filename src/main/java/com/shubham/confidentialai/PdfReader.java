package com.shubham.confidentialai;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.Resource;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class PdfReader {

    @Value("classpath:bob.pdf")
    private Resource pdfResource;

    @Autowired
    VectorStore vectorStore;

    @PostConstruct
    public void load() {
        System.out.println("Loading PDF file...");
        var config = PdfDocumentReaderConfig.builder()
                .withPageExtractedTextFormatter(
                        new ExtractedTextFormatter.Builder()
                                .build())
                .build();

        var pdfReader = new PagePdfDocumentReader(pdfResource, config);
        var textSplitter = new TokenTextSplitter();
        vectorStore.accept(textSplitter.apply(pdfReader.get()));
        ((SimpleVectorStore)vectorStore).save(new File("vectorestore.json"));
    }

}
