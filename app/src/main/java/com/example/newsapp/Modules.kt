package com.example.newsapp

import androidx.room.Room
import com.example.newsapp.base.HeaderInterceptor
import com.example.newsapp.extensions.LocaleResolver
import com.example.newsapp.extensions.NotificationHelper
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

const val APP_DATABASE = "APP_DATABASE"

val networkModule = module {

    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(get())
            .build()
    }

    single<OkHttpClient> {
        OkHttpClient.Builder()
            .addInterceptor(HeaderInterceptor())
            .addInterceptor(HttpLoggingInterceptor(
                object : HttpLoggingInterceptor.Logger {
                    override fun log(message: String) {
                        Timber.tag("OkHttp").d(message)
                    }
                }
            ).apply {
                setLevel(HttpLoggingInterceptor.Level.BASIC)
            })
            .build()
    }
}

val notificationModule = module {
    single<NotificationHelper> {
        NotificationHelper()
    }
}

val databaseModule = module {
    single {
        Room
            .databaseBuilder(androidContext(), AppDatabase::class.java, APP_DATABASE)
            .fallbackToDestructiveMigration()
            .build()
    }
    single {
        get<AppDatabase>().bookmarksDao()
    }
    single {
        get<AppDatabase>().articlesDao()
    }
}

val localeModule = module {
    single<LocaleResolver> {
        LocaleResolver(androidContext().resources)
    }
}
