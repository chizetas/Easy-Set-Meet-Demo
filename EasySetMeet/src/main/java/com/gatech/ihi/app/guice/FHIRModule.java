package com.gatech.ihi.app.guice;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.gatech.ihi.app.facade.FHIRFacade;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import javax.inject.Named;
import javax.inject.Singleton;

public class FHIRModule extends AbstractModule {
    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    public IGenericClient providesFHIRClient(@Named("FHIREndpoint") final String baseUrl) {
        final FhirContext ctx = FhirContext.forDstu3();
        return ctx.newRestfulGenericClient(baseUrl);
    }

    @Provides
    @Named("FHIREndpoint")
    public String provideFHIREndpoint() {
        return "http://hapi.fhir.org/baseDstu3";
    }

    @Provides
    @Singleton
    public FHIRFacade provideFHIRFacade(final IGenericClient fhirClient) {
        return new FHIRFacade(fhirClient);
    }
}
