package org.storageservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.storageservice.model.TextFile;
import org.storageservice.sevice.FileService;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final FileService fileService;

    @KafkaListener(topics = "textFile", groupId = "my_consumer")
    public void newFileListener(String file) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        TextFile textFile = objectMapper.readValue(file, TextFile.class);
        fileService.putFile(textFile);
    }

    @KafkaListener(topics = "editedFile", groupId = "my_consumer")
    public void editedFileListener(String file) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        TextFile textFile = objectMapper.readValue(file, TextFile.class);
        fileService.updateFile(textFile);
    }
}
