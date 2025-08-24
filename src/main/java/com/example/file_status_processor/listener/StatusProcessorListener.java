package com.example.file_status_processor.listener;

import com.example.file_status_processor.dto.FileDto;
import com.example.file_status_processor.service.StatusProcessorService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatusProcessorListener {

    private final ObjectMapper objectMapper;
    private final StatusProcessorService statusProcessorService;

    @KafkaListener(topics = "status", groupId = "status-processor-id")
    public void handle(ConsumerRecord<String, String> consumerRecord) throws JsonProcessingException {
        statusProcessorService.updateStatus(objectMapper.readValue(consumerRecord.value(), FileDto.class));
    }

}
