package com.github.piasy.base.model.provider;

import android.support.annotation.Nullable;
import com.google.auto.value.AutoValue;
import com.google.gson.TypeAdapterFactory;
import org.threeten.bp.format.DateTimeFormatter;

/**
 * Created by Piasy{github.com/Piasy} on 5/12/16.
 */
@AutoValue
public abstract class GsonConfig {
    public static Builder builder() {
        return new AutoValue_GsonConfig.Builder();
    }

    public abstract DateTimeFormatter dateTimeFormatter();

    public abstract String dateFormatString();

    @Nullable
    public abstract TypeAdapterFactory autoGsonTypeAdapterFactory();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder dateTimeFormatter(final DateTimeFormatter dateTimeFormatter);

        public abstract Builder dateFormatString(final String dateFormatString);

        public abstract Builder autoGsonTypeAdapterFactory(
                @Nullable final TypeAdapterFactory typeAdapterFactory);

        public abstract GsonConfig build();
    }
}
