package com.chyronis.literature.literature.mongo;

import com.chyronis.literature.literature.model.LiteratureItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LiteratureItemRepository extends MongoRepository<LiteratureItem, String> {
}
