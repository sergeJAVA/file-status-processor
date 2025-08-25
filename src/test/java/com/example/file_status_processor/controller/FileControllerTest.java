package com.example.file_status_processor.controller;

import com.example.file_status_processor.constant.FileStatus;
import com.example.file_status_processor.dto.FileDto;
import com.example.file_status_processor.model.File;
import com.example.file_status_processor.repository.FileRepository;
import com.example.file_status_processor.testcontainer.Testcontainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class FileControllerTest extends Testcontainer {

    @Autowired
    private MockMvc mockMvc;

    @MockitoSpyBean
    private FileRepository fileRepository;

    private ObjectMapper objectMapper;

    private String checksum;
    private File file;
    private FileDto saveFile;
    private String checksumSave;

    @BeforeEach
    void setUp() {

        objectMapper = new ObjectMapper();

        checksum = "123456";
        checksumSave = "31234";

        file = File.builder()
                .fileName("test.xls")
                .fileBytes("content".getBytes())
                .fileStatus(FileStatus.FILE_ACCEPTED)
                .checksum(checksum)
                .build();

        saveFile = FileDto.builder()
                .fileName("save.xls")
                .fileBytes("content".getBytes())
                .fileStatus(FileStatus.FILE_ACCEPTED)
                .checksum(checksumSave)
                .build();
    }

    @AfterEach
    void afterEach() {
        fileRepository.deleteAll();
    }


    @Test
    void checkStatus_Success() throws Exception {
        fileRepository.save(file);

        mockMvc.perform(get("/status").param("checksum", checksum))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value("test.xls"))
                .andExpect(jsonPath("$.fileStatus").value("FILE_ACCEPTED"));

    }

    @Test
    void checkStatus_ShouldReturnNull() throws Exception {

        mockMvc.perform(get("/status").param("checksum", "notRealChecksum"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    void checkStatus_ShouldSave() throws Exception {

        Optional<File> findFile = fileRepository.findByChecksum(checksumSave);
        assertThat(findFile.isEmpty());

        mockMvc.perform(post("/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(saveFile)))
                .andExpect(status().isOk());

        Optional<File> savedFile = fileRepository.findByChecksum(checksumSave);
        assertThat(savedFile.isPresent());
        assertEquals(saveFile.getFileName(), savedFile.get().getFileName());
    }

    @Test
    void checkStatus_ShouldNotSave_FileAlreadyExist() throws Exception {
        FileDto fileDto = FileDto.builder()
                .fileName(file.getFileName())
                .fileBytes(file.getFileBytes())
                .fileStatus(file.getFileStatus())
                .checksum(checksum)
                .build();

        fileRepository.save(file);
        verify(fileRepository, times(1)).save(any());
        Optional<File> findFile = fileRepository.findByChecksum(checksum);
        assertThat(findFile.isPresent());

        mockMvc.perform(post("/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fileDto)))
                .andExpect(status().isOk());
        verify(fileRepository, times(1)).save(any());

    }

}