package com.keyholesoftware;

import java.util.List;
import java.util.Random;
import java.util.Set;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.keyholesoftware.rest.client.NowPlaying;
import com.keyholesoftware.rest.client.TMDBService;

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

    private final MeterRegistry registry;

    MovieResource(MeterRegistry registry) {
        this.registry = registry;
    }

    @GET
    @Path("/nowPlaying")
    @RolesAllowed("user")
    public List<Movie> nowPlaying() {
        registry.counter("list.movies").increment();
        List<Movie> movies = em.createQuery("from Movie", Movie.class).getResultList();
        return movies;
    }

    @Inject
    @RestClient
    TMDBService tmdbService;

    @GET
    @Path("/nowPlaying2")
    @RolesAllowed("user")
    @Fallback(fallbackMethod = "nowPlayingFallback")
    @CacheResult(cacheName = "nowPlayingCache")
    public Set<com.keyholesoftware.rest.client.Movie> nowPlaying2() {

        // maybeFail("MovieResource#nowPlaying2() invocation failed");
        registry.counter("nowPlaying.counter").increment();
        LOGGER.info("MovieResource#nowPlaying2() returning successfully");

        NowPlaying nowPlaying = tmdbService.nowPlaying();
        return nowPlaying.results;
    }

    // private void maybeFail(String failureLogMessage) {
    // if (new Random().nextBoolean()) {
    // LOGGER.error(failureLogMessage);
    // throw new RuntimeException("TMDB API call failed.");
    // }
    // }

    Set<com.keyholesoftware.rest.client.Movie> nowPlayingFallback() {
        return Set.of();
    }
}
