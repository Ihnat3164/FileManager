package org.storageservice.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.storageservice.model.TextFile;

import java.util.Optional;

@Repository
public interface FileRepository extends CrudRepository<TextFile, String> {
     Optional<TextFile> findTextFileById(String id);
}
