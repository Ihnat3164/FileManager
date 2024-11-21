package org.storageservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.storageservice.service.FileService;


@RestController
@RequestMapping("/storage/api/")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping
    public ResponseEntity<?> sendFileById(@RequestParam(value = "id") String id){
        System.out.println(id);
        return ResponseEntity.ok(fileService.getFile(id));
    }

    @GetMapping("/search/")
    public ResponseEntity<?> sendFileByContent(@RequestParam(value= "content") String content) {
        return ResponseEntity.ok(fileService.findFileByContent(content));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteFileById(@RequestParam String id){
        return ResponseEntity.ok(fileService.deleteFileById(id));
    }
}
