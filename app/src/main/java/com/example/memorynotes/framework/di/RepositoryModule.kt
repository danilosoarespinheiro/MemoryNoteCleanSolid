package com.example.memorynotes.framework.di

import android.app.Application
import com.example.core.repository.NoteRepository
import com.example.memorynotes.framework.RoomNoteDataSource
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {

    @Provides
    fun provideRepository(app: Application) = NoteRepository(RoomNoteDataSource(app))
}