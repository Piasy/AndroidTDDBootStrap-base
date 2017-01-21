package com.github.piasy.bootstrap.base.model.provider;

import com.google.auto.value.AutoValue;
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

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder dateTimeFormatter(final DateTimeFormatter dateTimeFormatter);

        public abstract Builder dateFormatString(final String dateFormatString);

        public abstract GsonConfig build();
    }
}
