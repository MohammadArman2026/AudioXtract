package com.example.audioxtract.di

import android.content.Context
import com.example.audioxtract.data.datasource.MediaExtractorDataSource
import com.example.audioxtract.data.repository.AudioExtractorRepositoryImpl
import com.example.audioxtract.domain.repository.AudioExtractorRepository
import com.example.audioxtract.domain.usecase.ExtractAudioUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataSource(@ApplicationContext context: Context): MediaExtractorDataSource {
        return MediaExtractorDataSource(context)
    }

    @Provides
    @Singleton
    fun provideRepository(
        dataSource: MediaExtractorDataSource,
        @ApplicationContext context: Context
    ): AudioExtractorRepository {
        return AudioExtractorRepositoryImpl(context,dataSource)
    }

    @Provides
    @Singleton
    fun provideUseCase(
        repository: AudioExtractorRepository
    ): ExtractAudioUseCase {
        return ExtractAudioUseCase(repository)
    }
}
