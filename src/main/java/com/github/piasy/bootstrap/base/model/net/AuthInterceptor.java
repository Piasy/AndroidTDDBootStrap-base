/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Piasy
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

package com.github.piasy.bootstrap.base.model.net;

import android.text.TextUtils;
import java.io.IOException;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Piasy{github.com/Piasy} on 25/02/2017.
 */

public class AuthInterceptor implements Interceptor {

    public static final String AUTH_TYPE = "Auth-Type";
    public static final String AUTH_TYPE_DISABLED = "Disabled";
    public static final String AUTH_TYPE_TOKEN = "Token";

    private final String mTokenKey;
    private final boolean mAllowTokenMissing;

    private volatile String mToken;

    public AuthInterceptor(final String tokenKey, final boolean allowTokenMissing) {
        mTokenKey = tokenKey;
        mAllowTokenMissing = allowTokenMissing;
    }

    public void updateAuth(final String token) {
        mToken = token;
    }

    @Override
    public Response intercept(final Chain chain) throws IOException {
        final Request origin = chain.request();
        final Headers originHeaders = origin.headers();
        final Headers.Builder newHeaders = new Headers.Builder();
        String authType = AUTH_TYPE_TOKEN;
        for (int i = 0, size = originHeaders.size(); i < size; i++) {
            if (!TextUtils.equals(originHeaders.name(i), AUTH_TYPE)) {
                newHeaders.add(originHeaders.name(i), originHeaders.value(i));
            } else {
                authType = originHeaders.value(i);
            }
        }
        Request.Builder newRequest = origin.newBuilder()
                .headers(newHeaders.build());
        switch (authType) {
            case AUTH_TYPE_DISABLED:
                return chain.proceed(origin);
            case AUTH_TYPE_TOKEN:
            default:
                return chain.proceed(tokenAuth(origin, newRequest));
        }
    }

    private Request tokenAuth(final Request origin, final Request.Builder newRequest) {
        if (TextUtils.isEmpty(mToken)) {
            if (mAllowTokenMissing) {
                return origin;
            }
            throw new InvalidTokenError();
        }

        HttpUrl.Builder newUrl = origin.url().newBuilder()
                .addQueryParameter(mTokenKey, mToken);

        newRequest.url(newUrl.build());
        return newRequest.build();
    }

    public static class InvalidTokenError extends RuntimeException {
        private InvalidTokenError() {
            super("Invalid token!");
        }
    }
}
