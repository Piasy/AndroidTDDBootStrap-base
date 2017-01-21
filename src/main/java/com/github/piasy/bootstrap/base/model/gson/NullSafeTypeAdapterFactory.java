package com.github.piasy.bootstrap.base.model.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

/**
 * Created by Piasy{github.com/Piasy} on 21/01/2017.
 */

public class NullSafeTypeAdapterFactory implements TypeAdapterFactory {
    private final TypeAdapterFactory mDelegate;

    public NullSafeTypeAdapterFactory(final TypeAdapterFactory delegate) {
        mDelegate = delegate;
    }

    @Override
    public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> type) {
        final TypeAdapter<T> typeAdapter = mDelegate.create(gson, type);
        return typeAdapter == null ? null : typeAdapter.nullSafe();
    }
}
