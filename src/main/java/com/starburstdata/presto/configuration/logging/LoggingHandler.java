package com.starburstdata.presto.configuration.logging;

import com.google.inject.Inject;
import io.airlift.log.Level;
import io.airlift.log.Logging;
import io.trino.server.security.ResourceSecurity;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.Locale;

import static io.trino.server.security.ResourceSecurity.AccessType.AUTHENTICATED_USER;

@Path("/ex/logging")
public class LoggingHandler
{
    @Inject
    public LoggingHandler()
    {}

    @ResourceSecurity(AUTHENTICATED_USER)
    @GET
    @Path("/level/{name}")
    public Response getLevel(@PathParam("name") String n)
    {
        if (n == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return Response.ok(Logging.initialize().getLevel(n).name()).build();
    }

    @ResourceSecurity(AUTHENTICATED_USER)
    @POST
    @Path("/level/{name}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response setLevel(@PathParam("name") String n, @FormParam("level") String l)
    {
        // Sanity checks
        if (n == null || l == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        try {
            Logging.initialize().setLevel(n, Level.valueOf(l.toUpperCase(Locale.US)));
        } catch (IllegalArgumentException ex) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return Response.ok().build();
    }

    @ResourceSecurity(AUTHENTICATED_USER)
    @GET
    @Path("/level/root")
    public Response getRootLevel()
    {
        return Response.ok(Logging.initialize().getRootLevel().name()).build();
    }

    @ResourceSecurity(AUTHENTICATED_USER)
    @POST
    @Path("/level/root")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response setRootLevel(@FormParam("level") String l)
    {
        // Sanity checks
        if (l == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        try {
            Logging.initialize().setRootLevel(Level.valueOf(l.toUpperCase(Locale.US)));
        } catch (IllegalArgumentException ex) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Logging.initialize().setRootLevel(Level.valueOf(l.toUpperCase(Locale.US)));

        return Response.ok().build();
    }
}
