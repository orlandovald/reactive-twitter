package com.orlandovald.twitter.consumer;

import com.orlandovald.twitter.consumer.domain.Tweet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.util.stream.Collectors.joining;

@EnableReactiveMongoRepositories
@EnableConfigurationProperties(TwitterConsumerProperties.class)
@SpringBootApplication
public class ReactiveTwitterConsumerApplication {

    public static Logger log = LoggerFactory.getLogger(ReactiveTwitterConsumerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ReactiveTwitterConsumerApplication.class, args);
    }

    @Bean
    ApplicationRunner twitterStream(WebClient.Builder wcb, OAuth1SignatureUtil oauthUtil, TweetRepository repo) {
        return args -> {
            Assert.isTrue(args.containsOption("track"), "[--track] argument is required");
            String tracks = args.getOptionValues("track").stream().collect(joining(","));
            Assert.hasText(tracks, "[--track] argument must contain at least one value");

            WebClient webClient = wcb
                    .baseUrl("https://stream.twitter.com/1.1")
                    .filter(oauthFilter(oauthUtil))
                    .filter(logRequest())
                    .build();

            Flux<Tweet> tweets = webClient.get().uri(uriBuilder -> uriBuilder.path("/statuses/filter.json")
                    .queryParam("track", tracks)
                    .build())
                    .exchange()
                    .flatMapMany(clientResponse -> clientResponse.bodyToFlux(Tweet.class));

            repo.saveAll(tweets).log().subscribe();
        };
    }

    @Bean
    OAuth1SignatureUtil oAuth1SignatureUtil(TwitterConsumerProperties props) {
        OAuth1Credentials creds = new OAuth1Credentials(props.getApi().getKey(), props.getApi().getSecret(),
                props.getApi().getAccessToken(), props.getApi().getAccessTokenSecret());
        return new OAuth1SignatureUtil(creds);
    }

    private ExchangeFilterFunction oauthFilter(OAuth1SignatureUtil oauthUtil) {
        return ExchangeFilterFunction.ofRequestProcessor(req -> {
            ClientRequest oauthReq = ClientRequest.from(req)
                    .headers(headers -> headers.add(HttpHeaders.AUTHORIZATION, oauthUtil.oAuth1Header(req)))
                    .build();
            return Mono.just(oauthReq);
        });
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(req -> {
            log.info("Request: {} {}", req.method(), req.url());
            req.headers().forEach((name, values) -> values.forEach(value -> log.info("Header: {}={}", name, value)));
            return Mono.just(req);
        });
    }

}
