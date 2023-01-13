package com.starburstdata.presto;

import com.google.common.collect.ImmutableList;
import com.google.inject.Module;
import com.starburstdata.presto.configuration.logging.LoggingModule;
import com.starburstdata.presto.log.CloudWatchLogger;
import com.starburstdata.presto.server.StarburstServerExtensionsModule;
import com.starburstdata.presto.telemetry.MemoryHandler;
import io.airlift.log.Logging;
import io.trino.server.Server;

import java.util.List;
import java.util.logging.Logger;

import static com.google.common.base.MoreObjects.firstNonNull;

public class StarburstTrinoServerEx
        extends Server
{
    public static void main(String[] args)
    {
        // hold a reference to the logger so the handler doesn't get removed if the logger gets garbage collected
        Logger bootstrapLogger = Logger.getLogger("Bootstrap");
        try {
            // explicitly initialize logging so that we can install our logs handler to capture bootstrapping log output
            Logging.initialize();
            CloudWatchLogger.initialize();
            // create a MemoryHandler so that bootstrapping log output can be processed later
            bootstrapLogger.addHandler(new MemoryHandler());
        }
        catch (Throwable t) {
            t.printStackTrace(System.err);
            System.exit(1);
        }

        String version = StarburstTrinoServerEx.class.getPackage().getImplementationVersion();
        new StarburstTrinoServerEx().start(firstNonNull(version, "unknown"));
    }

    @Override
    public List<Module> getAdditionalModules()
    {
        // StarburstServerExtensionsModule should be the only module here, ensuring that
        // Starburst connectors' integration tests (non-product tests) are run along with
        // Starburst extensions to the Trino server.
        //return List.of(new StarburstServerExtensionsModule());

        ImmutableList.Builder<Module> modules = ImmutableList.builder();

        modules.add(new StarburstServerExtensionsModule());
        modules.add(new LoggingModule());

        return modules.build();
    }
}
