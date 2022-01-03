package com.example.hihello.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SharedPrefModule {

    @Provides
    fun getAppSharedPref(application: Application) =
        application.getSharedPreferences("app_shared_perf", MODE_PRIVATE)
}