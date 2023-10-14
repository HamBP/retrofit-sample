package me.algosketch.retrofitsample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
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
    private val articleRepository = ArticleRepository()

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
//        fetchArticleUsingCallback1()
//        fetchArticleUsingCallback2()
//        fetchArticleUsingCoroutine1()
//        fetchWArticleWithResult()
        fetchArticleWithFlow()
    }

    private fun fetchArticleUsingCallback1() {
        _uiState.value = UiState.Loading
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
            _uiState.value = UiState.Loading
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

    private fun fetchArticleUsingCoroutine1() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val response = articleApi.getArticle3(1)
            if (response.isSuccessful) {
                println("코루틴 예시 1 : ${response.body()}")
                _uiState.value = UiState.Success(response.body()!!)
            } else {
                _uiState.value = UiState.Error("요청에 실패했어요.")
            }
        }
    }

    private fun fetchArticleWithResult() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            articleRepository.getArticleWithResult()
                .onSuccess { article ->
                    println("코루틴 + Result 예시 : $article")
                    _uiState.value = UiState.Success(article)
                }
                .onFailure {
                    _uiState.value = UiState.Error(it.message ?: "요청에 실패했어요.")
                }
        }
    }

    private fun fetchArticleWithFlow() {
        articleRepository.getArticleWithFlow()
            .onStart {
                _uiState.value = UiState.Loading
            }
            .onEach { article ->
                println("코루틴 + Flow 예시 : $article")
                _uiState.value = UiState.Success(article)
            }
            .catch {
                _uiState.value = UiState.Error(it.message ?: "요청에 실패했어요.")
            }
            .onCompletion {
                // do something
            }
            .launchIn(viewModelScope)
    }
}