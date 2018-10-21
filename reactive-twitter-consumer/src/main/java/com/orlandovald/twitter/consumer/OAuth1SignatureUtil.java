package com.orlandovald.twitter.consumer;

import com.twitter.joauth.Normalizer;
import com.twitter.joauth.OAuthParams;
import com.twitter.joauth.Request;
import com.twitter.joauth.Signer;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.util.UriUtils;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class to generate the OAuth1 Authorization
 */
class OAuth1SignatureUtil {

    private static final String OAUTH1_HEADER_AUTHTYPE = "OAuth ";
    private static final String OAUTH_TOKEN = "oauth_token";
    private static final String OAUTH_CONSUMER_KEY = "oauth_consumer_key";
    private static final String OAUTH_SIGNATURE = "oauth_signature";
    private static final String OAUTH_NONCE = "oauth_nonce";
    private static final String OAUTH_TIMESTAMP = "oauth_timestamp";
    private static final String OAUTH_SIGNATURE_METHOD = "oauth_signature_method";
    private static final String OAUTH_VERSION = "oauth_version";
    private static final String HMAC_SHA1 = "HMAC-SHA1";
    private static final String ONE_DOT_OH = "1.0";

    private final Normalizer normalizer;
    private final Signer signer;
    private final SecureRandom secureRandom;
    private final OAuth1Credentials credentials;

    OAuth1SignatureUtil(OAuth1Credentials credentials) {
        this.credentials = credentials;
        this.normalizer = Normalizer.getStandardNormalizer();
        this.signer = Signer.getStandardSigner();
        this.secureRandom = new SecureRandom();
    }

    String oAuth1Header(ClientRequest request) {
        List<Request.Pair> requestParams = new ArrayList<>();
        for (Map.Entry<String, String> entry : parseQueryString(request.url().getRawQuery()).entrySet()) {
            requestParams.add(new Request.Pair(urlEncode(entry.getKey()), urlEncode(entry.getValue())));
        }

        long timestampSecs = this.generateTimestamp();
        String nonce = this.generateNonce();
        OAuthParams.OAuth1Params oAuth1Params = new OAuthParams.OAuth1Params(
                credentials.getAccessToken(), credentials.getConsumerKey(), nonce, timestampSecs,
                Long.toString(timestampSecs), "", HMAC_SHA1, ONE_DOT_OH);

        URI requestUri = request.url();

        int port = getPort(requestUri);

        String normalized = this.normalizer.normalize(requestUri.getScheme(), requestUri.getHost(), port, request.method().name().toUpperCase(),
                requestUri.getPath(), requestParams, oAuth1Params);

        String signature;
        try {
            signature = this.signer.getString(normalized, credentials.getAccessTokenSecret(), credentials.getConsumerSecret());
        } catch (InvalidKeyException | NoSuchAlgorithmException invalidKeyEx) {
            throw new RuntimeException(invalidKeyEx);
        }

        Map<String, String> oauthHeaders = new HashMap<>();
        oauthHeaders.put(OAUTH_CONSUMER_KEY, this.quoted(credentials.getConsumerKey()));
        oauthHeaders.put(OAUTH_TOKEN, this.quoted(credentials.getAccessToken()));
        oauthHeaders.put(OAUTH_SIGNATURE, this.quoted(signature));
        oauthHeaders.put(OAUTH_SIGNATURE_METHOD, this.quoted(HMAC_SHA1));
        oauthHeaders.put(OAUTH_TIMESTAMP, this.quoted(Long.toString(timestampSecs)));
        oauthHeaders.put(OAUTH_NONCE, this.quoted(nonce));
        oauthHeaders.put(OAUTH_VERSION, this.quoted(ONE_DOT_OH));

        return OAUTH1_HEADER_AUTHTYPE
                + oauthHeaders.entrySet().stream().map(Map.Entry::toString).collect(Collectors.joining(", "));

    }

    private int getPort(URI uri) {
        int port = uri.getPort();

        if (port <= 0) {
            if (uri.getScheme().equalsIgnoreCase("http")) {
                port = 80;
            } else {
                if (!uri.getScheme().equalsIgnoreCase("https")) {
                    throw new IllegalStateException("Bad URI scheme: " + uri.getScheme());
                }
                port = 443;
            }
        }
        return port;
    }

    private static String formDecode(String encoded) {
        return URLDecoder.decode(encoded, StandardCharsets.UTF_8);
    }

    private Map<String, String> parseQueryString(String parameterString) {
        if (parameterString == null || parameterString.length() == 0) {
            return new HashMap<>();
        }
        String[] pairs = StringUtils.tokenizeToStringArray(parameterString, "&");
        MultiValueMap<String, String> result = new LinkedMultiValueMap<>(pairs.length);
        for (String pair : pairs) {
            int idx = pair.indexOf('=');
            if (idx == -1) {
                result.add(formDecode(pair), "");
            } else {
                String name = formDecode(pair.substring(0, idx));
                String value = formDecode(pair.substring(idx + 1));
                result.add(name, value);
            }
        }
        return result.toSingleValueMap();
    }

    private String quoted(String str) {
        return "\"" + str + "\"";
    }

    private long generateTimestamp() {
        long timestamp = System.currentTimeMillis();
        return timestamp / 1000L;
    }

    private String generateNonce() {
        return Long.toString(Math.abs(this.secureRandom.nextLong())) + System.currentTimeMillis();
    }

    private String urlEncode(String source) {
        return UriUtils.encode(source, StandardCharsets.UTF_8);
    }
}
