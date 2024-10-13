package org.mainservice.model;

import jakarta.persistence.*;
import lombok.Data;

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
}
