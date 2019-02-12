package com.orlandovald.twitterapi;

import com.orlandovald.twitterapi.domain.Tweet;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import reactor.core.publisher.Flux;

public interface TweetRepository extends ReactiveMongoRepository<Tweet, String> {

    @Tailable
    Flux<Tweet> findWithTailableCursorBy();

}
