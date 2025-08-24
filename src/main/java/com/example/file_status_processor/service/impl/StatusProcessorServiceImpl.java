package com.example.file_status_processor.service.impl;

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
        if (existingFile.isPresent()) {
            File updated = existingFile.get();

            log.info("The status of the file {} has been updated from {} to {}.",
                    updated.getFileName(),
                    updated.getFileStatus(),
                    fileDto.getFileStatus());

            updated.setFileStatus(fileDto.getFileStatus());
            fileRepository.save(updated);
        }
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
        }

    }

}
