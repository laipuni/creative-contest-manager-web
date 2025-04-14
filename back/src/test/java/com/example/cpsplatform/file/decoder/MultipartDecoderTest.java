package com.example.cpsplatform.file.decoder;

import com.example.cpsplatform.exception.FileReadException;
import com.example.cpsplatform.exception.UnsupportedFileTypeException;
import com.example.cpsplatform.file.decoder.vo.FileSources;
import com.example.cpsplatform.file.domain.FileExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class MultipartDecoderTest {

    private MultipartDecoder multipartDecoder;

    @BeforeEach
    void setUp() {
        multipartDecoder = new MultipartDecoder();
    }
    @DisplayName("MultipartDecoder가 여러 파일을 정상 변환하는지 테스트")
    @Test
    void decode() throws IOException {
        //given
        byte[] content1 = "test content 1".getBytes();
        byte[] content2 = "test content 2".getBytes();

        MockMultipartFile file1 = new MockMultipartFile(
                "file1",
                "test1.pdf",
                "application/pdf",
                content1
        );

        MockMultipartFile file2 = new MockMultipartFile(
                "file2",
                "test2.pdf",
                "application/pdf",
                content2
        );

        List<MultipartFile> files = Arrays.asList(file1, file2);

        //when
        FileSources result = multipartDecoder.decode(files);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getFileSourceList()).hasSize(2);

        assertThat(result.getFileSourceList())
                .extracting("originalFilename","mimeType","extension","fileBytes","size")
                .containsExactlyInAnyOrder(
                        tuple(file1.getOriginalFilename(),file1.getContentType(),FileExtension.PDF,file1.getBytes(),file1.getSize()),
                        tuple(file2.getOriginalFilename(),file2.getContentType(),FileExtension.PDF,file2.getBytes(),file2.getSize())
                );
    }

    @Test
    @DisplayName("빈 파일 목록을 처리할 수 있는지 테스트")
    void decodeWithEmptyFileList() {
        //given
        List<MultipartFile> emptyFiles = Collections.emptyList();

        //when
        FileSources result = multipartDecoder.decode(emptyFiles);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getFileSourceList()).isEmpty();
    }

    @Test
    @DisplayName("originalFilename이 없는 경우 'unknown'으로 설정되는지 테스트")
    void convertToFileSourceWithNullOriginalFilename() {
        //given
        byte[] content = "test content".getBytes();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                null,
                "application/pdf",
                content);

        List<MultipartFile> files = Collections.singletonList(file);

        //when
        FileSources result = multipartDecoder.decode(files);

        //then
        assertThat(result.getFileSourceList())
                .extracting("originalFilename")
                .containsExactly("unknown");
    }

    @Test
    @DisplayName("지원되지 않는 ContentType이 주어졌을 때 예외 발생 테스트")
    void convertToFileSource_WithUnsupportedContentType_ShouldThrowException() {
        //given
        MultipartFile mockFile = mock(MultipartFile.class);
        given(mockFile.getContentType()).willReturn(null);

        List<MultipartFile> files = Collections.singletonList(mockFile);

        //when
        //then
        assertThatThrownBy(() -> multipartDecoder.decode(files))
                .isInstanceOf(UnsupportedFileTypeException.class)
                .hasMessage("해당 파일의 확장자는 지원하지 않습니다.");
    }
    @DisplayName("파일 읽기 실패 시 예외 발생 테스트")
    @Test
    void convertToFileSourceThrowIOException() throws IOException {
        //given
        MultipartFile mockFile = mock(MultipartFile.class);
        given(mockFile.getContentType()).willReturn("application/pdf");
        given(mockFile.getOriginalFilename()).willReturn("test.pdf");
        given(mockFile.getBytes()).willThrow(new IOException("Test IO Exception"));

        List<MultipartFile> files = Collections.singletonList(mockFile);

        //when
        //then
        assertThatThrownBy(() -> multipartDecoder.decode(files))
                .isInstanceOf(FileReadException.class)
                .hasMessage("파일의 읽는데 실패했습니다.");
    }

}