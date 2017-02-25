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

package com.github.piasy.bootstrap.base.model.provider;

import android.content.Context;
import android.content.SharedPreferences;
import com.f2prateek.rx.preferences2.RxSharedPreferences;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.github.piasy.bootstrap.base.model.jsr310.ZonedDateTimeJsonConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;
import com.moczul.ok2curl.CurlInterceptor;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import dagger.Module;
import dagger.Provides;
import io.reactivex.schedulers.Schedulers;
import java.util.Set;
import javax.inject.Singleton;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.greenrobot.eventbus.EventBus;
import org.threeten.bp.ZonedDateTime;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

/**
 * Created by Piasy{github.com/Piasy} on 15/9/6.
 */
@Module
public class ProviderModule {

    @Singleton
    @Provides
    EventBus provideEventBus(final EventBusConfig config) {
        return EventBus.builder()
                .logNoSubscriberMessages(config.logNoSubscriberMessages())
                .sendNoSubscriberEvent(config.sendNoSubscriberEvent())
                .eventInheritance(config.eventInheritance())
                .throwSubscriberException(config.throwSubscriberException())
                .build();
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    @Singleton
    @Provides
    Gson provideGson(final GsonConfig config, final Set<TypeAdapterFactory> factories) {
        final GsonBuilder builder = new GsonBuilder();
        for (final TypeAdapterFactory factory : factories) {
            builder.registerTypeAdapterFactory(factory);
        }
        return builder.registerTypeAdapter(ZonedDateTime.class,
                new ZonedDateTimeJsonConverter(config.dateTimeFormatter()))
                .setDateFormat(config.dateFormatString())
                .setPrettyPrinting()
                .create();
    }

    @Singleton
    @Provides
    Retrofit provideRetrofit(final RetrofitConfig config, final OkHttpClient okHttpClient,
            final Gson gson) {
        return new Retrofit.Builder().baseUrl(config.baseUrl())
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(
                        RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build();
    }

    @Singleton
    @Provides
    OkHttpClient provideHttpClient(final HttpClientConfig config) {
        final OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (config.enableLog()) {
            builder.addNetworkInterceptor(new StethoInterceptor())
                    .addInterceptor(new HttpLoggingInterceptor(
                            message -> Timber.tag("OkHttp")
                                    .d(message))
                            .setLevel(HttpLoggingInterceptor.Level.BODY))
                    .addInterceptor(new CurlInterceptor(
                            message -> Timber.tag("Ok2Curl")
                                    .d(message)));
        }
        return builder.build();
    }

    @Singleton
    @Provides
    BriteDatabase provideBriteDb(final BriteDbConfig config) {
        final SqlBrite sqlBrite = new SqlBrite.Builder()
                .logger(message -> Timber.d(message))
                .build();
        final BriteDatabase briteDb = sqlBrite
                .wrapDatabaseHelper(config.sqliteOpenHelper(), rx.schedulers.Schedulers.io());
        briteDb.setLoggingEnabled(config.enableLogging());
        return briteDb;
    }

    @Singleton
    @Provides
    SharedPreferences provideSharedPreferences(final Context context,
            final SharedPreferenceConfig config) {
        return context.getSharedPreferences(config.name(), config.mode());
    }

    @Singleton
    @Provides
    RxSharedPreferences provideRxSharedPreferences(final SharedPreferences preferences) {
        return RxSharedPreferences.create(preferences);
    }
}
