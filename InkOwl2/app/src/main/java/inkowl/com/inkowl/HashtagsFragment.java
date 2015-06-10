package inkowl.com.inkowl;

import android.app.Activity;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ListView;

import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.TextPost;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import inkowl.com.inkowl.adapters.HashtagAdapter;

/**
 * A fragment representing a list of hashtags.
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class HashtagsFragment extends ListFragment {

    private OnFragmentInteractionListener mListener;

    private ArrayList<String> mHashtags;
    private HashtagAdapter hashtagsAdapter;

    public static HashtagsFragment newInstance(String param1, String param2) {
        HashtagsFragment fragment = new HashtagsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public HashtagsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
        }

        mHashtags = new ArrayList<String>();
        hashtagsAdapter = new HashtagAdapter(getActivity(), mHashtags);

        setListAdapter(hashtagsAdapter);

//        getActivity().getResources().getXml(R.xml)
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new GetTags().execute("");
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
//        getListView().setDivider(new ColorDrawable(Color.BLACK));
//        getListView().setDividerHeight(2);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (mListener != null) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(position);
        }
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(int position);
    }

    public class GetTags extends AsyncTask<String, Void, Boolean>
    {
        JumblrClient client;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            Activity activity = HashtagsFragment.this.getActivity();
            Resources resources = activity.getResources();
            progressDialog = new ProgressDialog(activity);
            progressDialog.setTitle(resources.getString(R.string.loading));
            progressDialog.setMessage(resources.getString(R.string.loading_message));
            progressDialog.show();
            // Authenticate via OAuth
            client = new JumblrClient(
                    TumblrConfig.consumerKey,
                    TumblrConfig.consumerSectret
            );
            client.setToken(
                    TumblrConfig.token,
                    TumblrConfig.tokenSecret
            );
        }

        protected Boolean doInBackground(final String... args)
        {
            // Make the request
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("tag", TumblrConfig.hashtagName);

            // Get post with tags that the tumblr will have
            TextPost postWithTags = (TextPost) client.blogPosts(TumblrConfig.tumblrAddress, params).get(0);

            // Cleanup string and split it
            String postText = Html.fromHtml(postWithTags.getBody()).toString();

            List<String> hashtagsList = Arrays.asList(postText.split(", "));
            mHashtags.addAll(hashtagsList);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success)
        {
            super.onPostExecute(success);
            progressDialog.dismiss();
            if(success)
            {
                hashtagsAdapter.notifyDataSetChanged();
            } else {
                // TODO: message in case of failure
            }
        }
    }
}
