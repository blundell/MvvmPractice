package com.blundell.mvvmpractice

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

sealed class CharactersListState {
    object Empty : CharactersListState()
    object Loading : CharactersListState()
    data class Success(val characters: List<String>) : CharactersListState()
    data class Failure(val errorMessage: String) : CharactersListState()
}

class CharactersListViewModel : ViewModel() {

    val state: MutableStateFlow<CharactersListState> = MutableStateFlow(CharactersListState.Empty)

    private val repository: CharactersRepository

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://rickandmortyapi.com/api/")
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder()
                        .addLast(KotlinJsonAdapterFactory())
                        .build()
                )
            )
            .build()
        val rickAndMortyApiService = retrofit.create(RickAndMortyApiService::class.java)
        val networkDataSource = NetworkDataSource(rickAndMortyApiService)
        repository = CharactersRepository(networkDataSource)
        loadCharacters()
    }

    private fun loadCharacters() {
        state.value = CharactersListState.Loading
        repository.getCharacters()
            .onEach {
                state.value = CharactersListState.Success(it)
            }
            .catch {
                Log.e("TUT", "Error in flow.", it)
                state.value = CharactersListState.Failure("Error, please check your network connection and try again.")
            }
            .launchIn(viewModelScope)
    }

}
