package org.mainservice.service;

import lombok.RequiredArgsConstructor;
import org.mainservice.DTO.TextFileDTO;
import org.mainservice.exception.InvalidFileTypeException;
import org.mainservice.exception.ObjectNotFoundException;
import org.mainservice.model.FileMeta;
import org.mainservice.repository.FileRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestTemplate;


import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HttpService {

    private final FileRepository fileRepository;
    private final RestTemplate restTemplate;

    public TextFileDTO getTextFileById(String id, Principal principal){
        FileMeta fileMeta = fileRepository.findFileMetaByAuthorAndId(principal.getName(),id)
                .orElseThrow(() -> new ObjectNotFoundException("File not found"));

        if (!fileMeta.getType().startsWith("text/")) {
            throw new InvalidFileTypeException("File should be text for this option");
        }
        String url = UriComponentsBuilder.fromHttpUrl("http://localhost:8081/storage/api/?")
                .queryParam("id", id)
                .toUriString();

        return restTemplate.getForObject(url, TextFileDTO.class);
    }

    public List<TextFileDTO> findFileByContent(String content, Principal principal) {

        String url = UriComponentsBuilder.fromHttpUrl("http://localhost:8081/storage/api/search/?")
                .queryParam("content", content)
                .toUriString();

        List<TextFileDTO> files = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TextFileDTO>>() {}
        ).getBody();
        Set<String>  usersFiles = fileRepository.findFileMetaByAuthor(principal.getName());
        assert files != null;
        return files.stream()
                .filter(file -> usersFiles.contains(file.getId()))
                .collect(Collectors.toList());
    }
}

