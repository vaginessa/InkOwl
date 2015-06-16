package inkowl.com.inkowl;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewFragment;

/**
 * Created by filipemarquespereira on 6/16/15.
 */
public class AboutWebViewFragment extends WebViewFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String summary = "<html><body>This is our about section. <b>Should it be html?</b></br> Filipe. </body></html>";
        WebView webView = (WebView) super.onCreateView(inflater, container, savedInstanceState);
        webView.loadData(summary, "text/html", null);
        return webView;
    }
}
