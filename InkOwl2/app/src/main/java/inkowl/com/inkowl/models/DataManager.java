package inkowl.com.inkowl.models;

import android.content.Context;
import android.text.Html;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tumblr.jumblr.types.Photo;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.Post;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by filipemarquespereira on 8/5/15.
 */
public class DataManager {

    private Gson gson;
    private Context context;
    private static String fileextension = ".json";

    public DataManager(Context context) {
        gson  = new Gson();
        this.context = context;
    }

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

    public void savePosts(ArrayList<TattooPost> posts, String hashtag) {
        String json = gson.toJson(posts);
        saveJson(json, hashtag);
    }

    private void saveJson(String json, String filename) {
        FileOutputStream outputStream;

        try {
            outputStream = context.openFileOutput(filename+fileextension, Context.MODE_PRIVATE);
            outputStream.write(json.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<TattooPost> restorePosts(String hashtag) {
        ArrayList<TattooPost> retVal;
        Type arrayListType = new TypeToken<ArrayList<TattooPost>>(){}.getType();
        retVal = gson.fromJson(restoreJson(hashtag), arrayListType);
        if (retVal == null) {
            retVal = new ArrayList<>();
        }
        return retVal;
    }

    private String restoreJson(String hashtag) {
        String retVal = "";
        String hashtagFilename = hashtag+fileextension;
        for (String filename : context.fileList()) {
            if (filename.equals(hashtagFilename)) {
                try {
                    FileInputStream fis = context.openFileInput(filename);

                    StringBuffer fileContent = new StringBuffer("");
                    int n;

                    byte[] buffer = new byte[1024];

                    while ((n = fis.read(buffer)) != -1)
                    {
                        fileContent.append(new String(buffer, 0, n));
                    }
                    fis.close();
                    retVal = fileContent.toString();
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return retVal;
    }
}
