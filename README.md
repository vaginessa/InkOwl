# InkOwl
Android App based on a given Tumblr

Initial steps, expect this to be confusing.
You need to create a TumblrConfig.java file yourself and inclue a few static vars in there.

<pre><code>public static String consumerKey = YOUR_CONSUMER_KEY;
public static String consumerSectret = YOUR_CONSUMER_SECRET;
public static String token = YOUR_TOKEN;
public static String tokenSecret  = YOUR_TOKEN_SECRETS;
public static String hashtagName = YOUR_TAG_NAME;
public static String tumblrAddress = YOUR_TUMBLR_ADDRESS;
</code></pre>

<p>The first four values are obtained through the Tumblr API console and this sort of thing. <br />
The tag name is the base of the app and the tumblr address is your tumblr.</p>

<p>The tag name is used to filter the posts from the tumblr address of choice and in that post the searchable <br />
tags are inserted separated by commas.

<p>TO-DO:</p>
* Try to make the app look nice (We need UI tweaks)
* Let's try testing our app (unit testing and this sort of thing)
* Replace ListView with RecyclerView
* Fix the about(maybe textview with a DialogFragment)

...to be continued


## License

InkOwl is released under the [MIT License](http://www.opensource.org/licenses/MIT).




