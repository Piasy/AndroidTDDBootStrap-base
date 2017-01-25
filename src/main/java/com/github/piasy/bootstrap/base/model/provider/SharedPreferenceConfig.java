package com.github.piasy.bootstrap.base.model.provider;

import com.google.auto.value.AutoValue;

/**
 * Created by Piasy{github.com/Piasy} on 5/12/16.
 */
@AutoValue
public abstract class SharedPreferenceConfig {
    public static Builder builder() {
        return new AutoValue_SharedPreferenceConfig.Builder();
    }

    public abstract String name();

    public abstract int mode();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder name(final String name);

        public abstract Builder mode(final int mode);

        public abstract SharedPreferenceConfig build();
    }
}
