package com.starburstdata.presto.configuration.logging;

import com.google.inject.Binder;
import io.airlift.configuration.AbstractConfigurationAwareModule;

import static io.airlift.jaxrs.JaxrsBinder.jaxrsBinder;

public class LoggingModule
        extends AbstractConfigurationAwareModule
{
    public LoggingModule()
    {}

    @Override
    protected void setup(Binder binder)
    {
        jaxrsBinder(binder).bind(LoggingHandler.class);
    }
}
