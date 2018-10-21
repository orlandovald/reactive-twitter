package com.orlandovald.twitter.consumer;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("ov")
public class TwitterConsumerProperties {

    private TwitterApi api;

    public TwitterApi getApi() {
        return api;
    }

    public void setApi(TwitterApi api) {
        this.api = api;
    }

    static class TwitterApi {

        /**
         * Twitter API Consumer Key
         */
        private String key;

        /**
         * Twitter API Consumer Secret
         */
        private String secret;

        /**
         * Twitter API Access Token
         */
        private String accessToken;

        /**
         * Twitter API Access Token Secret
         */
        private String accessTokenSecret;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getAccessTokenSecret() {
            return accessTokenSecret;
        }

        public void setAccessTokenSecret(String accessTokenSecret) {
            this.accessTokenSecret = accessTokenSecret;
        }
    }
}
