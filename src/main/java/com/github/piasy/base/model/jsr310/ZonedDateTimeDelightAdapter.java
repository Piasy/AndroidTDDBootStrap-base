package com.github.piasy.base.model.jsr310;

import android.support.annotation.NonNull;
import com.squareup.sqldelight.ColumnAdapter;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

public class ZonedDateTimeDelightAdapter implements ColumnAdapter<ZonedDateTime, String> {

    private final DateTimeFormatter mDateTimeFormatter;

    public ZonedDateTimeDelightAdapter(final DateTimeFormatter dateTimeFormatter) {
        mDateTimeFormatter = dateTimeFormatter;
    }

    @NonNull
    @Override
    public ZonedDateTime decode(final String databaseValue) {
        return mDateTimeFormatter.parse(databaseValue, ZonedDateTime.FROM);
    }

    @Override
    public String encode(@NonNull final ZonedDateTime value) {
        return mDateTimeFormatter.format(value);
    }
}
