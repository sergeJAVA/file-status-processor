package com.example.file_status_processor.service;

import com.example.file_status_processor.constant.FileStatus;
import com.example.file_status_processor.model.File;
import com.example.file_status_processor.repository.FileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SchedulingStatusProcessorTest {

    @Mock
    private FileRepository fileRepository;
    @Mock
    private StatusProcessorService statusProcessorService;

    @InjectMocks
    private SchedulingStatusProcessor schedulingStatusProcessor;

    @Test
    void process_whenNoFiles_shouldLogAndReturn() {
        when(fileRepository.findByFileStatus(FileStatus.SECOND_VALIDATION_SUCCESS))
                .thenReturn(Collections.emptyList());

        schedulingStatusProcessor.process();

        verify(fileRepository, times(1)).findByFileStatus(FileStatus.SECOND_VALIDATION_SUCCESS);
        verify(fileRepository, never()).save(any());
    }

    @Test
    void process_whenFilesExist_shouldUpdateStatusAndSave() {

        File file = File.builder()
                .fileName("file.xls")
                .fileStatus(FileStatus.SECOND_VALIDATION_SUCCESS)
                .fileBytes("cnt".getBytes())
                .checksum("3456")
                .build();

        File file2 = File.builder()
                .fileName("file2.xls")
                .fileStatus(FileStatus.SECOND_VALIDATION_SUCCESS)
                .fileBytes("cnt".getBytes())
                .checksum("1234")
                .build();

        when(fileRepository.findByFileStatus(FileStatus.SECOND_VALIDATION_SUCCESS))
                .thenReturn(List.of(file, file2));

        schedulingStatusProcessor.process();

        ArgumentCaptor<File> captor = ArgumentCaptor.forClass(File.class);
        verify(fileRepository, times(2)).save(captor.capture());

        List<File> savedFiles = captor.getAllValues();
        assertEquals(FileStatus.FILE_UPLOADED, savedFiles.get(0).getFileStatus());
        assertEquals(FileStatus.FILE_UPLOADED, savedFiles.get(1).getFileStatus());
    }
}