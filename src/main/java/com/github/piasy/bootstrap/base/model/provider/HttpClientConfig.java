package com.github.piasy.bootstrap.base.model.provider;

import com.google.auto.value.AutoValue;

/**
 * Created by Piasy{github.com/Piasy} on 5/12/16.
 */
@AutoValue
public abstract class HttpClientConfig {
    public static Builder builder() {
        return new AutoValue_HttpClientConfig.Builder();
    }

    public abstract boolean enableLog();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder enableLog(final boolean enableLog);

        public abstract HttpClientConfig build();
    }
}
