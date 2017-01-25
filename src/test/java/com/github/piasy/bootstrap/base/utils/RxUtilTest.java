package com.github.piasy.bootstrap.base.utils;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created by Piasy{github.com/Piasy} on 24/01/2017.
 */
public class RxUtilTest {
    @Test
    public void repoGet_not_refresh_cache_hit_not_call_remote() throws Exception {
        Observable<Boolean> cache = Observable.just(true);
        Observable<Boolean> remote = Observable.fromCallable(() -> {
            throw new RuntimeException("Should not call remote!");
        });

        TestObserver<Boolean> testObserver = new TestObserver<>();
        RxUtil.repoGet(cache, remote, false)
                .subscribe(testObserver);

        testObserver.awaitTerminalEvent();
        assertThat(testObserver.getEvents().get(0)).containsExactly(true).inOrder();
    }

    @Test
    public void repoGet_not_refresh_cache_hit() throws Exception {
        Observable<Boolean> cache = Observable.just(true);
        Observable<Boolean> remote = Observable.just(false);

        TestObserver<Boolean> testObserver = new TestObserver<>();
        RxUtil.repoGet(cache, remote, false)
                .subscribe(testObserver);

        testObserver.awaitTerminalEvent();
        assertThat(testObserver.getEvents().get(0)).containsExactly(true).inOrder();
    }

    @Test
    public void repoGet_not_refresh_cache_miss() throws Exception {
        Observable<Boolean> cache = Observable.empty();
        Observable<Boolean> remote = Observable.just(false);

        TestObserver<Boolean> testObserver = new TestObserver<>();
        RxUtil.repoGet(cache, remote, false)
                .subscribe(testObserver);

        testObserver.awaitTerminalEvent();
        assertThat(testObserver.getEvents().get(0)).containsExactly(false).inOrder();
    }

    @Test
    public void repoGet_refresh_cache_hit() throws Exception {
        Observable<Boolean> cache = Observable.just(true);
        Observable<Boolean> remote = Observable.just(false);

        TestObserver<Boolean> testObserver = new TestObserver<>();
        RxUtil.repoGet(cache, remote, true)
                .subscribe(testObserver);

        testObserver.awaitTerminalEvent();
        assertThat(testObserver.getEvents().get(0)).containsExactly(true, false).inOrder();
    }

    @Test
    public void repoGet_refresh_cache_miss() throws Exception {
        Observable<Boolean> cache = Observable.empty();
        Observable<Boolean> remote = Observable.just(false);

        TestObserver<Boolean> testObserver = new TestObserver<>();
        RxUtil.repoGet(cache, remote, true)
                .subscribe(testObserver);

        testObserver.awaitTerminalEvent();
        assertThat(testObserver.getEvents().get(0)).containsExactly(false).inOrder();
    }
}