package com.technowaysoftware.mvvmnewsapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.technowaysoftware.mvvmnewsapp.R
import com.technowaysoftware.mvvmnewsapp.database.ArticleDatabase
import com.technowaysoftware.mvvmnewsapp.repository.NewsRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_news.*

@AndroidEntryPoint
class NewsActivity : AppCompatActivity() {

    //lateinit var viewModel: NewsViewModel
    val viewModel: NewsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        //val newsRepository = NewsRepository(ArticleDatabase(this))
        //val viewModelProviderFactory = NewsViewModelProviderFactory(application, newsRepository)
        //viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)

        viewModel

        bottomNavView.setupWithNavController(fragmentContainer.findNavController())
    }
}