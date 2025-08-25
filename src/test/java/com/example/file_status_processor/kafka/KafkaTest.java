package com.example.file_status_processor.kafka;

import com.example.file_status_processor.constant.FileStatus;
import com.example.file_status_processor.dto.FileDto;
import com.example.file_status_processor.model.File;
import com.example.file_status_processor.repository.FileRepository;
import com.example.file_status_processor.testcontainer.Testcontainer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest
@ActiveProfiles("test")
public class KafkaTest extends Testcontainer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldUpdateFileStatus_whenMessageConsumed() throws Exception {

        File file = File.builder()
                .fileName("integration.xlsx")
                .fileBytes("data".getBytes())
                .fileStatus(FileStatus.FIRST_VALIDATION_SUCCESS)
                .checksum("integration123")
                .build();
        fileRepository.save(file);

        FileDto updateDto = FileDto.builder()
                .fileName(file.getFileName())
                .fileBytes(file.getFileBytes())
                .fileStatus(FileStatus.SECOND_VALIDATION_SUCCESS)
                .checksum(file.getChecksum())
                .build();

        kafkaTemplate.send("status", objectMapper.writeValueAsString(updateDto));

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            File updated = fileRepository.findByChecksum("integration123").orElseThrow();
            assertThat(updated.getFileStatus()).isEqualTo(FileStatus.SECOND_VALIDATION_SUCCESS);
        });

    }

}
