package inkowl.com.inkowl.helpers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

// source https://gist.github.com/henrytao-me/2f7f113fb5f2a59987e7

/**
 * Created by henrytao on 5/1/15.
 */
public class RecycleEmptyErrorView extends RecyclerView {

    private View mEmptyView;

    private View mErrorView;

    private boolean isError;

    private int mVisibility;

    final private AdapterDataObserver mObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            updateEmptyView();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            updateEmptyView();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            updateEmptyView();
        }
    };

    public RecycleEmptyErrorView(Context context) {
        super(context);
        mVisibility = getVisibility();
    }

    public RecycleEmptyErrorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mVisibility = getVisibility();
    }

    public RecycleEmptyErrorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mVisibility = getVisibility();
    }

    @Override
    public void setAdapter(Adapter adapter) {
        Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(mObserver);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(mObserver);
        }
        updateEmptyView();
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        mVisibility = visibility;
        updateErrorView();
        updateEmptyView();
    }

    private void updateEmptyView() {
        if (mEmptyView != null && getAdapter() != null) {
            boolean isShowEmptyView = getAdapter().getItemCount() == 0;
            mEmptyView.setVisibility(isShowEmptyView && !shouldShowErrorView() && mVisibility == VISIBLE ? VISIBLE : GONE);
            super.setVisibility(!isShowEmptyView && !shouldShowErrorView() && mVisibility == VISIBLE ? VISIBLE : GONE);
        }
    }

    private void updateErrorView() {
        if (mErrorView != null) {
            mErrorView.setVisibility(shouldShowErrorView() && mVisibility == VISIBLE ? VISIBLE : GONE);
        }
    }

    private boolean shouldShowErrorView() {
        if (mErrorView != null && isError) {
            return true;
        }
        return false;
    }

    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;
        updateEmptyView();
    }

    public void setErrorView(View errorView) {
        mErrorView = errorView;
        updateErrorView();
        updateEmptyView();
    }

    public void showErrorView() {
        isError = true;
        updateErrorView();
        updateEmptyView();
    }

    public void hideErrorView() {
        isError = false;
        updateErrorView();
        updateEmptyView();
    }

}
