package com.gatech.ihi.app.guice;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/**
 * Before you run, you need to supply AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY in your environment variable.
 */
public class DynamoDBModule extends AbstractModule {
    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    public AmazonDynamoDB provideDynamoDB(@Named("DynamoDBEndpoint") final String endpoint,
                                          @Named("DynamoDBTables") final List<String> tables) {
        final AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder
                .standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(endpoint, "us-west-2"))
                .build();

        init(amazonDynamoDB, tables);

        return amazonDynamoDB;
    }

    @Provides
    @Singleton
    public DynamoDBMapper provideDynamoDBMapper(final AmazonDynamoDB amazonDynamoDB) {
        return new DynamoDBMapper(amazonDynamoDB);
    }

    private void init(final AmazonDynamoDB amazonDynamoDB, final List<String> tables) {
        createTables(amazonDynamoDB, tables);
    }

    private void createTables(final AmazonDynamoDB amazonDynamoDB,
                              final List<String> tables) {
        for (String table : tables) {
            final CreateTableRequest createTableRequest = getCreateTableRequest(table);
            TableUtils.createTableIfNotExists(amazonDynamoDB, createTableRequest);
        }
    }

    private CreateTableRequest getCreateTableRequest(final String table) {
        final List<AttributeDefinition> attributeDefinitions = new ArrayList<>();
        attributeDefinitions.add(
                new AttributeDefinition().withAttributeName("UserId").withAttributeType("S"));

        final List<KeySchemaElement> keySchema = new ArrayList<>();
        keySchema.add(new KeySchemaElement().withAttributeName("UserId").withKeyType(KeyType.HASH));

        final ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput()
                .withReadCapacityUnits(5L)
                .withWriteCapacityUnits(5L);

        return new CreateTableRequest()
                .withTableName(table)
                .withKeySchema(keySchema)
                .withAttributeDefinitions(attributeDefinitions)
                .withProvisionedThroughput(provisionedThroughput);
    }

    @Provides
    @Named("DynamoDBTables")
    public List<String> provideDynamoDBTables() {
        return ImmutableList.of("UserHistory");
    }
}
