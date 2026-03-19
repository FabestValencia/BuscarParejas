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
        if (isProcessing) return
        
        val currentList = _cards.value.toMutableList()
        val card = currentList.getOrNull(index) ?: return
        
        if (index == firstSelectedIdx || card.isFlipped || card.isMatched) return

        // Voltear la carta seleccionada
        currentList[index] = card.copy(isFlipped = true)
        _cards.value = currentList.toList()

        if (firstSelectedIdx == null) {
            firstSelectedIdx = index
        } else {
            val firstIdx = firstSelectedIdx!!
            firstSelectedIdx = null
            score++

            if (currentList[firstIdx].value == currentList[index].value) {
                // Es un par coincidente
                viewModelScope.launch {
                    val matchedList = _cards.value.toMutableList()
                    // Activar estado de éxito (verde + escala)
                    matchedList[firstIdx] = matchedList[firstIdx].copy(isMatched = true, isSuccess = true)
                    matchedList[index] = matchedList[index].copy(isMatched = true, isSuccess = true)
                    _cards.value = matchedList.toList()
                    
                    delay(1000L) // Mantener el efecto de éxito por 1 segundo para hacer que el jugador se sienta familiarizado
                    
                    val finalizedList = _cards.value.toMutableList()
                    finalizedList[firstIdx] = finalizedList[firstIdx].copy(isSuccess = false)
                    finalizedList[index] = finalizedList[index].copy(isSuccess = false)
                    _cards.value = finalizedList.toList()
                    
                    if (finalizedList.all { it.isMatched }) onGameOver(score)
                }
            } else {
                // No coinciden: bloqueo de entrada y espera de 2 segundos
                isProcessing = true
                viewModelScope.launch {
                    // Mostrar error (borde rojo)
                    val errorList = _cards.value.toMutableList()
                    errorList[firstIdx] = errorList[firstIdx].copy(isError = true)
                    errorList[index] = errorList[index].copy(isError = true)
                    _cards.value = errorList.toList()
                    
                    delay(2000L) // Tiempo de espera de 2 segundos para que el jugador tenga un momento para memorizar
                    
                    // Ocultar cartas y limpiar error
                    val resetList = _cards.value.toMutableList()
                    resetList[firstIdx] = resetList[firstIdx].copy(isFlipped = false, isError = false)
                    resetList[index] = resetList[index].copy(isFlipped = false, isError = false)
                    _cards.value = resetList.toList()

                    isProcessing = false
                }
            }
        }
    }
}
