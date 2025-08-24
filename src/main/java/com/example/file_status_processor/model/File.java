package com.example.file_status_processor.model;

import com.example.file_status_processor.constant.FileStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "files")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class File {

    @Id
    private String id;

    private String fileName;

    private byte[] fileBytes;

    private FileStatus fileStatus;

    private String checksum;

}
