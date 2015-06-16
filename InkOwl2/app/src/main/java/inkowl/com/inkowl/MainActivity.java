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

    public static boolean isTablet;
    private ImagesListFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isTablet = getResources().getBoolean(R.bool.isTablet);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.listcontainer, new HashtagsFragment());

        if (isTablet) {
            fragment = new ImagesListFragment();
            transaction.add(R.id.tattooscontainer, fragment);
        }

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

        if (!isTablet) {
            fragment = new ImagesListFragment();
            FragmentManager manager = getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.listcontainer, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

        fragment.setTag(tag);
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
