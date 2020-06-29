package com.example.newsapp.app.di

import android.app.Application
import androidx.room.Room
import com.example.newsapp.Constants
import com.example.newsapp.api.requests.News
import com.example.newsapp.db.AppDatabase
import com.example.newsapp.db.dao.BookmarkDao
import com.example.newsapp.db.dao.NewsDao
import com.example.newsapp.db.repositories.BookmarkRepository
import com.example.newsapp.db.repositories.NewsRepository
import com.example.newsapp.viewmodel.MainViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

val viewModelModule = module {
    single { MainViewModel(get(), get()) }
}

val apiModule = module {
    fun provideNewsApi(retrofit: Retrofit): News {
        return retrofit.create(News::class.java)
    }

    single { provideNewsApi(get()) }
}

val netModule = module {
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor(
            object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    Timber.tag("OkHttp").d(message)
                }
            }
        ).apply {
            setLevel(HttpLoggingInterceptor.Level.BASIC)
        }
    }

    fun provideHttpClient(logging: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addNetworkInterceptor {
                val requestBuilder = it.request().newBuilder()
                requestBuilder.addHeader("x-api-key", Constants.KEY)
                it.proceed(requestBuilder.build())
            }
            .build()
    }

    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://newsapi.org/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    single { provideLoggingInterceptor() }
    single { provideHttpClient(get()) }
    single { provideRetrofit(get()) }
}

val databaseModule = module {
    fun provideDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(application, AppDatabase::class.java, "AppDatabase")
            .fallbackToDestructiveMigration()
            .build()
    }

    fun provideNewsDao(database: AppDatabase): NewsDao {
        return database.newsDao
    }

    fun provideBookmarkDao(database: AppDatabase): BookmarkDao {
        return database.bookmarkDao
    }

    single { provideDatabase(androidApplication()) }
    single { provideNewsDao(get()) }
    single { provideBookmarkDao(get()) }
}

val repositoryModule = module {
    fun provideNewsRepository(api: News, newsDao: NewsDao): NewsRepository {
        return NewsRepository(api, newsDao)
    }

    fun provideBookmarkRepository(bookmarkDao: BookmarkDao): BookmarkRepository {
        return BookmarkRepository(bookmarkDao)
    }

    single { provideNewsRepository(get(), get()) }
    single { provideBookmarkRepository(get()) }
}