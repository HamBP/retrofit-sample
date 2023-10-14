package me.algosketch.retrofitsample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

sealed interface UiState {
    object Loading : UiState

    data class Success(
        val article: Article
    ) : UiState

    data class Error(
        val message: String
    ) : UiState
}

class MainViewModel : ViewModel() {
    private val articleApi: ArticleApi = RetrofitFactory.create()

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
//        fetchArticleUsingCallback1()
        fetchArticleUsingCallback2()
    }

    private fun fetchArticleUsingCallback1() {
        val call = articleApi.getArticle1(1)
        call.enqueue(object : Callback<Article> {
            override fun onResponse(call: Call<Article>, response: Response<Article>) {
                if (response.isSuccessful) {
                    println("콜백 방식 1 : ${response.body()}")
                    _uiState.value = UiState.Success(response.body()!!)
                } else {
                    _uiState.value = UiState.Error("요청에 실패했어요.")
                }
            }

            override fun onFailure(call: Call<Article>, t: Throwable) {}
        })
    }

    private fun fetchArticleUsingCallback2() {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                articleApi.getArticle1(1).execute()
            }

            if (response.isSuccessful) {
                println("콜백 방식 2 : ${response.body()}")
                _uiState.value = UiState.Success(response.body()!!)
            } else {
                _uiState.value = UiState.Error("요청에 실패했어요.")
            }
        }
    }
}