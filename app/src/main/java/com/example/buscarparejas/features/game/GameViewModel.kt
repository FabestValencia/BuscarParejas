package com.example.buscarparejas.features.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buscarparejas.domain.model.MemoryCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {
    private val _cards = MutableStateFlow<List<MemoryCard>>(emptyList())
    val cards = _cards.asStateFlow()
    private var firstSelectedIdx: Int? = null
    private var isProcessing = false
    var score = 0

    init {
        generateBoard()
    }

    private fun generateBoard() {
        val values = (1..8).flatMap { listOf(it, it) }.shuffled()
        _cards.value = values.mapIndexed { index, value ->
            MemoryCard(id = index, value = value)
        }
    }

    fun onCardClicked(index: Int, onGameOver: (Int) -> Unit) {
        val currentCards = _cards.value.toMutableList()
        val card = currentCards[index]
        if (isProcessing || index == firstSelectedIdx || card.isFlipped || card.isMatched) return

        currentCards[index] = card.copy(isFlipped = true)
        _cards.value = currentCards

        if (firstSelectedIdx == null) {
            firstSelectedIdx = index
        } else {
            val firstIdx = firstSelectedIdx!!
            firstSelectedIdx = null
            score++

            if (currentCards[firstIdx].value == currentCards[index].value) {
                currentCards[firstIdx] = currentCards[firstIdx].copy(isMatched = true)
                currentCards[index] = currentCards[index].copy(isMatched = true)
                _cards.value = currentCards

                if (currentCards.all { it.isMatched }) onGameOver(score)
            } else {
                isProcessing = true
                viewModelScope.launch {
                    delay(1000L) // Pausa asíncrona de 1 segundo
                    currentCards[firstIdx] = currentCards[firstIdx].copy(isFlipped = false)
                    currentCards[index] = currentCards[index].copy(isFlipped = false)
                    _cards.value = currentCards.toList()
                    isProcessing = false
                }
            }
        }
    }
}