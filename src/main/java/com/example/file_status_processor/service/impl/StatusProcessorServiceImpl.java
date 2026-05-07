package com.example.file_status_processor.service.impl;

import com.example.file_status_processor.constant.FileStatus;
import com.example.file_status_processor.dto.FileDto;
import com.example.file_status_processor.model.File;
import com.example.file_status_processor.repository.FileRepository;
import com.example.file_status_processor.service.StatusProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatusProcessorServiceImpl implements StatusProcessorService {

    private final FileRepository fileRepository;

    @Override
    public FileDto getFile(String checksum) {
        Optional<File> existingFile = fileRepository.findByChecksum(checksum);
        if (existingFile.isPresent()) {
            return FileDto.builder()
                    .fileName(existingFile.get().getFileName())
                    .fileBytes(existingFile.get().getFileBytes())
                    .fileStatus(existingFile.get().getFileStatus())
                    .checksum(existingFile.get().getChecksum())
                    .build();
        } else {
            log.info("The file with checksum {} not exist", checksum);
            return null;
        }
    }

    @Override
    public void updateStatus(FileDto fileDto) {
        Optional<File> existingFile = fileRepository.findByChecksum(fileDto.getChecksum());
        existingFile.ifPresent(file -> applyStatusUpdate(file, fileDto));
    }

    @Override
    public void saveFile(FileDto fileDto) {
        Optional<File> existingFile = fileRepository.findByChecksum(fileDto.getChecksum());
        if (existingFile.isEmpty()){
            File file = File.builder()
                    .fileName(fileDto.getFileName())
                    .fileBytes(fileDto.getFileBytes())
                    .fileStatus(fileDto.getFileStatus())
                    .checksum(fileDto.getChecksum())
                    .build();
            fileRepository.save(file);
        } else {
            log.info("The file with the checksum {} already exists", fileDto.getChecksum());
        }
    }

    private void applyStatusUpdate(File file, FileDto fileDto) {
        FileStatus previousStatus = file.getFileStatus();
        FileStatus newStatus = fileDto.getFileStatus();
        file.setFileStatus(newStatus);

        if (newStatus == FileStatus.SECOND_VALIDATION_SUCCESS) {
            file.setFileBytes(fileDto.getFileBytes());
            file.setFileStatus(FileStatus.FILE_UPLOADED);
            log.info("File {} bytes saved, status changed: {} -> {} -> {}",
                    file.getFileName(),
                    previousStatus,
                    FileStatus.SECOND_VALIDATION_SUCCESS,
                    FileStatus.FILE_UPLOADED);
        } else {
            log.info("The status of the file {} has been updated from {} to {}.",
                    file.getFileName(),
                    previousStatus,
                    newStatus);
        }
        fileRepository.save(file);
    }

}
