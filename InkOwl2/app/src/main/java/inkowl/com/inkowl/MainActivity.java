package inkowl.com.inkowl;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;


public class MainActivity extends ActionBarActivity implements HashtagsFragment.OnFragmentInteractionListener {
    private static String logTag = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onFragmentInteraction(String tag) {
        Log.i(logTag, "Tag to be searched \"" + tag + "\"");
    }
}
