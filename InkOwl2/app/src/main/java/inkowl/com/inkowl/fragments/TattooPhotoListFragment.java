package inkowl.com.inkowl.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import inkowl.com.inkowl.helpers.EndlessRecyclerOnScrollListener;
import inkowl.com.inkowl.helpers.JumblrHelper;
import inkowl.com.inkowl.helpers.RecycleEmptyErrorView;
import inkowl.com.inkowl.helpers.RecyclerItemClickListener;
import inkowl.com.inkowl.models.DataManager;
import inkowl.com.inkowl.models.TattooData;
import inkowl.com.inkowl.models.TattooPost;

/**
 * Created by filipemarquespereira on 6/18/15.
 */
public class TattooPhotoListFragment extends Fragment {

    public static String mTag;
    public ArrayList<TattooPost> mPosts;
    private PhotosAdapter mPhotosAdapter;
    private boolean hasLoadedEverything;
    private SwipeRefreshLayout refreshLayout;
    private RecycleEmptyErrorView mRecyclerView;

    private OnFragmentInteractionListener mListener;
    private OnProgressDialogStateListener mProgressDialogListener;

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(TattooPost post);
    }

    public interface OnProgressDialogStateListener {
        public void onShowProgressDialog(int messageType, String info);
        public void onDismissProgressDialog();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hasLoadedEverything = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photos_recycler_view_list, container, false);
        mPosts = new ArrayList<>();
        if (mTag != null) {
            mPosts.addAll(TattooData.getInstance().getDataManager().restorePosts(mTag));
        }

        mPhotosAdapter = new PhotosAdapter(getActivity(), mPosts);

        if (!MainActivity.isTablet && JumblrHelper.hasConnection(getActivity())) {
            new GetImages().execute("");
        }

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        refreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (JumblrHelper.hasConnection(getActivity())) {
                    new GetImages().execute("");
                    hasLoadedEverything = false;
                } else {
                    refreshLayout.setRefreshing(false);
                    hasLoadedEverything = true;
                }
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = (RecycleEmptyErrorView) view.findViewById(R.id.photos_list);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mPhotosAdapter);
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int numberOfItems) {
                if (!hasLoadedEverything && JumblrHelper.hasConnection(getActivity())) {
                    new GetMoreImages().execute(numberOfItems);
                } else {
                    hasLoadedEverything = true;
                }
            }
        });
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mListener != null) {
                    mListener.onFragmentInteraction(mPosts.get(position));
                }
            }
        }));

        mRecyclerView.setEmptyView(view.findViewById(R.id.empty_list_item));
        mRecyclerView.setErrorView(view.findViewById(R.id.error_list_item));

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

        try {
            mProgressDialogListener = (OnProgressDialogStateListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnProgressDialogStateListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mProgressDialogListener.onDismissProgressDialog();
        mProgressDialogListener = null;
    }

    public void setTag(String tag) {
        mTag = tag;
        if (MainActivity.isTablet && JumblrHelper.hasConnection(getActivity())) {
            new GetImages().execute("");
        }
    }

    public class GetImages extends AsyncTask<String, Void, Boolean> {
        JumblrClient client;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mPosts.clear();
            mPhotosAdapter.notifyDataSetChanged();

            client = JumblrHelper.getInstance().registerOAuth();

            if (mProgressDialogListener != null) {
                mProgressDialogListener.onShowProgressDialog(1, mTag);
            }
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            /**
             * This request brings 20 posts (at the time of this writing)
             * **/
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("tag", mTag);
            List<Post> posts = client.blogPosts(TumblrConfig.tumblrAddress, params);
            mPosts.addAll(TattooData.getInstance().getDataManager().parsePosts(posts));
            TattooData.getInstance().getDataManager().savePosts(mPosts, mTag);

            return mPosts.size() > 0 ? true : false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (mProgressDialogListener != null) {
                mProgressDialogListener.onDismissProgressDialog();
            }

            if (refreshLayout.isRefreshing()) {
                refreshLayout.setRefreshing(false);
            }

            if (aBoolean) {
                mPhotosAdapter.notifyDataSetChanged();
            }
        }
    }

    public class GetMoreImages extends AsyncTask<Integer, Void, Boolean> {
        JumblrClient client;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            client = JumblrHelper.getInstance().registerOAuth();
        }

        @Override
        protected Boolean doInBackground(Integer... ints) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("tag", mTag);
            params.put("offset", ints[0]);
            List<Post> posts = client.blogPosts(TumblrConfig.tumblrAddress, params);
            mPosts.addAll(TattooData.getInstance().getDataManager().parsePosts(posts));
            TattooData.getInstance().getDataManager().savePosts(mPosts, mTag);
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
