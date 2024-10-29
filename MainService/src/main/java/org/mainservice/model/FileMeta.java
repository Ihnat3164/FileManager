package org.mainservice.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "meta-inf")
public class FileMeta {
    @Id
    private String id;
    private String title;
    private String type;
    private String path;
    private String author;


    public FileMeta() {
        this.id = UUID.randomUUID().toString();
    }
}
