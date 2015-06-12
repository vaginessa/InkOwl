package inkowl.com.inkowl;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.Post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import inkowl.com.inkowl.adapters.PhotosAdapter;

/**
 * Created by filipemarquespereira on 6/11/15.
 */
public class ImagesListFragment extends ListFragment {
    public static String tag;
    public ArrayList<Post> mPosts;
    private PhotosAdapter mPhotosAdapter;

    public static ImagesListFragment newInstance(String param1, String param2) {
        ImagesListFragment fragment = new ImagesListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ImagesListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
        }

        mPosts = new ArrayList<Post>();
        mPhotosAdapter = new PhotosAdapter(getActivity(), mPosts);

        setListAdapter(mPhotosAdapter);
        new GetImages().execute("");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getListView().setDivider(new ColorDrawable(Color.BLACK));
        getListView().setDividerHeight(2);
    }

    public class GetImages extends AsyncTask<String, Void, Boolean> {
        MainActivity activity;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            activity = (MainActivity) ImagesListFragment.this.getActivity();
            Resources resources = activity.getResources();
            progressDialog = new ProgressDialog(activity);
            progressDialog.setTitle(resources.getString(R.string.loading));
            progressDialog.setMessage(resources.getString(R.string.loading_message_two) + " " + tag +  " " + resources.getString(R.string.tatto_lower));
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("tag", tag);
//            params.put("limit", 2);
            JumblrClient client = activity.registerOAuth();
            mPosts.addAll(client.blogPosts(TumblrConfig.tumblrAddress, params));

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressDialog.dismiss();
            if (aBoolean) {
                mPhotosAdapter.notifyDataSetChanged();
                if (mPosts.size() == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(R.string.no_tats_found_message)
                            .setTitle(R.string.no_tats_title).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (getFragmentManager().getBackStackEntryCount() > 0) {
                                getFragmentManager().popBackStack();
                            }
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        }
    }
}
