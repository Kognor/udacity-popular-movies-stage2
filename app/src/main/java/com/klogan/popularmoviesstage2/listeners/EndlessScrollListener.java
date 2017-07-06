package com.klogan.popularmoviesstage2.listeners;

import android.util.Log;
import android.widget.AbsListView;

/**
 * ScrollListener to support pagination.
 */
public class EndlessScrollListener implements AbsListView.OnScrollListener {

    private static final String LOG_TAG = EndlessScrollListener.class.getSimpleName();

    private static final int PAGE_SIZE = 20;

    // The minimum number of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 6;

    // The total number of items in the dataset after the last load
    private int previousTotalItemCount = 0;
    // True if we are still waiting for the last set of data to load.
    private boolean loading = true;

    // When testing, it seems there's a bug in Android that shows 20 items in the visibleItemCount.
    // This is way more than expected for our use-case, so I'm working around to prevent this bug
    // From causing more next-page-loads than necessary.
    private int maxVisibleAtATime = 6;

    private Callback callback;

    public EndlessScrollListener(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // Don't care about scroll state changing.
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        if (!loading && (totalItemCount - maxVisibleAtATime) <= (firstVisibleItem + visibleThreshold)) {
            int nextPage = totalItemCount / PAGE_SIZE + 1;
            Log.i(LOG_TAG, "loading...Calling onLoadMore() for nextPage=" + nextPage);
            this.callback.onLoadMore(nextPage);
            loading = true;
        }

        if (loading) {
            Log.i(LOG_TAG, "loading...Loading is true");
            if (totalItemCount > previousTotalItemCount) {
                loading = false;
                previousTotalItemCount = totalItemCount;
            }
        }

    }

    public interface Callback {
        boolean onLoadMore(int nextPage);
    }
}
