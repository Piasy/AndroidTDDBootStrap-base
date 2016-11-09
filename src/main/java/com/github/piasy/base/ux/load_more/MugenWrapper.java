package com.github.piasy.base.ux.load_more;

import android.support.v7.widget.RecyclerView;
import com.mugen.Mugen;
import com.mugen.MugenCallbacks;
import rx.functions.Action0;

/**
 * Created by Piasy{github.com/Piasy} on 04/10/2016.
 */

public class MugenWrapper implements MugenCallbacks {
    private final Action0 mLoadMore;

    private boolean mIsLoading;
    private boolean mIsAllLoaded;

    public MugenWrapper(final RecyclerView recyclerView, final Action0 loadMore) {
        mLoadMore = loadMore;

        Mugen.with(recyclerView, this)
                .start();
    }

    public void dataLoaded() {
        mIsLoading = false;
    }

    public void allDataLoaded() {
        mIsLoading = false;
        mIsAllLoaded = true;
    }

    @Override
    public void onLoadMore() {
        mLoadMore.call();
        mIsLoading = true;
    }

    @Override
    public boolean isLoading() {
        return mIsLoading;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return mIsAllLoaded;
    }
}
