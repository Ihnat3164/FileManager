package org.storageservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.storageservice.sevice.FileService;

@RestController
@RequestMapping("/storage/api/")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping
    public ResponseEntity<?> sendFile(@RequestParam(value = "id") String id){
        System.out.println(id);
        return fileService.sendFile(id);
    }
}
