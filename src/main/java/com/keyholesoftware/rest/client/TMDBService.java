package com.keyholesoftware.rest.client;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.quarkus.rest.client.reactive.ClientQueryParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@RegisterRestClient(configKey = "tmdb")
@ClientQueryParam(name = "api_key", value = "${tmdb.apiKey}")
public interface TMDBService {

    @GET
    @Path("/now_playing")
    @Retry(maxRetries = 2)
    @Timeout
    @CircuitBreaker
    NowPlaying nowPlaying();
}
