package com.gatech.ihi.app.guice;

import com.gatech.ihi.app.facade.DataFacade;
import com.gatech.ihi.app.facade.FHIRFacade;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class DataFacadeModule extends AbstractModule {
    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    public DataFacade provideDataFacade(final FHIRFacade fhirFacade) {
        return new DataFacade(fhirFacade);
    }
}
