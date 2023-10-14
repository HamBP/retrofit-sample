package me.algosketch.retrofitsample

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ArticleRepository {
    private val articleApi: ArticleApi = RetrofitFactory.create()

    suspend fun getArticleWithResult(): Result<Article> = kotlin.runCatching {
        val response = articleApi.getArticle3(1)

        if (response.isSuccessful) response.body() ?: throw RuntimeException("이럴 수가...")
        else throw RuntimeException("통신 과정에서 에러가 발생했어요.")
    }

    fun getArticleWithFlow(): Flow<Article> = flow {
        val response = articleApi.getArticle3(1)

        if (response.isSuccessful) {
            val article = response.body() ?: throw RuntimeException("이럴 수가...")
            emit(article)
        } else {
            throw RuntimeException("통신 과정에서 에러가 발생했어요.")
        }
    }
}