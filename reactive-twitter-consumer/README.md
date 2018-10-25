# Reactive Twitter stream consumer

A Twitter realtime filter API consumer.

You'll need to create a new app at https://developer.twitter.com/en/apps and generate key and tokens
Set your key and tokens as the below environment variables, 

```properties
OV_API_KEY=<API key>
OV_API_SECRET=<OV_API_SECRET>
OV_API_ACCESSTOKEN=<Access token>
OV_API_ACCESSTOKENSECRET=<Access token secret>
```

Pass the tracks filter parameter as a program argument, see https://developer.twitter.com/en/docs/tweets/filter-realtime/guides/basic-stream-parameters for reference

Execution example,

`java -jar reactive-twitter-consumer.jar --track='user,google com'`