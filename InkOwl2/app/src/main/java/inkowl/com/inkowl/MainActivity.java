package inkowl.com.inkowl;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.tumblr.jumblr.types.Photo;
import com.tumblr.jumblr.types.PhotoPost;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.picasso.Picasso;

import inkowl.com.inkowl.fragments.HashtagsListFragment;
import inkowl.com.inkowl.fragments.TattooPhotoListFragment;
import inkowl.com.inkowl.models.TattooPost;


public class MainActivity extends AppCompatActivity implements HashtagsListFragment.OnFragmentInteractionListener, TattooPhotoListFragment.OnFragmentInteractionListener, HashtagsListFragment.OnProgressDialogStateListener, TattooPhotoListFragment.OnProgressDialogStateListener {
    public static String TAG = MainActivity.class.getSimpleName();

    private static final int MENU_HOME = -1;
    private static final int MENU_ABOUT = -2;

    public static boolean isTablet;
    private TattooPhotoListFragment fragment;

    private ProgressDialog progressDialog;

    private Drawer navigationDrawer;

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
		if (BuildConfig.DEBUG) {
            Picasso.with(this).setIndicatorsEnabled(true);
        }

        buildDrawer();
    }

    private void buildDrawer()
    {
        navigationDrawer = new DrawerBuilder()
                .withActivity(this)
                .withTranslucentNavigationBar(false)
                .withTranslucentActionBarCompatibility(false)
                .withTranslucentStatusBar(false)
                .withActionBarDrawerToggle(true)
                .withToolbar(new Toolbar(this))
                .withOnDrawerItemClickListener(navigationDrawerClick)
        .build();

        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        navigationDrawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);

        buildDrawerItems();
    }

    private void buildDrawerItems()
    {
        navigationDrawer.removeAllItems();

        navigationDrawer.addItem(new PrimaryDrawerItem().withName("Home").withIcon(ContextCompat.getDrawable(this, R.drawable.ic_list_white_24dp)).withIdentifier(MENU_HOME));

        navigationDrawer.addItem(new SectionDrawerItem().withName("Tattoo Hashtags"));
        navigationDrawer.addItem(new PrimaryDrawerItem().withName("Teeeeest 4"));
        navigationDrawer.addItem(new DividerDrawerItem());

        navigationDrawer.addItem(new SecondaryDrawerItem().withName("About").withIcon(ContextCompat.getDrawable(this, R.drawable.ic_info_outline_white_24dp)).withIdentifier(MENU_ABOUT));
    }

    private void clickMenuItem(IDrawerItem iDrawerItem)
    {
        switch (iDrawerItem.getIdentifier())
        {
            case MENU_HOME:
                break;
            case MENU_ABOUT:
                openAboutActivity();
                break;
            default:
                break;
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
    public void onFragmentInteraction(String tag)
    {
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
    public void onFragmentInteraction(TattooPost post) {
        Log.i(TAG, post.getPostUrl());

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

    Drawer.OnDrawerItemClickListener navigationDrawerClick = new Drawer.OnDrawerItemClickListener()
    {
        @Override
        public boolean onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem)
        {
            clickMenuItem(iDrawerItem);
            return false;
        }
    };
}
