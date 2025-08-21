package com.apexManagent.servicios.utilerias;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.TemplateEngine;
import org.springframework.core.io.ResourceLoader;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer; // Import necesario para PDF

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class PdfGeneraterService {
    @Autowired
    private TemplateEngine templateEngine;

    //@Autowired
    //private ResourceLoader resourceLoader;

    public byte[] generatePdfFromHtml(String templateName, Map<String, Object> data) throws IOException {
        Context context = new Context();
        context.setVariables(data);
        String htmlContent = templateEngine.process(templateName, context);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream, false);
            renderer.finishPDF();
            return outputStream.toByteArray();
        }
    }

    public <T> byte[] generatePdfFromHtml(String templateName, String nameModelo, List<T> dataList)
            throws IOException {

        Context context = new Context();
        context.setVariable(nameModelo, dataList);

        String htmlContent = templateEngine.process(templateName, context);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream, false);
            renderer.finishPDF();
            return outputStream.toByteArray();
        }
    }
}