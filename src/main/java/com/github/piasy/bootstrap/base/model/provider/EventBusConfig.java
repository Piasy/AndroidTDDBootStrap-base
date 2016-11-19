package com.github.piasy.bootstrap.base.model.provider;

import com.google.auto.value.AutoValue;

/**
 * Created by Piasy{github.com/Piasy} on 5/12/16.
 */
@AutoValue
public abstract class EventBusConfig {
    public static Builder builder() {
        return new AutoValue_EventBusConfig.Builder();
    }

    public abstract boolean logNoSubscriberMessages();

    public abstract boolean sendNoSubscriberEvent();

    public abstract boolean eventInheritance();

    public abstract boolean throwSubscriberException();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder logNoSubscriberMessages(final boolean logNoSubscriberMessages);

        public abstract Builder sendNoSubscriberEvent(final boolean sendNoSubscriberEvent);

        public abstract Builder eventInheritance(final boolean eventInheritance);

        public abstract Builder throwSubscriberException(
                final boolean throwSubscriberException);

        public abstract EventBusConfig build();
    }
}
