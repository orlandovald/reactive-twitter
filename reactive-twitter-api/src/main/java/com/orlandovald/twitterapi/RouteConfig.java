package com.orlandovald.twitterapi;

import com.orlandovald.twitterapi.domain.Tweet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouteConfig {

    private final TweetRepository repo;

    public RouteConfig(TweetRepository repo) {
        this.repo = repo;
    }

    @Bean
    RouterFunction<ServerResponse> tweetRoutes() {
        return route(GET("/tweets"), this::allTweets)
                .andRoute(GET("/tweets/{id}"), this::byId);
    }

    private Mono<ServerResponse> byId(ServerRequest req) {
        return ServerResponse.ok()
                .body(repo.findById(req.pathVariable("id")), Tweet.class);
    }

    private Mono<ServerResponse> allTweets(ServerRequest req) {
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(repo.findWithTailableCursorBy(), Tweet.class);
    }

}
