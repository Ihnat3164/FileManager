package org.mainservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.mainservice.service.FileService;
import org.mainservice.service.HttpService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
@Tag(name = "File Management", description = "Controller for managing file operations such as upload, download, update, and retrieval of user files.")
public class FileController {

    private final FileService fileService;
    private final HttpService httpService;

    @GetMapping
    @Operation(summary = "Get user files", description = "Retrieves the list of files belonging to the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of files successfully retrieved. If no files are found, an empty list [] will be returned"),
    })
    public ResponseEntity<?> getUserFiles(Principal principal) {
        return ResponseEntity.ok(fileService.getFilesList(principal));
    }

    @GetMapping("/content")
    @Operation(summary = "Get file content", description = "Fetches the content of a text file by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File content successfully retrieved."),
            @ApiResponse(responseCode = "404", description = "File not found."),
            @ApiResponse(responseCode = "400", description = "Invalid file type.")
    })
    public ResponseEntity<?> getFileContent(@Parameter(name = "id", description = "ID of the file to retrieve content from.", required = true, example = "12345")
                                            @RequestParam(value = "id") String id, Principal principal) {
        return ResponseEntity.ok(httpService.getTextFileById(id, principal).getContent());
    }

    @PatchMapping("/update")
    @Operation(summary = "Update file content", description = "Updates the content of an existing text file by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File content successfully updated."),
            @ApiResponse(responseCode = "404", description = "File not found."),
            @ApiResponse(responseCode = "400", description = "Invalid file type.")
    })
    public ResponseEntity<?> updateFileContent(@Parameter(name = "id", description = "ID of the file to be updated.", required = true, example = "12345")
                                               @RequestParam(value = "id") String id,
                                               @Parameter(name = "content", description = "New content to update in the file.", required = true, example = "Updated file content")
                                               @RequestBody String content, Principal principal) throws JsonProcessingException {
        fileService.editFile(id, content, principal);
        return ResponseEntity.ok("File updated");
    }

    @GetMapping("/download")
    @Operation(summary = "Download file", description = "Downloads the file specified by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File successfully downloaded."),
            @ApiResponse(responseCode = "404", description = "File not found."),
            @ApiResponse(responseCode = "500", description = "Error occurred during file download.")
    })
    public ResponseEntity<?> downloadFile(@Parameter(name = "id", description = "ID of the file to download.", required = true, example = "12345")
                                          @RequestParam(value = "id") String id, Principal principal) throws IOException {
        return fileService.getFileForDownload(id, principal);
    }

    @PostMapping
    @Operation(summary = "Upload file", description = "Uploads a new file for the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File successfully uploaded."),
            @ApiResponse(responseCode = "500", description = "Error occurred during file upload.")
    })
    public ResponseEntity<?> storeFile(@Parameter(name = "file", description = "File to upload.", required = true)
                                       @RequestParam MultipartFile file, Principal principal) throws IOException {
        return ResponseEntity.ok(fileService.putFile(file, principal));
    }

    @GetMapping("/search")
    @Operation(summary = "Text search", description = "Retrieves the list of user's files according to the given text.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of files successfully retrieved. If no files are found, an empty list [] will be returned"),
    })
    public ResponseEntity<?> searchByText(@Parameter(name = "content", description = "Text for search")@RequestBody String content, Principal principal) {
        return ResponseEntity.ok(httpService.findFileByContent(content, principal));
    }
}
