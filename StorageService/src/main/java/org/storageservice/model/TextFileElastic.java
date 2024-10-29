package org.storageservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "files")
public class TextFileElastic {
    @Id
    private String id;
    @Field(type = FieldType.Text, analyzer = "standard", searchAnalyzer = "standard")
    private String content;
}
