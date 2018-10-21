package com.orlandovald.twitter.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@EnableConfigurationProperties(TwitterConsumerProperties.class)
@SpringBootApplication
public class ReactiveTwitterConsumerApplication {

	public static Logger log = LoggerFactory.getLogger(ReactiveTwitterConsumerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ReactiveTwitterConsumerApplication.class, args);
	}

	@Bean
	CommandLineRunner twitterStream(WebClient.Builder wcb, OAuth1SignatureUtil oauthUtil) {
		return args -> {
			WebClient webClient = wcb
					.baseUrl("https://stream.twitter.com/1.1")
					.filter(oauthFilter(oauthUtil))
					.filter(logRequest())
					.filter(logResponse())
					.build();
			Flux<String> tweets = webClient.get().uri(uriBuilder -> uriBuilder.path("/statuses/filter.json")
					.queryParam("track", "realDonaldTrump")
					.build())
					.exchange().flatMapMany(clientResponse -> clientResponse.bodyToFlux(String.class));
//                    .retrieve().bodyToFlux(String.class);
			tweets.subscribe(System.out::println);

		};
	}

	@Bean
	OAuth1SignatureUtil oAuth1SignatureUtil(TwitterConsumerProperties props) {
		OAuth1Credentials creds = new OAuth1Credentials(props.getApi().getKey(), props.getApi().getSecret(),
				props.getApi().getAccessToken(), props.getApi().getAccessTokenSecret());
		return new OAuth1SignatureUtil(creds);
	}

	private static ExchangeFilterFunction oauthFilter(OAuth1SignatureUtil oauthUtil) {
		return ExchangeFilterFunction.ofRequestProcessor(req -> {
			ClientRequest oauthReq = ClientRequest.from(req)
					.headers(headers -> headers.add(HttpHeaders.AUTHORIZATION, oauthUtil.oAuth1Header(req)))
					.build();
			return Mono.just(oauthReq);
		});
	}

	private static ExchangeFilterFunction logRequest() {
		return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
			log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
			clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
			return Mono.just(clientRequest);
		});
	}

	private static ExchangeFilterFunction logResponse() {
		return ExchangeFilterFunction.ofResponseProcessor(response -> {
			log.info("Response code: {}",response.statusCode().toString());
			response.headers().asHttpHeaders().forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
			return Mono.just(response);
		});
	}

}
