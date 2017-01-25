package com.github.piasy.bootstrap.base.rx;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import java.util.concurrent.TimeUnit;

public class DisperseTransformer<T> implements ObservableTransformer<T, T> {

    private final long mLeastInterval;
    private long mLastItemTs;

    public DisperseTransformer(final long leastInterval) {
        mLeastInterval = leastInterval;
        mLastItemTs = System.currentTimeMillis();
    }

    @Override
    public ObservableSource<T> apply(final Observable<T> upstream) {
        return upstream.flatMap(t -> {
            final long now = System.currentTimeMillis();
            final long next = mLastItemTs + mLeastInterval;
            mLastItemTs = Math.max(next, now);
            return Observable.just(t)
                    .delay(mLastItemTs - now, TimeUnit.MILLISECONDS);
        });
    }
}
