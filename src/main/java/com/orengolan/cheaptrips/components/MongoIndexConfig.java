package com.orengolan.cheaptrips.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.Optional;

/**
 * The {@code MongoIndexConfig} class is a Spring component responsible for configuring and initializing TTL (Time-to-Live)
 * indexes for MongoDB collections used in the CheapTrips application. It utilizes Spring Data MongoDB to interact with
 * the MongoDB database and ensures that specific collections have TTL indexes to manage data expiration.
 *
 * The initialization process is triggered during the application startup through the {@code @PostConstruct} annotated
 * method, {@code initIndexes()}. It creates TTL indexes for collections such as "airline," "airports," "cities,"
 * "countries," "news," "opentripmap," and "userinfo." These TTL indexes enable automatic expiration of documents
 * after a specified duration, enhancing data management and cleanup.
 *
 * Additionally, the class defines a unique index on the "email" field of the "userinfo" collection, ensuring the
 * uniqueness of email entries in the collection.
 *
 * Usage Example:
 * <pre>
 * {@code
 * // During application startup, the MongoIndexConfig component initializes TTL indexes for MongoDB collections,
 * // facilitating the automatic expiration of documents after predefined durations.
 * }
 * </pre>
 *
 * This {@code MongoIndexConfig} class plays a vital role in maintaining the integrity and performance of the MongoDB
 * collections in the CheapTrips application by applying TTL indexes and ensuring the uniqueness of email entries.
 */
@Component
@Order(5)
public class MongoIndexConfig {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public MongoIndexConfig(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @PostConstruct
    public void initIndexes() {

        createTTLIndex("airline", 364*24*60*60);        // 1 year: 365 days * 24 hours * 60 minutes * 60 seconds
        createTTLIndex("airports", 364*24*60*60);       // 1 year: 365 days * 24 hours * 60 minutes * 60 seconds
        createTTLIndex("cities", 364*24*60*60);         // 1 year: 365 days * 24 hours * 60 minutes * 60 seconds
        createTTLIndex("countries", 364*24*60*60);      // 1 year: 365 days * 24 hours * 60 minutes * 60 seconds
        createTTLIndex("news", 24*60*60);               // 24 hours * 60 minutes * 60 seconds
        createTTLIndex("opentripmap", 364*24*60*60);    // 1 year: 365 days * 24 hours * 60 minutes * 60 seconds
        createTTLIndex("userinfo", 7*24*60*60);         // 3 days * 24 hours * 60 minutes * 60 seconds
        mongoTemplate.indexOps("userinfo").ensureIndex(new Index().on("email", Sort.Direction.ASC).unique());
    }

    private void createTTLIndex(String collectionName, int expirationSeconds) {
        IndexOperations indexOperations = mongoTemplate.indexOps(collectionName);
        Optional<IndexInfo> indexInfo = indexOperations.getIndexInfo().stream().filter(i->i.getName().contains("expireAt_")).findFirst();
        if(indexInfo.isPresent()) {
            indexOperations.dropIndex(indexInfo.get().getName());
        }
        mongoTemplate.indexOps(collectionName).ensureIndex(new Index().on("expireAt", Sort.Direction.ASC).expire(expirationSeconds));
    }
}
