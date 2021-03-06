package inkowl.com.inkowl.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.TextPost;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import inkowl.com.inkowl.R;
import inkowl.com.inkowl.TumblrConfig;
import inkowl.com.inkowl.adapters.HashtagsAdapter;
import inkowl.com.inkowl.helpers.JumblrHelper;
import inkowl.com.inkowl.helpers.RecycleEmptyErrorView;
import inkowl.com.inkowl.helpers.RecyclerItemClickListener;
import inkowl.com.inkowl.models.DataManager;


/**
 * Created by filipemarquespereira on 6/18/15.
 * <p/>
 * A fragment representing a list of hashtags.
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class HashtagsListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private OnProgressDialogStateListener mProgressDialogListener;

    private ArrayList<String> mHashtags;
    private HashtagsAdapter hashtagsAdapter;

    private SwipeRefreshLayout refreshLayout;
    private RecycleEmptyErrorView mRecyclerView;

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(String tag);
    }

    public interface OnProgressDialogStateListener {
        public void onShowProgressDialog(int messageType, String info);
        public void onDismissProgressDialog();
    }

    private DataManager dataManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataManager = new DataManager(getActivity());
        mHashtags = new ArrayList<String>();
        mHashtags.addAll(dataManager.restoreHashtags());
        hashtagsAdapter = new HashtagsAdapter(mHashtags);
        if (JumblrHelper.hasConnection(getActivity())) {
            new GetTags().execute("");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hashtags_recycler_view_list, container, false);

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        refreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (JumblrHelper.hasConnection(getActivity())) {
                    mHashtags.clear();
                    new GetTags().execute("");
                } else {
                    refreshLayout.setRefreshing(false);
                }
            }
        });


        mRecyclerView = (RecycleEmptyErrorView) view.findViewById(R.id.hashtagsList);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(hashtagsAdapter);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (mListener != null) {
                            // Notify the active callbacks interface (the activity, if the
                            // fragment is attached to one) that an item has been selected.
                            String tag = mHashtags.get(position);
                            mListener.onFragmentInteraction(tag);
                        }
                    }
                })
        );
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
        mProgressDialogListener = null;
    }

    public class GetTags extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mProgressDialogListener != null) {
                mProgressDialogListener.onShowProgressDialog(0, null);
            }
            mHashtags.clear();
        }

        protected Boolean doInBackground(final String... args) {
            // Make the request
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("tag", TumblrConfig.hashtagName);

            // Get post with tags that the tumblr will have
            JumblrClient client = JumblrHelper.getInstance().registerOAuth();
            TextPost postWithTags = (TextPost) client.blogPosts(TumblrConfig.tumblrAddress, params).get(0);

            // Cleanup string and split it
            String postText = Html.fromHtml(postWithTags.getBody()).toString();
            postText = postText.replace("\n", "");

            List<String> hashtagsList = Arrays.asList(postText.split(","));
            mHashtags.addAll(hashtagsList);
            dataManager.saveHashtags(mHashtags);

            // TO-DO: has to return false under some circumstance
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            super.onPostExecute(success);
            if (mProgressDialogListener != null) {
                mProgressDialogListener.onDismissProgressDialog();
            }

            if (refreshLayout.isRefreshing()) {
                refreshLayout.setRefreshing(false);
            }

            if (success) {
                hashtagsAdapter.notifyDataSetChanged();
            } else {
                mRecyclerView.showErrorView();
            }
        }
    }
}
