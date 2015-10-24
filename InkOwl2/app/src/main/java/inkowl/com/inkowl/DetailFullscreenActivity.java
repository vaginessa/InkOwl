package inkowl.com.inkowl;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Filipe on 24/10/2015.
 */
public class DetailFullscreenActivity extends AppCompatActivity {
    public static String imageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_fullscreen);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView tattooFullscreenImageView = (ImageView) findViewById(R.id.tatto_fullscreen_image_view);
        Picasso.with(this).load(imageURL).into(tattooFullscreenImageView);
    }
}
