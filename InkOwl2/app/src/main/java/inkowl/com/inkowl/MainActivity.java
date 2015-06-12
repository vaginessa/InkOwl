package inkowl.com.inkowl;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.tumblr.jumblr.JumblrClient;


public class MainActivity extends ActionBarActivity implements HashtagsFragment.OnFragmentInteractionListener {
    private static String logTag = "MainActivity";
    private JumblrClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.listcontainer, new HashtagsFragment());
        transaction.commit();
    }

    public JumblrClient registerOAuth() {
        if (client != null) {
            return client;
        }
        client = new JumblrClient(
                TumblrConfig.consumerKey,
                TumblrConfig.consumerSectret
        );
        client.setToken(
                TumblrConfig.token,
                TumblrConfig.tokenSecret
        );
        return client;
    }

    @Override
    public void onFragmentInteraction(String tag) {
        Log.i(logTag, "Tag to be searched \"" + tag + "\"");

        FragmentManager manager = getFragmentManager();

        ImagesListFragment.tag = tag;

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.listcontainer, new ImagesListFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
