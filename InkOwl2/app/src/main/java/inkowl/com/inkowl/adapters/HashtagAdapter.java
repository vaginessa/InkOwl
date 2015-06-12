package inkowl.com.inkowl.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import inkowl.com.inkowl.R;

/**
 * Created by filipemarquespereira on 6/10/15.
 */
public class HashtagAdapter extends BaseAdapter {
    private ArrayList<String> mHashtags;
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Integer> colorResources;

    public HashtagAdapter(Context context, ArrayList<String> hashtags) {
        mContext = context;
        mHashtags = hashtags;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Resources resources = context.getResources();

        colorResources = new ArrayList<Integer>();
        colorResources.add(resources.getColor(R.color.light_blue));
        colorResources.add(resources.getColor(R.color.light_green));
        colorResources.add(resources.getColor(R.color.yellow));
        colorResources.add(resources.getColor(R.color.purple));
        colorResources.add(resources.getColor(R.color.strong_blue));
        colorResources.add(resources.getColor(R.color.strong_red));
    }

    @Override
    public int getCount() {
        return mHashtags.size();
    }

    @Override
    public String getItem(int position) {
        return mHashtags.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;

        int colorPosition = position % colorResources.size();

        if (view == null) {
            view = mInflater.inflate(R.layout.hashtag_item, null);
            holder = new ViewHolder();
            holder.hashtagName = (TextView) view.findViewById(R.id.hashtagTextView);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        view.setBackgroundColor(colorResources.get(colorPosition));
        holder.hashtagName.setText("#" + getItem(position));
        return view;
    }

    private static class ViewHolder {
        TextView hashtagName;
    }
}
