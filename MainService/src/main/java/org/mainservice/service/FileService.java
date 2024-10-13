package org.mainservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.mainservice.DTO.FileMetaDTO;
import org.mainservice.DTO.TextFileDTO;
import org.mainservice.exception.InvalidFileTypeException;
import org.mainservice.exception.ObjectNotFoundException;
import org.mainservice.kafka.KafkaProducer;
import org.mainservice.model.FileMeta;
import org.mainservice.model.User;
import org.mainservice.repository.FileRepository;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final UserService userService;
    private final KafkaProducer kafkaProducer;
    private final RestTemplate restTemplate;

    public ResponseEntity<?> putFile(MultipartFile file, Principal principal) throws IOException {
        FileMeta fileMeta = new FileMeta();
        Optional<User> user = userService.findUserByEmail(principal.getName());
        fileMeta.setId(UUID.randomUUID().toString());
        fileMeta.setTitle(file.getOriginalFilename());
        fileMeta.setType(file.getContentType());
        fileMeta.setAuthor(user.get().getEmail());

        String directoryPath = "C:\\OTHERS\\unik\\Java\\temp\\ProjectFiles"; // enter your path

        if (fileMeta.getType() != null && fileMeta.getType().startsWith("text/")) {
            fileMeta.setPath("mongodb");
            processTextFile(file,fileMeta.getId());
        } else {
            fileMeta.setPath(directoryPath + "\\" + file.getOriginalFilename());
            processOtherFile(file, directoryPath);
        }
        return ResponseEntity.ok(fileRepository.save(fileMeta));
    }

    private void processTextFile(MultipartFile file, String id) throws IOException {
        TextFileDTO textFileDTO = new TextFileDTO();
        String content = new String(file.getBytes(), StandardCharsets.UTF_8);

        textFileDTO.setId(id);
        textFileDTO.setContent(content);

        ObjectMapper objectMapper = new ObjectMapper();
        kafkaProducer.sendMessage(objectMapper.writeValueAsString(textFileDTO));
    }

    private void processOtherFile(MultipartFile file, String directoryPath) throws IOException {

        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String filePath = directoryPath + "\\" + file.getOriginalFilename();
        file.transferTo(new File(filePath));
    }

    public List<FileMetaDTO> getFilesList(Principal principal){
        return fileRepository.findFileMetaByName(principal.getName());
    }

    public TextFileDTO getFileContent(String id, Principal principal){

        if(userOwnsFile(id, principal)){
            throw new ObjectNotFoundException("File not found");
        }
        if(!fileRepository.findFileMetaById(id).get().getType().startsWith("text/")){
            throw new InvalidFileTypeException("File should be text for this option");
        }
        return getText(id);
    }

    public ResponseEntity<?> getFileForDownload(String id,Principal principal) throws IOException {
        if(userOwnsFile(id, principal)){
            throw new ObjectNotFoundException("File not found");
        }

        Optional<FileMeta> fileMeta = fileRepository.findFileMetaById(id);

        if(fileMeta.get().getPath().equals("mongodb")){
            return getTextFile(id,fileMeta);
        }
        else{
            return getOtherFile(id,fileMeta);
        }

    }

    public TextFileDTO getText(String id){

        TextFileDTO textFileDTO;
        String url = UriComponentsBuilder.fromHttpUrl("http://localhost:8081/storage/api/?")
                .queryParam("id", id)
                .toUriString();

        return textFileDTO = restTemplate.getForObject(url, TextFileDTO.class);
    }

    private ResponseEntity<?> getTextFile(String id, Optional<FileMeta> fileMeta) {

        TextFileDTO textFileDTO = getText(id);
        byte[] contentBytes = textFileDTO.getContent().getBytes(StandardCharsets.UTF_8);
        ByteArrayResource resource = new ByteArrayResource(contentBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=\"" + fileMeta.get().getTitle() + "\"");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);  // Универсальный тип для скачивания

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(contentBytes.length)
                .body(resource);
    }

    private ResponseEntity<?> getOtherFile(String id, Optional<FileMeta> fileMeta) throws IOException {
        Path path = Paths.get(fileMeta.get().getPath());
        byte[] fileBytes = Files.readAllBytes(path);
        ByteArrayResource resource = new ByteArrayResource(fileBytes);

        HttpHeaders headers = new HttpHeaders();

        headers.add("Content-Disposition", "attachment; filename=\"" + fileMeta.get().getTitle() + "\"");

        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(fileBytes.length)
                .body(resource);
    }

    public ResponseEntity<?> editFile(String id, String content, Principal principal) throws JsonProcessingException {
        if(userOwnsFile(id, principal)){
            throw new ObjectNotFoundException("File not found");
        }
        if(!fileRepository.findFileMetaById(id).get().getType().startsWith("text/")){
            throw new InvalidFileTypeException("File should be text for this option");
        }

        TextFileDTO textFileDTO = new TextFileDTO(id,content);
        ObjectMapper objectMapper = new ObjectMapper();
        kafkaProducer.updateText(objectMapper.writeValueAsString(textFileDTO));
        return ResponseEntity.ok("File updated");
    }

    private boolean userOwnsFile(String id, Principal principal) {
        List<FileMetaDTO> userFiles = fileRepository.findFileMetaByName(principal.getName());
        return userFiles.stream().noneMatch(file -> file.getId().equals(id));
    }

}
