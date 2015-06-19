package inkowl.com.inkowl.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.tumblr.jumblr.types.Photo;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.Post;

import java.util.ArrayList;

import inkowl.com.inkowl.R;

/**
 * Created by filipemarquespereira on 6/18/15.
 */
public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.PhotoViewHolder> {
    private ArrayList<Post> mPosts;
    private Context mContext;

    public PhotosAdapter(Context context, ArrayList<Post> posts) {
        mContext = context;
        mPosts = posts;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.image_item, viewGroup, false);

        return new PhotoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder photoViewHolder, int i) {
        PhotoPost post = (PhotoPost) mPosts.get(i);

        Photo photo = post.getPhotos().get(0);
        String photoUrl = photo.getOriginalSize().getUrl();

        Picasso.with(mContext).load(photoUrl).into(photoViewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imageView;

        public PhotoViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.tattooImage);
        }
    }
}
