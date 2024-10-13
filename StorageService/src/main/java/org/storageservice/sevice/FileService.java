package org.storageservice.sevice;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.storageservice.model.TextFile;
import org.storageservice.repository.FileRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;

    public void putFile(TextFile textFile){
        fileRepository.save(textFile);
    }

    public ResponseEntity<?> sendFile(String id){
        Optional<TextFile> file = fileRepository.findTextFileById(id);
        return ResponseEntity.ok(file);
    }

    public void updateFile(TextFile editedTextFile){
        Optional<TextFile> opTextFile = fileRepository.findTextFileById(editedTextFile.getId());
        TextFile existingTextFile = opTextFile.get();
        existingTextFile.setContent(editedTextFile.getContent());
        fileRepository.save(existingTextFile);
    }
}
