package com.example.auth.di

import com.example.auth.datasource.*
import com.example.auth.repo.UserRepository
import com.example.auth.repo.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
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