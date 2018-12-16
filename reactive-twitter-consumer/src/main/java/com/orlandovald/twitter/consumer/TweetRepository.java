package com.orlandovald.twitter.consumer;

import com.orlandovald.twitter.consumer.domain.Tweet;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface TweetRepository extends ReactiveCrudRepository<Tweet, Long> {
}
