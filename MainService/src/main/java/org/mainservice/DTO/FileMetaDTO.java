package org.mainservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class FileMetaDTO {
    private String id;
    private String title;
    private String type;
}
