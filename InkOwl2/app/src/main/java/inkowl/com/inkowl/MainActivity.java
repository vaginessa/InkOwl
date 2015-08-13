package inkowl.com.inkowl;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import inkowl.com.inkowl.fragments.HashtagsListFragment;
import inkowl.com.inkowl.fragments.TattooPhotoListFragment;
import inkowl.com.inkowl.helpers.Utils;
import inkowl.com.inkowl.models.TattooPost;


public class MainActivity extends AppCompatActivity implements HashtagsListFragment.OnFragmentInteractionListener, TattooPhotoListFragment.OnFragmentInteractionListener, HashtagsListFragment.OnProgressDialogStateListener, TattooPhotoListFragment.OnProgressDialogStateListener {
    public static String TAG = MainActivity.class.getSimpleName();

    public static boolean isTablet;
    private TattooPhotoListFragment fragment;

    private ProgressDialog progressDialog;

    private MixpanelAPI mixpanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mixpanel = MixpanelAPI.getInstance(this, Utils.projectToken);
        mixpanel.track(MainActivity.class.getName());

        isTablet = getResources().getBoolean(R.bool.isTablet);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.listcontainer, new HashtagsListFragment());

        if (isTablet) {
            fragment = new TattooPhotoListFragment();
            transaction.add(R.id.tattooscontainer, fragment);
        }

        transaction.commit();
        if (BuildConfig.DEBUG) {
            Picasso.with(this).setIndicatorsEnabled(true);
        }
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
            openAboutActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(String tag) {
        Log.i(TAG, "Tag to be searched \"" + tag + "\"");

        if (!isTablet) {
            JSONObject properties = new JSONObject();
            try {
                properties.put("tag", tag);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mixpanel.track("choose tag", properties);
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
    public void onFragmentInteraction(TattooPost post) {
        Log.i(TAG, post.getPostUrl());

        JSONObject properties = new JSONObject();
        try {
            properties.put("posturl", post.getPostUrl());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mixpanel.track("tattoourl", properties);
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.ARG1, post.getPhotoUrl());
        intent.putExtra(DetailActivity.ARG2, post.getSourceUrl());
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    private void openAboutActivity() {
        mixpanel.track("hit open about");
        startActivity(new Intent(this, AboutActivity.class));
    }

    @Override
    public void onShowProgressDialog(int messageType, String info) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.loading));
        if (messageType == 0) {
            progressDialog.setMessage(getResources().getString(R.string.loading_message));
        } else if (messageType == 1) {
            progressDialog.setMessage(getResources().getString(R.string.loading_message_two) + " " + info + " " + getResources().getString(R.string.tatto_lower));
        }

        progressDialog.show();
    }

    @Override
    public void onDismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
