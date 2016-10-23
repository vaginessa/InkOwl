# InkOwl
Android App based on a given Tumblr

## Setup
- IDE: Android Studio 2.2.1

## Initial steps

- Expect this to be confusing.
You need to create a TumblrConfig.java file yourself and include a few static vars in there. Such as this:


```
public static String consumerKey = YOUR_CONSUMER_KEY;
public static String consumerSectret = YOUR_CONSUMER_SECRET;
public static String token = YOUR_TOKEN;
public static String tokenSecret  = YOUR_TOKEN_SECRETS;
public static String hashtagName = YOUR_TAG_NAME;
public static String tumblrAddress = YOUR_TUMBLR_ADDRESS;
```

The first four values are obtained through the Tumblr API console and this sort of thing.
The tag name is the base of the app and the tumblr address is your tumblr.

The tag name is used to filter the posts from the tumblr address of choice and in that post the searchable
tags are inserted separated by commas.

## TO-DO:

- Let's try testing our app (unit testing and this sort of thing)
- Add error handling with Jumblr
- Add support to landscape
...to be continued


## License

InkOwl is released under the [MIT License](http://www.opensource.org/licenses/MIT).
