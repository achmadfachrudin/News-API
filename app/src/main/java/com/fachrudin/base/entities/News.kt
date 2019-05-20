package com.fachrudin.base.entities

/**
 * @author achmad.fachrudin
 * @date 21-May-19
 */
data class News (
    val status: String,
    val totalResults: Int,
    val articles: List<NewsItem>)