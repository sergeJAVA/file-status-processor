package com.example.file_status_processor.service;

import com.example.file_status_processor.dto.FileDto;

public interface StatusProcessorService {

    FileDto getFile(String checksum);

    void updateStatus(FileDto fileDto);

    void saveFile(FileDto fileDto);

}
