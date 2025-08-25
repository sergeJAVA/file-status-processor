package com.example.file_status_processor.listener;

import com.example.file_status_processor.constant.FileStatus;
import com.example.file_status_processor.dto.FileDto;
import com.example.file_status_processor.service.StatusProcessorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StatusProcessorListenerTest {

    @Mock
    private StatusProcessorService statusProcessorService;

    private ObjectMapper objectMapper;

    private StatusProcessorListener listener;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        listener = new StatusProcessorListener(objectMapper, statusProcessorService);
    }

    @Test
    void handle_ShouldCallUpdateStatus() throws Exception {
        FileDto fileDto = FileDto.builder()
                .fileName("test")
                .fileBytes("content".getBytes())
                .fileStatus(FileStatus.SECOND_VALIDATION_SUCCESS)
                .checksum("abc123")
                .build();

        String json = objectMapper.writeValueAsString(fileDto);
        ConsumerRecord<String, String> record = new ConsumerRecord<>("status", 0, 0, null, json);

        listener.handle(record);

        verify(statusProcessorService).updateStatus(argThat(dto ->
                dto.getChecksum().equals("abc123") &&
                        dto.getFileStatus() == FileStatus.SECOND_VALIDATION_SUCCESS
        ));
    }

}