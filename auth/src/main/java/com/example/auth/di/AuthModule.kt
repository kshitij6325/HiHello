package com.example.auth.di

import androidx.room.RoomDatabase
import com.example.auth.datasource.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @MeLocalDataSourceType
    abstract fun getMeLocalDataSource(dataSource: MeLocalDataSource): UserDataSource

    @Binds
    @UserFirebaseDataSourceType
    abstract fun getUserFirebaseDataSource(dataSource: UserFirebaseDataSource): UserDataSource

    @Binds
    @UserRoomDataSourceType
    abstract fun getUserRoomDatasource(dataSource: UserRoomDataSource): UserDataSource
}