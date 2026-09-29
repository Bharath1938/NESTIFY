package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.data.local.NestifyDatabase
import com.example.data.repository.NestifyRepository
import com.example.ui.NestifyApp
import com.example.ui.NestifyViewModel
import com.example.ui.NestifyViewModelFactory
import com.example.ui.theme.NestifyTheme

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Initialize Room Database & Repository
    val database = NestifyDatabase.getDatabase(applicationContext, lifecycleScope)
    val repository = NestifyRepository(database.nestifyDao())
    val factory = NestifyViewModelFactory(repository)
    val viewModel = ViewModelProvider(this, factory)[NestifyViewModel::class.java]

    setContent {
      NestifyTheme {
        NestifyApp(viewModel = viewModel)
      }
    }
  }
}
