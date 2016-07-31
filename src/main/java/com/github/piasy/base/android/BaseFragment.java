/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Piasy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.piasy.base.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.github.piasy.base.utils.RxUtil;
import com.github.piasy.safelyandroid.activity.StartActivityDelegate;
import com.github.piasy.safelyandroid.fragment.SupportFragmentTransactionDelegate;
import com.github.piasy.safelyandroid.fragment.TransactionCommitter;
import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.components.support.RxFragment;
import com.yatatsu.autobundle.AutoBundle;
import java.util.concurrent.TimeUnit;
import onactivityresult.ActivityResult;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Piasy{github.com/Piasy} on 15/7/23.
 *
 * Base fragment class.
 */
public abstract class BaseFragment extends RxFragment implements TransactionCommitter {

    private static final int WINDOW_DURATION = 1;
    private final SupportFragmentTransactionDelegate mSupportFragmentTransactionDelegate =
            new SupportFragmentTransactionDelegate();
    private CompositeSubscription mCompositeSubscription;
    private Unbinder mUnBinder;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (hasArgs()) {
            AutoBundle.bind(this);
        }
    }

    /**
     * CONTRACT: the new life cycle method {@link #initFields()}, {@link #bindView(View)}
     * and {@link #startBusiness()} might use other infrastructure initialised in subclass's
     * onViewCreated, e.g. DI, MVP, so those subclass should do those
     * infrastructure init job before this method is invoked.
     */
    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFields();
        bindView(view);
        startBusiness();
    }

    @Override
    public void onResume() {
        super.onResume();
        mSupportFragmentTransactionDelegate.onResumed();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbindView();
        unSubscribeAll();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        setHasOptionsMenu(shouldHaveOptionsMenu());
        return inflater.inflate(getLayoutRes(), container, false);
    }

    @Override
    public boolean isCommitterResumed() {
        return isResumed();
    }

    protected final boolean startActivitySafely(final Intent intent) {
        return StartActivityDelegate.startActivitySafely(this, intent);
    }

    protected final boolean startActivityForResultSafely(final Intent intent, final int code) {
        return StartActivityDelegate.startActivityForResultSafely(this, intent, code);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ActivityResult.onResult(requestCode, resultCode, data).into(this);
    }

    protected boolean safeCommit(@NonNull final FragmentTransaction transaction) {
        return mSupportFragmentTransactionDelegate.safeCommit(this, transaction);
    }

    protected void addSubscribe(final Subscription subscription) {
        if (mCompositeSubscription == null || mCompositeSubscription.isUnsubscribed()) {
            // recreate mCompositeSubscription
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(subscription);
    }

    protected void listenOnClickRxy(final View view, final Action1<Void> action) {
        listenOnClickRxy(view, WINDOW_DURATION, action);
    }

    protected void listenOnClickRxy(final View view, final int seconds,
            final Action1<Void> action) {
        addSubscribe(RxView.clicks(view)
                .throttleFirst(seconds, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action, RxUtil.OnErrorLogger));
    }

    protected void unSubscribeAll() {
        if (mCompositeSubscription != null && !mCompositeSubscription.isUnsubscribed()) {
            mCompositeSubscription.unsubscribe();
        }
    }

    /**
     * layout resource id
     *
     * @return layout resource id
     */
    @LayoutRes
    protected abstract int getLayoutRes();

    /**
     * override and return {@code true} to enable option menu.
     */
    protected boolean shouldHaveOptionsMenu() {
        return false;
    }

    /**
     * When use AutoBundle to inject arguments, should override this and return {@code true}.
     */
    protected boolean hasArgs() {
        return false;
    }

    /**
     * When use ButterKnife to auto bind views, should override this and return {@code true}.
     * If not, should override {@link #bindView(View)} and {@link #unbindView()} to do it manually.
     */
    protected boolean autoBindViews() {
        return true;
    }

    /**
     * init necessary fields.
     */
    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    protected void initFields() {

    }

    /**
     * bind views, should override this method when bind view manually.
     */
    @CallSuper
    protected void bindView(final View rootView) {
        if (autoBindViews()) {
            mUnBinder = ButterKnife.bind(this, rootView);
        }
    }

    /**
     * start specific business logic.
     */
    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    protected void startBusiness() {

    }

    /**
     * unbind views, should override this method when unbind view manually.
     */
    protected void unbindView() {
        if (autoBindViews() && mUnBinder != null) {
            mUnBinder.unbind();
        }
    }
}
