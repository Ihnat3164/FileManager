package org.storageservice.repository;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.storageservice.model.TextFileElastic;

import java.util.List;

@Repository
@EnableElasticsearchRepositories
public interface FileRepositoryElastic extends ElasticsearchRepository<TextFileElastic,String>, CrudRepository<TextFileElastic,String> {

    @Query("{\"match\": {\"content\": \"?0\"}}")
    List<TextFileElastic> searchByContent(String content);

}
