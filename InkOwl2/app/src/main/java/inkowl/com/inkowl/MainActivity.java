package inkowl.com.inkowl;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.PhotoPost;

import inkowl.com.inkowl.fragments.AboutWebViewFragment;
import inkowl.com.inkowl.fragments.HashtagsListFragment;
import inkowl.com.inkowl.fragments.TattooPhotoListFragment;


public class MainActivity extends AppCompatActivity implements HashtagsListFragment.OnFragmentInteractionListener, TattooPhotoListFragment.OnFragmentInteractionListener {
    public static String TAG = MainActivity.class.getSimpleName();
    private JumblrClient client;

    public static boolean isTablet;
    private TattooPhotoListFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isTablet = getResources().getBoolean(R.bool.isTablet);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.listcontainer, new HashtagsListFragment());

        if (isTablet) {
            fragment = new TattooPhotoListFragment();
            transaction.add(R.id.tattooscontainer, fragment);
        }

        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            openWebView();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        Log.i(TAG, "Tag to be searched \"" + tag + "\"");

        if (!isTablet) {
            fragment = new TattooPhotoListFragment();
            FragmentManager manager = getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.listcontainer, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

        fragment.setTag(tag);
    }

    @Override
    public void onFragmentInteraction(PhotoPost post) {
        Log.i(TAG, post.getPostUrl());
        // To-Do: implement detail fragment
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    private void openWebView() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.listcontainer, new AboutWebViewFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
