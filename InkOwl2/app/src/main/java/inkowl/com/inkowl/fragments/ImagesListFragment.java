package inkowl.com.inkowl.fragments;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quentindommerc.superlistview.OnMoreListener;
import com.quentindommerc.superlistview.SuperListview;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import inkowl.com.inkowl.MainActivity;
import inkowl.com.inkowl.R;
import inkowl.com.inkowl.TumblrConfig;
import inkowl.com.inkowl.adapters.PhotosAdapter;

/**
 * Created by filipemarquespereira on 6/11/15.
 */
public class ImagesListFragment extends ListFragment {
    public static String mTag;
    public ArrayList<Post> mPosts;
    private PhotosAdapter mPhotosAdapter;
    private SuperListview listView;
    private boolean hasLoadedEverything;

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


        hasLoadedEverything = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPosts = new ArrayList<Post>();
        mPhotosAdapter = new PhotosAdapter(getActivity(), mPosts);

        if (!MainActivity.isTablet) {
            new GetImages().execute("");
        }
        View view = inflater.inflate(R.layout.superlistview_fragment, container, false);
        listView = (SuperListview) view.findViewById(R.id.list);
        listView.setAdapter(mPhotosAdapter);

        listView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetImages().execute("");
                hasLoadedEverything = false;
            }
        });

        /**
         * This starts the request when the users has only 5 more images to check
         */
        listView.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPosition) {
                if (!hasLoadedEverything) {
                    new GetMoreImages().execute(numberOfItems);
                }
            }
        }, 5);

        return view;
    }

    public void setTag(String tag) {
        mTag = tag;
        if (MainActivity.isTablet) {
            new GetImages().execute("");
        }
    }

    public class GetImages extends AsyncTask<String, Void, Boolean> {
        MainActivity activity;
        ProgressDialog progressDialog;
        JumblrClient client;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mPosts.clear();
            mPhotosAdapter.notifyDataSetChanged();

            activity = (MainActivity) ImagesListFragment.this.getActivity();
            client = activity.registerOAuth();

            Resources resources = activity.getResources();
            progressDialog = new ProgressDialog(activity);
            progressDialog.setTitle(resources.getString(R.string.loading));
            progressDialog.setMessage(resources.getString(R.string.loading_message_two) + " " + mTag +  " " + resources.getString(R.string.tatto_lower));
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            /**
             * This request brings 20 posts (at the time of this writing)
             * **/
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("tag", mTag);
            mPosts.addAll(client.blogPosts(TumblrConfig.tumblrAddress, params));

            return mPosts.size() > 0 ? true : false;
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

    public class GetMoreImages extends AsyncTask<Integer, Void, Boolean> {
        JumblrClient client;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MainActivity activity = (MainActivity) ImagesListFragment.this.getActivity();
            client = activity.registerOAuth();
        }

        @Override
        protected Boolean doInBackground(Integer... ints) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("tag", mTag);
            params.put("offset", ints[0]);
            List<Post> posts = client.blogPosts(TumblrConfig.tumblrAddress, params);
            mPosts.addAll(posts);
            return posts.size() > 0 ? true : false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                mPhotosAdapter.notifyDataSetChanged();
            } else {
                hasLoadedEverything = true;
            }
            listView.hideMoreProgress();
        }
    }
}
