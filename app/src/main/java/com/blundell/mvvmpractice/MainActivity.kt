@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.blundell.mvvmpractice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blundell.mvvmpractice.ui.theme.MvvmPracticeTheme

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<CharactersListViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MvvmPracticeTheme {
                val state by viewModel.state.collectAsStateWithLifecycle()
                when (val s = state) {
                    CharactersListState.Empty -> {
                        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                            Greeting("Android - Empty")
                        }
                    }

                    is CharactersListState.Failure -> {
                        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                            Greeting("Android - Failed ${s.errorMessage}")
                        }
                    }

                    CharactersListState.Loading -> {
                        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                            Greeting("Android - Loading")
                        }
                    }

                    is CharactersListState.Success -> {
                        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                            LazyColumn {
                                items(s.characters) { item ->
                                    ListItem(
                                        modifier = Modifier.clickable { /** TODO **/ },
                                        headlineText = {
                                            Text(item)
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MvvmPracticeTheme {
        Greeting("Android")
    }
}
