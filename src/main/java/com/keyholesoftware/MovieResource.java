package com.keyholesoftware;

import java.util.List;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/movies")
@Produces(MediaType.APPLICATION_JSON)
public class MovieResource {

    @Inject
    EntityManager em;

    private final MeterRegistry registry;

    MovieResource(MeterRegistry registry) {
        this.registry = registry;
    }

    @GET
    @Path("/nowPlaying")
    public List<Movie> nowPlaying() {
        registry.counter("list.movies").increment();
        List<Movie> movies = em.createQuery("from Movie", Movie.class).getResultList();
        return movies;
    }
}
