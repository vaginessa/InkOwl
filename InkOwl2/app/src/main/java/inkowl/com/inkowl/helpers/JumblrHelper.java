package inkowl.com.inkowl.helpers;

import com.tumblr.jumblr.JumblrClient;

import inkowl.com.inkowl.TumblrConfig;

/**
 * Created by Filipe on 20/06/2015.
 */
public class JumblrHelper {
    private JumblrClient client;
    private static JumblrHelper instance = null;

    protected JumblrHelper(){}

    public static JumblrHelper getInstance() {
        if (instance == null) {
            instance = new JumblrHelper();
        }
        return instance;
    }

    public JumblrClient registerOAuth() {
        if (client == null) {
            client = new JumblrClient(
                    TumblrConfig.consumerKey,
                    TumblrConfig.consumerSectret
            );
            client.setToken(
                    TumblrConfig.token,
                    TumblrConfig.tokenSecret
            );
        }
        return client;
    }
}
