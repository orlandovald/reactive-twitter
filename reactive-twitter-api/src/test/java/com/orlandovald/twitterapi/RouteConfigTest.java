package com.orlandovald.twitterapi;

import com.orlandovald.twitterapi.domain.Tweet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@WebFluxTest({RouteConfig.class})
@RunWith(SpringRunner.class)
public class RouteConfigTest {

    @Autowired
    WebTestClient client;

    @MockBean
    private TweetRepository repo;

    private List<Tweet> tweets;

    @Before
    public void setUp() throws Exception {
        tweets = IntStream.range(1, 4)
                .mapToObj(i -> {
                    Tweet tweet = new Tweet();
                    tweet.setId(UUID.randomUUID().toString());
                    tweet.setText("Tweet with id " + tweet.getId());
                    return tweet;
                })
                .collect(Collectors.toList());

        tweets.forEach(tweet -> Mockito.when(repo.findById(tweet.getId())).thenReturn(Mono.just(tweet)));
        Mockito.when(repo.findWithTailableCursorBy()).thenReturn(Flux.fromIterable(tweets));
    }

    @Test
    public void getAll() {
        StepVerifier.create(client.get()
                .uri("/tweets")
                .exchange()
                .expectStatus().isOk()
                .returnResult(Tweet.class)
                .getResponseBody())
                .expectNextMatches(tweet -> tweet.getId().equals(tweets.get(0).getId()))
                .expectNextMatches(tweet -> tweet.getId().equals(tweets.get(1).getId()))
                .expectNextMatches(tweet -> tweet.getId().equals(tweets.get(2).getId()))
                .verifyComplete();
    }

    @Test
    public void findById() {
        StepVerifier.create(client.get()
                .uri("/tweets/{id}", tweets.get(1).getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .returnResult(Tweet.class)
                .getResponseBody())
                .expectNextMatches(tweet -> tweet.getId().equals(tweets.get(1).getId()))
                .verifyComplete();
    }
}