package com.example.auth.app.di

import com.example.auth.data.datasource.*
import com.example.auth.data.repo.UserRepository
import com.example.auth.data.repo.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(ViewModelComponent::class, SingletonComponent::class)
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

    @Binds
    abstract fun getFirebaseDataSource(dataSource: FirebaseDataSourceImpl): FirebaseDataSource

    @Binds
    abstract fun getUserRepo(repo: UserRepositoryImpl): UserRepository
}