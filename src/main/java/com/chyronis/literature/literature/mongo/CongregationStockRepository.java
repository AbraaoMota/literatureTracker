package com.chyronis.literature.literature.mongo;

import com.chyronis.literature.literature.model.CongLiteratureItem;
import com.chyronis.literature.literature.model.Congregation;
import com.chyronis.literature.literature.model.CongregationStock;
import com.chyronis.literature.literature.model.LiteratureStock;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CongregationStockRepository extends MongoRepository<CongregationStock, String> {

    CongregationStock findByCongregation(String congregation);
}
