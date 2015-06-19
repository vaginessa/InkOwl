package inkowl.com.inkowl.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import inkowl.com.inkowl.R;

/**
 * Created by filipemarquespereira on 6/19/15.
 */
public class AboutFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_fragment, container, false);
        TextView aboutTextView = (TextView) view.findViewById(R.id.about_text_view);
        aboutTextView.setText("Hello");

        return view;
    }
}
