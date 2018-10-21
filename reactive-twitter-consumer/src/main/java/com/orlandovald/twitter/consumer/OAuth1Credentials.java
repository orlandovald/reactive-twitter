package com.orlandovald.twitter.consumer;

class OAuth1Credentials {

    private final String consumerKey;
    private final String consumerSecret;
    private final String accessToken;
    private final String accessTokenSecret;

    OAuth1Credentials(String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.accessToken = accessToken;
        this.accessTokenSecret = accessTokenSecret;
    }

    String getConsumerKey() {
        return consumerKey;
    }

    String getConsumerSecret() {
        return consumerSecret;
    }

    String getAccessToken() {
        return accessToken;
    }

    String getAccessTokenSecret() {
        return accessTokenSecret;
    }

    @Override
    public String toString() {
        return "OAuth1Credentials{" +
                "consumerKey='" + consumerKey + '\'' +
                ", consumerSecret='" + consumerSecret + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", accessTokenSecret='" + accessTokenSecret + '\'' +
                '}';
    }
}
