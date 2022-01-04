package com.example.hihello.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SharedPrefModule {

    @Provides
    fun getAppSharedPref(application: Application): SharedPreferences =
        application.getSharedPreferences("app_shared_perf", MODE_PRIVATE)
}