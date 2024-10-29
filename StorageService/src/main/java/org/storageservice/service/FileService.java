package org.storageservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.storageservice.model.TextFileMongodb;
import org.storageservice.model.TextFileElastic;
import org.storageservice.repository.FileRepositoryMongodb;
import org.storageservice.repository.FileRepositoryElastic;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepositoryMongodb fileRepositoryMongodb;
    private final FileRepositoryElastic fileRepositoryElastic;

    public void putFile(TextFileMongodb textFile){
        TextFileElastic fileElastic = new TextFileElastic();
        fileElastic.setId(textFile.getId());
        fileElastic.setContent(textFile.getContent());

        fileRepositoryElastic.save(fileElastic);
        fileRepositoryMongodb.save(textFile);
    }

    public TextFileMongodb sendFile(String id) {
        return fileRepositoryMongodb.findTextFileById(id)
                .orElse(null); // Вернёт null, если файл не найден
    }

    public void updateFile(TextFileMongodb editedTextFile){
            TextFileMongodb existingMongoFile = fileRepositoryMongodb.findTextFileById(editedTextFile.getId())
                    .orElseThrow(NoSuchElementException::new);

        existingMongoFile.setContent(editedTextFile.getContent());

            TextFileElastic existingElasticFile = fileRepositoryElastic.findById(editedTextFile.getId())
                    .orElse(new TextFileElastic());

        existingElasticFile.setId(editedTextFile.getId());
        existingElasticFile.setContent(editedTextFile.getContent());

            fileRepositoryElastic.save(existingElasticFile);
            fileRepositoryMongodb.save(existingMongoFile);


    }

    public List<TextFileElastic> findFileByContent(String content){
        String decodedContent = URLDecoder.decode(content, StandardCharsets.UTF_8);
        return fileRepositoryElastic.searchByContent(decodedContent);
    }
}
