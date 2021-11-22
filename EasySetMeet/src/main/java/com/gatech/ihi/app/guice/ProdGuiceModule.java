package com.gatech.ihi.app.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import javax.inject.Named;

public class ProdGuiceModule extends AbstractModule {
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
        // We are not using database, just need some hostname
        return "http://dynamodb.lax.mx:8000";
    }
}
