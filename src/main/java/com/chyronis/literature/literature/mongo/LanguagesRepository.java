package com.chyronis.literature.literature.mongo;

import com.chyronis.literature.literature.model.jwresponses.LanguageResponse;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LanguagesRepository extends MongoRepository<LanguageResponse, String> {
}
