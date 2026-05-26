package com.sanjoy.videoplayer.core.di

import com.sanjoy.videoplayer.data.repository.VideoRepositoryImpl
import com.sanjoy.videoplayer.domain.repository.VideoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that tells the app:
 *
 * When VideoRepository is requested,
 * provide VideoRepositoryImpl.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindVideoRepository(
        videoRepositoryImpl: VideoRepositoryImpl
    ): VideoRepository
}