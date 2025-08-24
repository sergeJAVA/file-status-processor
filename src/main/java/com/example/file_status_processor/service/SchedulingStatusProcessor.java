package com.example.file_status_processor.service;

import com.example.file_status_processor.constant.FileStatus;
import com.example.file_status_processor.model.File;
import com.example.file_status_processor.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class SchedulingStatusProcessor {

    private final FileRepository fileRepository;
    private final StatusProcessorService statusProcessorService;

    @Scheduled(fixedDelayString = "${scheduling.delay}", timeUnit = TimeUnit.SECONDS)
    protected void process() {
        List<File> files = fileRepository
                .findByFileStatus(FileStatus.SECOND_VALIDATION_SUCCESS);
        if (files.isEmpty()) {
            log.info("No files to change status");
            return;
        }

        for (File file : files) {
            file.setFileStatus(FileStatus.FILE_UPLOADED);
            fileRepository.save(file);
            log.info("The status of the file {} has been updated to {}.", file.getFileName(), FileStatus.FILE_UPLOADED);
        }
    }
}
