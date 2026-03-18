package com.example.buscarparejas.domain.model

data class MemoryCard(
    val id: Int,
    val value: Int,
    val isFlipped: Boolean = false,
    val isMatched: Boolean = false
)
