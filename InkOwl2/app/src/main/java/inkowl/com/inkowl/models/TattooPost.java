package inkowl.com.inkowl.models;

/**
 * Created by filipemarquespereira on 8/5/15.
 */
public class TattooPost {
    private String photoUrl;
    private String sourceUrl;
    private String postUrl;

    public TattooPost(String photoUrl, String sourceUrl, String postUrl) {
        this.photoUrl = photoUrl;
        this.sourceUrl = sourceUrl;
        this.postUrl = postUrl;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public String getPostUrl() {
        return postUrl;
    }
}
