package inkowl.com.inkowl.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.squareup.picasso.Picasso;

import inkowl.com.inkowl.DetailActivity;
import inkowl.com.inkowl.DetailFullscreenActivity;
import inkowl.com.inkowl.R;
import inkowl.com.inkowl.helpers.Utils;

public class DetailFragment extends Fragment {

    public String photoUrl;
    public String sourceUrl;

    private ImageView tattooImageView;
    private Button sourceButton;

    private MixpanelAPI mixpanel;

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mixpanel = MixpanelAPI.getInstance(getActivity(), Utils.projectToken);
        mixpanel.track(DetailFragment.class.getName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        tattooImageView = (ImageView) view.findViewById(R.id.tattooImage);
        sourceButton = (Button) view.findViewById(R.id.sourceButton);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(DetailActivity.ARG1)) {
                photoUrl = extras.getString(DetailActivity.ARG1);

                Picasso.with(getActivity()).load(photoUrl).into(tattooImageView);

                tattooImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mixpanel.track("detailtattoobutton");
                        DetailFullscreenActivity.imageURL = photoUrl;
                        Intent intent = new Intent(getActivity(), DetailFullscreenActivity.class);
                        startActivity(intent);
                    }
                });
            }
            if (extras.containsKey(DetailActivity.ARG2)) {
                sourceUrl = extras.getString(DetailActivity.ARG2);
                sourceButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openWebPage(sourceUrl);
                    }
                });
            }
        }
    }

    private void openWebPage(String sourceOriginUrl) {
        mixpanel.track("sourcebutton");
        Uri webpage = Uri.parse(sourceOriginUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            getActivity().startActivity(intent);
        }
    }
}
