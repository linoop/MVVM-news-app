package com.technowaysoftware.mvvmnewsapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.technowaysoftware.mvvmnewsapp.NewsApplication
import com.technowaysoftware.mvvmnewsapp.models.Article
import com.technowaysoftware.mvvmnewsapp.models.NewsResponse
import com.technowaysoftware.mvvmnewsapp.repository.NewsRepository
import com.technowaysoftware.mvvmnewsapp.util.MyResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    app: Application,
    private val newsRepository: NewsRepository
) :
    AndroidViewModel(app) {
    //ViewModel() { AndroidViewModel exist as long as app exit
    val breakingNews: MutableLiveData<MyResource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResponse: NewsResponse? = null

    val searchNews: MutableLiveData<MyResource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null

    init {
        getBreakingNews(countryCode = "IN")
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        safeBreakingNews(countryCode)
    }

    private suspend fun safeBreakingNews(countryCode: String) {
        breakingNews.postValue(MyResource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
                breakingNews.postValue(handleBreakingNewsResponse(response))
            } else {
                breakingNews.postValue(MyResource.Error("No internet connection"))
            }

        } catch (t: Throwable) {
            when (t) {
                is IOException -> breakingNews.postValue(MyResource.Error("Network failure"))
                else -> breakingNews.postValue(MyResource.Error("Conversion error"))
            }
        }

    }


    fun searchNews(searchQuery: String) {
        viewModelScope.launch {
            safeSearchNewsCall(searchQuery)
        }
    }

    private suspend fun safeSearchNewsCall(searchQuery: String) {
        searchNews.postValue(MyResource.Loading())
        try {

            if (hasInternetConnection()) {
                val response = newsRepository.searchNews(searchQuery, searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            } else {
                searchNews.postValue(MyResource.Error("No internet connection"))
            }

        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchNews.postValue(MyResource.Error("Network Failure"))
                else -> searchNews.postValue(MyResource.Error("Conversion Error"))
            }
        }

    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): MyResource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                breakingNewsPage++
                if (breakingNewsResponse == null) {
                    breakingNewsResponse = resultResponse
                } else {
                    val oldArticle = breakingNewsResponse?.articles
                    val newArticle = resultResponse.articles
                    oldArticle?.addAll(newArticle)
                }

                return MyResource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        return MyResource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): MyResource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                searchNewsPage++
                if (searchNewsResponse == null) {
                    searchNewsResponse = it
                } else {
                    val oldArticle = searchNewsResponse?.articles
                    val newArticle = it.articles
                    oldArticle?.addAll(newArticle)
                }

                return MyResource.Success(searchNewsResponse ?: it)
            }
        }
        return MyResource.Error(response.message())
    }

    fun saveArticle(article: Article) = viewModelScope.launch { newsRepository.upInsert(article) }

    fun getSavedNews() = newsRepository.getSavedNews()

    fun deleteArticle(article: Article) = viewModelScope.launch { newsRepository.delete(article) }


    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}