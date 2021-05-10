package com.technowaysoftware.mvvmnewsapp.repository

import com.technowaysoftware.mvvmnewsapp.api.RetrofitClient
import com.technowaysoftware.mvvmnewsapp.database.ArticleDatabase
import com.technowaysoftware.mvvmnewsapp.models.Article
import javax.inject.Inject

class NewsRepository @Inject constructor(val db: ArticleDatabase) {

    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitClient.api.getBreakingNews(countryCode, pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetrofitClient.api.searchForNews(searchQuery, pageNumber)

    suspend fun upInsert(article: Article) = db.getArticleDao().upInsert(article)

    fun getSavedNews() = db.getArticleDao().getAllArticles()

    suspend fun delete(article: Article) = db.getArticleDao().deleteArticle(article)
}