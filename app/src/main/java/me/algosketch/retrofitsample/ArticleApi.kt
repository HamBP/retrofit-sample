package me.algosketch.retrofitsample

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ArticleApi {
    @GET("articles/{article_number}")
    fun getArticle1(
        @Path("article_number") articleNumber: Int,
    ): Call<Article>

    @GET("articles/{article_number}")
    suspend fun getArticle2(
        @Path("article_number") articleNumber: Int,
    ): Article

    @GET("articles/{article_number}")
    suspend fun getArticle3(
        @Path("article_number") articleNumber: Int,
    ): Response<Article>
}