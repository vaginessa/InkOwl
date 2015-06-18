package inkowl.com.inkowl.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import inkowl.com.inkowl.MainActivity;
import inkowl.com.inkowl.R;
import inkowl.com.inkowl.helpers.RecyclerItemClickListener;
import inkowl.com.inkowl.TumblrConfig;
import inkowl.com.inkowl.adapters.HashtagsAdapter;

/**
 * Created by filipemarquespereira on 6/18/15.
 *
 * A fragment representing a list of hashtags.
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class HashtagsListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private ArrayList<String> mHashtags;
    private HashtagsAdapter hashtagsAdapter;

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(String tag);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHashtags = new ArrayList<String>();
        hashtagsAdapter = new HashtagsAdapter(mHashtags);
        new GetTags().execute("");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hashtags_recycler_view_list, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.hashtagsList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(hashtagsAdapter);
        recyclerView.addOnItemTouchListener(
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

    public class GetTags extends AsyncTask<String, Void, Boolean>
    {
        ProgressDialog progressDialog;
        MainActivity activity;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            activity = (MainActivity) HashtagsListFragment.this.getActivity();
            Resources resources = activity.getResources();
            progressDialog = new ProgressDialog(activity);
            progressDialog.setTitle(resources.getString(R.string.loading));
            progressDialog.setMessage(resources.getString(R.string.loading_message));
            progressDialog.show();
        }

        protected Boolean doInBackground(final String... args)
        {
            // Make the request
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("tag", TumblrConfig.hashtagName);

            // Get post with tags that the tumblr will have
            JumblrClient client = activity.registerOAuth();
            TextPost postWithTags = (TextPost) client.blogPosts(TumblrConfig.tumblrAddress, params).get(0);

            // Cleanup string and split it
            String postText = Html.fromHtml(postWithTags.getBody()).toString();
            postText = postText.replace("\n", "");

            List<String> hashtagsList = Arrays.asList(postText.split(","));
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
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.dialog_message)
                        .setTitle(R.string.dialog_title).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

}
