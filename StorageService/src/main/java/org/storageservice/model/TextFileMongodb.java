package org.storageservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "files" )
public class TextFileMongodb {
    @Id
    private String id;
    private String content;
}
