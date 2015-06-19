package inkowl.com.inkowl.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.Post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import inkowl.com.inkowl.MainActivity;
import inkowl.com.inkowl.R;
import inkowl.com.inkowl.TumblrConfig;
import inkowl.com.inkowl.adapters.PhotosAdapter;
import inkowl.com.inkowl.helpers.EndlessRecyclerOnScrollListener;
import inkowl.com.inkowl.helpers.RecyclerItemClickListener;

/**
 * Created by filipemarquespereira on 6/18/15.
 */
public class TattooPhotoListFragment extends Fragment {

    public static String mTag;
    public ArrayList<Post> mPosts;
    private PhotosAdapter mPhotosAdapter;
    private boolean hasLoadedEverything;
    private SwipeRefreshLayout refreshLayout;

    private OnFragmentInteractionListener mListener;

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(PhotoPost post);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hasLoadedEverything = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photos_recycler_view_list, container, false);
        mPosts = new ArrayList<Post>();
        mPhotosAdapter = new PhotosAdapter(getActivity(), mPosts);

        if (!MainActivity.isTablet) {
            new GetImages().execute("");
        }

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        refreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetImages().execute("");
                hasLoadedEverything = false;
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.photos_list);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mPhotosAdapter);
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int numberOfItems) {
                if (!hasLoadedEverything) {
                    new GetMoreImages().execute(numberOfItems);
                }
            }
        });
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mListener != null) {
                    PhotoPost post = (PhotoPost) mPosts.get(position);
                    mListener.onFragmentInteraction(post);
                }
            }
        }));

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

            activity = (MainActivity) TattooPhotoListFragment.this.getActivity();
            client = activity.registerOAuth();

            Resources resources = activity.getResources();
            progressDialog = new ProgressDialog(activity);
            progressDialog.setTitle(resources.getString(R.string.loading));
            progressDialog.setMessage(resources.getString(R.string.loading_message_two) + " " + mTag + " " + resources.getString(R.string.tatto_lower));
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
            if (refreshLayout.isRefreshing()) {
                refreshLayout.setRefreshing(false);
            }

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
            MainActivity activity = (MainActivity) TattooPhotoListFragment.this.getActivity();
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
        }
    }
}
