package com.gatech.ihi.app.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import javax.inject.Named;

public class RemoteGuiceModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new DynamoDBModule());
        install(new DatabaseFacadeModule());
        install(new FHIRModule());
        install(new DataFacadeModule());
    }

    @Provides
    @Named("DynamoDBEndpoint")
    public String provideDynamoDBEndpoint() {
        return "https://dynamodb.us-west-2.amazonaws.com";
    }
}
