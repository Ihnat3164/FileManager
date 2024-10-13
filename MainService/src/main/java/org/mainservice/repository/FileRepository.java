package org.mainservice.repository;

import org.mainservice.DTO.FileMetaDTO;
import org.mainservice.model.FileMeta;
import org.mainservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FileMeta,String> {
    @Query("SELECT new org.mainservice.DTO.FileMetaDTO(m.id, m.title,m.type) FROM FileMeta m WHERE m.author = :name")
     List<FileMetaDTO> findFileMetaByName(@Param("name") String name);
     Optional<FileMeta> findFileMetaById(String id);
}
