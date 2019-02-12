# Reactive Twitter API

A simple reactive API to return a stream of Tweets via a tailable MongoDb cursor.

### Endpoints

|   |        |                             |
|---|--------|-----------------------------|
|GET|/tweets |Returns a stream of tweets   |
|GET|/tweets/{id} |Returns a tweet   |

Execution example,

```bash
./gradlew clean build

java -Dspring.data.mongodb.database=twitter -jar build/libs/reactive-twitter-api-0.0.1-SNAPSHOT.jar
```

Then just hit the endpoints,

```bash
curl http://localhost:8080/tweets

curl http://localhost:8080/tweets/5c6228c549da2538bb45525c
```