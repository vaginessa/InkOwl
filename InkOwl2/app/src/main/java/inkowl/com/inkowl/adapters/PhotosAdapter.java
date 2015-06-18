package inkowl.com.inkowl.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.tumblr.jumblr.types.Photo;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.Post;

import java.util.ArrayList;

import inkowl.com.inkowl.R;

/**
 * Created by filipemarquespereira on 6/11/15.
 */
public class PhotosAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Post> mPosts;
    private LayoutInflater mInflater;

    public PhotosAdapter(Context context, ArrayList<Post> posts) {
        mContext = context;
        mPosts = posts;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mPosts.size();
    }

    @Override
    public PhotoPost getItem(int i) {
        return (PhotoPost) mPosts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mPosts.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        PhotoPost post = getItem(i);
        Photo photo = post.getPhotos().get(0);
        String photoUrl = photo.getOriginalSize().getUrl();

        if (view == null) {
            view = mInflater.inflate(R.layout.image_item, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) view.findViewById(R.id.tattooImage);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

//        final String sourceOriginUrl = Html.fromHtml(post.getCaption()).toString().replace("\n", "");

        Picasso.with(mContext).load(photoUrl).into(holder.imageView);

        return view;
    }

    private static class ViewHolder {
        ImageView imageView;
    }

    private void openWebPage(String sourceOriginUrl) {
        Uri webpage = Uri.parse(sourceOriginUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(mContext.getPackageManager()) != null) {
            mContext.startActivity(intent);
        }
    }
}
