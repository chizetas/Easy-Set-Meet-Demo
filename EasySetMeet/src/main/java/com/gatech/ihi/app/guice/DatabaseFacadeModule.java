package com.gatech.ihi.app.guice;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.gatech.ihi.app.facade.DatabaseFacade;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class DatabaseFacadeModule extends AbstractModule {
    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    public DatabaseFacade provideDatabaseFacade(final DynamoDBMapper dynamoDBMapper) {
        return new DatabaseFacade(dynamoDBMapper);
    }
}
