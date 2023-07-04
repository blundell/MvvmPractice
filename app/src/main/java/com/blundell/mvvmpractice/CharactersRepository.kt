package com.blundell.mvvmpractice

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import retrofit2.http.GET

class CharactersRepository(
    private val networkDataSource: NetworkDataSource,
) {

    fun getCharacters(): Flow<List<String>> = flow {
        val result = networkDataSource.getCharacters()
        if (result.isSuccess) {
            val apiCharacters = result.getOrThrow()
            emit(apiCharacters.map { it.name })
        } else {
            error("Error loading. Do you have an internet connection?")
        }
    }

}

class NetworkDataSource(
    private val apiService: RickAndMortyApiService,
) {
    suspend fun getCharacters(): Result<List<ApiCharacter>> {
        try {
            val response = apiService.characters()
            if (response.isSuccessful) {
                return Result.success(response.body()!!.results)
            } else {
                return Result.failure(IllegalStateException("${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("TUT", "Failure in network data source.", e)
            return Result.failure(IllegalStateException("Likely network error.", e))
        }
    }

}

interface RickAndMortyApiService {
    @GET("character")
    suspend fun characters(): Response<RickAndMortyCharacterResponse>
}

data class RickAndMortyCharacterResponse(
    val info: ApiInfo,
    val results: List<ApiCharacter>,
)

data class ApiInfo(
    val count: Int,
    val pages: Int,
    val next: String?,
    val previous: String?,
)

data class ApiCharacter(
    val id: Int,
    val name: String,
    val image: String,
)
