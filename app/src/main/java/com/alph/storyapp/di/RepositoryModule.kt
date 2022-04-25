package com.alph.storyapp.di

import com.alph.storyapp.repository.story.StoryRepository
import com.alph.storyapp.repository.story.StoryRepositoryImpl
import com.alph.storyapp.repository.user.UserRepository
import com.alph.storyapp.repository.user.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindStoryRepository(storyRepositoryImpl: StoryRepositoryImpl): StoryRepository

    @Binds
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

}