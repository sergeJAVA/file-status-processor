package com.example.file_status_processor.repository;

import com.example.file_status_processor.constant.FileStatus;
import com.example.file_status_processor.model.File;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends MongoRepository<File, String> {

    Optional<File> findByChecksum(String checksum);

    @Query(value = "{ 'fileStatus': ?0 }")
    List<File> findByFileStatus(FileStatus fileStatus);

}
