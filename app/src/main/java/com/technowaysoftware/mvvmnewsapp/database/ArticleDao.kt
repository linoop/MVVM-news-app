package com.technowaysoftware.mvvmnewsapp.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.technowaysoftware.mvvmnewsapp.models.Article

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upInsert(article: Article): Long

    @Query("SELECT * FROM article")
    fun getAllArticles(): LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)
}