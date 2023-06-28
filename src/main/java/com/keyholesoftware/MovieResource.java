package com.keyholesoftware;

import java.util.List;

import org.eclipse.microprofile.faulttolerance.Fallback;
import org.jboss.logging.Logger;

import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.cache.CacheResult;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/movies")
@Produces(MediaType.APPLICATION_JSON)
public class MovieResource {

    private static final Logger LOGGER = Logger.getLogger(MovieResource.class);

    @Inject
    EntityManager em;

    private final MeterRegistry meterRegistry;

    MovieResource(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @GET
    @Path("/nowPlaying")
    @RolesAllowed("user")
    @Fallback(fallbackMethod = "nowPlayingFallback")
    @CacheResult(cacheName = "nowPlayingCache")
    public List<Movie> nowPlaying() {
        meterRegistry.counter("nowPlaying.invocations").increment();
        List<Movie> movies = em.createQuery("from Movie", Movie.class).getResultList();
        LOGGER.info("MovieResource>>nowPlaying - fetched movies");
        return movies;
    }

    List<Movie> nowPlayingFallback() {
        return List.of();
    }
}
