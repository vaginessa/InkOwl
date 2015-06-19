package inkowl.com.inkowl.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import inkowl.com.inkowl.R;

/**
 * Created by filipemarquespereira on 6/18/15.
 */
public class HashtagsAdapter extends RecyclerView.Adapter<HashtagsAdapter.HashtagViewHolder> {

    private ArrayList<String> mHashtags;

    public HashtagsAdapter(ArrayList<String> hashtags) {
        mHashtags = hashtags;
    }

    @Override
    public HashtagViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.hashtag_item, viewGroup, false);

        return new HashtagViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(HashtagViewHolder hashtagViewHolder, int i) {
        String hashtag = mHashtags.get(i);
        hashtagViewHolder.hashtagName.setText("#" + hashtag);
    }

    @Override
    public int getItemCount() {
        return mHashtags.size();
    }

    public static class HashtagViewHolder extends RecyclerView.ViewHolder

    {
        protected TextView hashtagName;

        public HashtagViewHolder(View view) {
            super(view);
            hashtagName = (TextView) view.findViewById(R.id.hashtagTextView);
        }
    }
}
