package com.example.cpsplatform.template.generator;

import com.example.cpsplatform.exception.FontLoadException;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;

@Slf4j
public class PdfCertificateGenerator implements CertificateGenerator{

    private static final String FONT_RESOURCE_PATH = "static/fonts/NanumGothic.ttf";
    private static final String TEMP_FONT_PREFIX = "NanumGothic";
    private static final String TEMP_FONT_SUFFIX = ".ttf";

    private final String cachedFontPath;

    /**
     * 폰트 파일 생성 비용은 애플리케이션 시작 시 한 번만 발생.
     * 동시 요청에도 cachedFontPath만 공유하므로 스레드 안정성 확보.
     */
    public PdfCertificateGenerator() {
        try {
            // 클래스패스에서 폰트 로딩
            ClassPathResource fontResource = new ClassPathResource(FONT_RESOURCE_PATH);
            File tempFontFile = File.createTempFile(TEMP_FONT_PREFIX, TEMP_FONT_SUFFIX);

            try (InputStream in = fontResource.getInputStream();
                 OutputStream out = new FileOutputStream(tempFontFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            }

            tempFontFile.deleteOnExit(); // JVM 종료 시 삭제
            this.cachedFontPath = tempFontFile.getAbsolutePath();
        } catch (IOException e) {
            throw new FontLoadException("PDF 생성을 위해 폰트를 읽어들이는데 실패했습니다.", e);
        }
    }
    @Override
    public byte[] generate(String html) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();

            renderer.getFontResolver().addFont(
                    cachedFontPath,
                    BaseFont.IDENTITY_H,
                    BaseFont.NOT_EMBEDDED
            );

            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        } catch (IOException | DocumentException e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }
}
