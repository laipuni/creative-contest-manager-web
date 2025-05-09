package com.example.cpsplatform.template.generator;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import org.springframework.core.io.ClassPathResource;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PdfCertificateGenerator implements CertificateGenerator{
    @Override
    public byte[] generate(final String html) {
        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
            ITextRenderer renderer = new ITextRenderer();

            String fontPath = new ClassPathResource("static/fonts/NanumGothic.ttf").getFile().getAbsolutePath();

            renderer.getFontResolver().addFont(
                    fontPath,
                    BaseFont.IDENTITY_H,
                    BaseFont.NOT_EMBEDDED
            );

            renderer.setDocumentFromString(html);

            renderer.layout();
            renderer.createPDF(outputStream);
            outputStream.close();
            return outputStream.toByteArray();
        } catch (IOException | DocumentException e) {
            throw new RuntimeException(e);
        }
    }
}
