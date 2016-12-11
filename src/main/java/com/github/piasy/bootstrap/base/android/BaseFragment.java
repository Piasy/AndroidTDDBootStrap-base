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

package com.github.piasy.bootstrap.base.android;

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
import com.github.piasy.safelyandroid.activity.StartActivityDelegate;
import com.github.piasy.safelyandroid.fragment.SupportFragmentTransactionDelegate;
import com.github.piasy.safelyandroid.fragment.TransactionCommitter;
import com.github.piasy.yamvp.YaPresenter;
import com.github.piasy.yamvp.YaView;
import com.github.piasy.yamvp.dagger2.YaMvpDiFragment;
import com.yatatsu.autobundle.AutoBundle;
import onactivityresult.ActivityResult;

/**
 * Created by Piasy{github.com/Piasy} on 15/7/23.
 *
 * Base fragment class.
 */

@SuppressWarnings("unused")
public abstract class BaseFragment<V extends YaView, P extends YaPresenter<V>, C>
        extends YaMvpDiFragment<V, P, C> implements TransactionCommitter {

    private final SupportFragmentTransactionDelegate mSupportFragmentTransactionDelegate
            = new SupportFragmentTransactionDelegate();
    private Unbinder mUnBinder;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        // inject argument first
        if (savedInstanceState == null) {
            AutoBundle.bind(this);
        } else {
            AutoBundle.bind(this, savedInstanceState);
        }
        super.onCreate(savedInstanceState);
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

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        setHasOptionsMenu(shouldHaveOptionsMenu());
        return inflater.inflate(getLayoutRes(), container, false);
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
    }

    protected boolean safeCommit(@NonNull final FragmentTransaction transaction) {
        return mSupportFragmentTransactionDelegate.safeCommit(this, transaction);
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
        mUnBinder = ButterKnife.bind(this, rootView);
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
    @CallSuper
    protected void unbindView() {
        mUnBinder.unbind();
    }
}
