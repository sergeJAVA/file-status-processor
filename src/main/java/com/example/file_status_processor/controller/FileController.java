package com.example.file_status_processor.controller;

import com.example.file_status_processor.dto.CheckStatusRequest;
import com.example.file_status_processor.dto.FileDto;
import com.example.file_status_processor.service.StatusProcessorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/status")
@RequiredArgsConstructor
public class FileController {

    private final StatusProcessorService statusProcessorService;

    @GetMapping
    public FileDto checkStatus(@RequestParam String checksum) {
        return statusProcessorService.getFile(checksum);
    }

    @PostMapping
    public void saveFile(@RequestBody FileDto fileDto) {
        statusProcessorService.saveFile(fileDto);
    }

    @PutMapping
    public void updateStatus(@RequestBody FileDto fileDto) {
        statusProcessorService.updateStatus(fileDto);
    }

}
