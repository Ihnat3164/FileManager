package org.mainservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.mainservice.DTO.FileMetaDTO;
import org.mainservice.DTO.TextFileDTO;
import org.mainservice.exception.InvalidFileTypeException;
import org.mainservice.exception.ObjectNotFoundException;
import org.mainservice.kafka.KafkaProducer;
import org.mainservice.model.FileMeta;
import org.mainservice.repository.FileRepository;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final KafkaProducer kafkaProducer;
    private final HttpService httpService;

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public FileMeta putFile(MultipartFile file, Principal principal) throws IOException {
        FileMeta fileMeta = new FileMeta();
        fileMeta.setTitle(file.getOriginalFilename());
        fileMeta.setType(file.getContentType());
        fileMeta.setAuthor(principal.getName());

        String directoryPath = "C:\\OTHERS\\unik\\Java\\temp\\ProjectFiles";

        if (fileMeta.getType() != null && fileMeta.getType().startsWith("text/")) {
            fileMeta.setPath("mongodb");
            processTextFile(file, fileMeta.getId());
        } else {
            String filePath = Paths.get(directoryPath, file.getOriginalFilename()).toString();
            fileMeta.setPath(filePath);
            processMediaFile(file, directoryPath);
        }
        return fileRepository.save(fileMeta);
    }

    private void processTextFile(MultipartFile file, String id) throws IOException {
        TextFileDTO textFileDTO = new TextFileDTO();
        String content = new String(file.getBytes(), StandardCharsets.UTF_8);
        textFileDTO.setId(id);
        textFileDTO.setContent(content);

        kafkaProducer.sendMessage(textFileDTO);
    }
    
    private void processMediaFile(MultipartFile file, String directoryPath) throws IOException {
        Path directory = Paths.get(directoryPath);
        if (Files.notExists(directory)) {
            Files.createDirectories(directory);
        }

        Path filePath = directory.resolve(Objects.requireNonNull(file.getOriginalFilename()));
        file.transferTo(filePath.toFile());
    }

    public List<FileMetaDTO> getFilesList(Principal principal){
        return fileRepository.findFileMetaByName(principal.getName());
    }

    public ResponseEntity<?> getFileForDownload(String id,Principal principal) throws IOException {
        FileMeta fileMeta = fileRepository.findFileMetaByAuthorAndId(principal.getName(),id)
                .orElseThrow(() -> new ObjectNotFoundException("File with this id not found: " + id));
        if(fileMeta.getPath().equals("mongodb")){
            return getTextFile(id,fileMeta,principal);
        }
        else{
            return getMediaFile(id);
        }
    }

    private ResponseEntity<ByteArrayResource> getTextFile(String id, FileMeta fileMeta, Principal principal) {
        TextFileDTO textFileDTO = httpService.getTextFileById(id, principal);
        byte[] contentBytes = textFileDTO.getContent().getBytes(StandardCharsets.UTF_8);
        ByteArrayResource resource = new ByteArrayResource(contentBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=\"" + fileMeta.getTitle() + "\"");
        headers.setContentType(MediaType.TEXT_PLAIN);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(contentBytes.length)
                .body(resource);
    }

    private ResponseEntity<ByteArrayResource> getMediaFile(String id) throws IOException {
        FileMeta fileMeta = fileRepository.findFileMetaById(id)
                .orElseThrow(() -> new ObjectNotFoundException("File with this id not found: " + id));

        Path path = Paths.get(fileMeta.getPath());
        byte[] fileBytes = Files.readAllBytes(path);
        ByteArrayResource resource = new ByteArrayResource(fileBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=\"" + fileMeta.getTitle() + "\"");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(fileBytes.length)
                .body(resource);
    }

    public void editFile(String id, String content, Principal principal) throws JsonProcessingException {
        FileMeta fileMeta = fileRepository.findFileMetaByAuthorAndId(principal.getName(),id).orElseThrow(() -> new ObjectNotFoundException("File not found with id: "+ id));
        if (!fileMeta.getType().startsWith("text/")) {
            throw new InvalidFileTypeException("File's type: " + fileMeta.getType() + ", but file have to be text.");
        }
        TextFileDTO textFileDTO = new TextFileDTO(id, content);
        kafkaProducer.updateText(textFileDTO);
    }

    public String deleteFileById(String id, String email){
        FileMeta fileMeta = fileRepository.findFileMetaByAuthorAndId(email, id)
                .orElseThrow(() -> new ObjectNotFoundException("File not found with id: " + id));
        fileRepository.deleteById(id);

        if ("mongodb".equals(fileMeta.getPath())) {
            return httpService.deleteFileById(id);
        }

        Path path = Paths.get("C:\\OTHERS\\unik\\Java\\temp\\ProjectFiles\\" + fileMeta.getTitle());
        try {
            if (Files.exists(path)) {
                Files.delete(path);
                return "File deleted successfully from local storage.";
            } else {
                return "Local file not found.";
            }
        } catch (IOException e) {
            return "Error deleting file: " + e.getMessage();

    }
    }

}
