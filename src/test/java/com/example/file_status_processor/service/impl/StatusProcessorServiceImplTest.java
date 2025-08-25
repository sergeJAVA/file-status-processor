package com.example.file_status_processor.service.impl;

import com.example.file_status_processor.constant.FileStatus;
import com.example.file_status_processor.dto.FileDto;
import com.example.file_status_processor.model.File;
import com.example.file_status_processor.repository.FileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatusProcessorServiceImplTest {

    @Mock
    private FileRepository fileRepository;

    @InjectMocks
    private StatusProcessorServiceImpl statusProcessorService;

    private FileDto fileDto;
    private File fileEntity;

    @BeforeEach
    void setUp() {
        fileDto = FileDto.builder()
                .fileName("test.xlsx")
                .fileBytes("content".getBytes())
                .fileStatus(FileStatus.FIRST_VALIDATION_SUCCESS)
                .checksum("12345")
                .build();

        fileEntity = File.builder()
                .id("1")
                .fileName("test.xlsx")
                .fileBytes("content".getBytes())
                .fileStatus(FileStatus.FILE_ACCEPTED)
                .checksum("12345")
                .build();
    }

    @Test
    void getFile_ShouldReturnDto_WhenFileExists() {
        when(fileRepository.findByChecksum("12345")).thenReturn(Optional.of(fileEntity));

        FileDto result = statusProcessorService.getFile("12345");

        assertThat(result).isNotNull();
        assertThat(result.getFileName()).isEqualTo("test.xlsx");
        assertThat(result.getFileStatus()).isEqualTo(FileStatus.FILE_ACCEPTED);
    }

    @Test
    void getFile_ShouldReturnNull_WhenFileDoesNotExist() {
        when(fileRepository.findByChecksum("12345")).thenReturn(Optional.empty());

        FileDto result = statusProcessorService.getFile("12345");

        assertThat(result).isNull();
    }

    @Test
    void updateStatus_ShouldUpdateFileStatus_WhenFileExists() {
        when(fileRepository.findByChecksum("12345")).thenReturn(Optional.of(fileEntity));

        statusProcessorService.updateStatus(fileDto);

        verify(fileRepository).save(argThat(file ->
                file.getFileStatus().equals(FileStatus.FIRST_VALIDATION_SUCCESS)
        ));
    }

    @Test
    void updateStatus_ShouldDoNothing_WhenFileDoesNotExist() {
        when(fileRepository.findByChecksum("12345")).thenReturn(Optional.empty());

        statusProcessorService.updateStatus(fileDto);

        verify(fileRepository, never()).save(any());
    }

    @Test
    void saveFile_ShouldInsert_WhenFileDoesNotExist() {
        when(fileRepository.findByChecksum("12345")).thenReturn(Optional.empty());

        statusProcessorService.saveFile(fileDto);

        verify(fileRepository).save(any(File.class));
    }

    @Test
    void saveFile_ShouldNotInsert_WhenFileExists() {
        when(fileRepository.findByChecksum("12345")).thenReturn(Optional.of(fileEntity));

        statusProcessorService.saveFile(fileDto);

        verify(fileRepository, never()).save(any());
    }

}