package com.technowaysoftware.mvvmnewsapp

import android.content.Context
import com.technowaysoftware.mvvmnewsapp.database.ArticleDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NewsModule {
    @Singleton
    @Provides
    fun provideArticleDatabase(@ApplicationContext context: Context) = ArticleDatabase(context)
}