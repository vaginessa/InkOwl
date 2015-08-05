package inkowl.com.inkowl.models;

import android.text.Html;

import com.tumblr.jumblr.types.Photo;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.Post;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by filipemarquespereira on 8/5/15.
 */
public class DataManager {

    public ArrayList<TattooPost> parsePosts(List<Post> posts) {
        ArrayList<TattooPost> retVal = new ArrayList<TattooPost>(posts.size());

        for (Post post : posts) {
            PhotoPost photoPost = (PhotoPost) post;
            Photo photo = photoPost.getPhotos().get(0);

            String photoUrl = photo.getOriginalSize().getUrl();
            String sourceUrl = Html.fromHtml(photoPost.getCaption()).toString().replace("\n", "");

            retVal.add(new TattooPost(photoUrl, sourceUrl, post.getPostUrl()));
        }
        return retVal;
    }
}
