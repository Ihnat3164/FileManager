package org.storageservice.repository;

import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.storageservice.model.TextFileMongodb;

import java.util.Optional;

@Repository
public interface FileRepositoryMongodb extends CrudRepository<TextFileMongodb, String> {
     Optional<TextFileMongodb> findTextFileById(String id);

     @Override
     void deleteById(@NonNull String id);
}
