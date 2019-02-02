# Reactive Twitter stream consumer

This is a Spring Reactive consumer of the Twitter filter API.

1) You'll need to create a new app at https://developer.twitter.com/en/apps and generate key and tokens.

2) You'll need a Mongodb instance running and create a **capped** collection, this is important, if the collection gets automatically created it won't be capped. Only capped collections can be tailed. 

Spin up a Mongodb instance with Docker (skip this if you already have Mongodb running somewhere)

`docker run -p 27017:27017 --name twitterMongo -d --mount type=bind,source=$HOME/data/db,destination=/data/db mongo`

Open mongo shell,

`docker exec -it twitterMongo mongo`

Once connected, create the **twitter** database and the **tweets** capped collection

```
> use twitter
> db.createCollection("tweets", { capped : true, size : 20000000 } )
```

You'll need to set these properties when running the application, 

```properties
ov.api.key=<API key>
ov.api.secret=<API secret>
ov.api.accessToken=<Access token>
ov.api.accessTokenSecret=<Access token secret>
spring.data.mongodb.database=twitter
```

Execution example,

```bash
./gradlew clean build

java -Dov.api.key=api_key -Dov.api.secret=api_secret -Dov.api.accessToken=api_access_token -Dov.api.accessTokenSecret=api_access_token_secret -Dspring.data.mongodb.database=twitter -jar build/libs/reactive-twitter-consumer-0.0.1-SNAPSHOT.jar --track="someUser" --track="some other key"
```

Pass as many **track** filter parameter as program arguments, see https://developer.twitter.com/en/docs/tweets/filter-realtime/guides/basic-stream-parameters for reference
