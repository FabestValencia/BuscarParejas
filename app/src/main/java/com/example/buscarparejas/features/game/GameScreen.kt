package com.example.buscarparejas.features.game

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.buscarparejas.domain.model.MemoryCard

@Composable
fun GameScreen(
    name: String,
    onNavigateToResults: (String, Int) -> Unit,
    viewModel: GameViewModel = viewModel()
) {
    val cards by viewModel.cards.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Jugador: $name",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(cards) { card ->
                CardItem(card = card) {
                    // Al hacer clic, enviamos el ID al ViewModel y definimos qué hacer al ganar
                    viewModel.onCardClicked(card.id) { finalScore ->
                        onNavigateToResults(name, finalScore)
                    }
                }
            }
        }
    }
}

@Composable
fun CardItem(card: MemoryCard, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (card.isFlipped || card.isMatched)
                MaterialTheme.colorScheme.secondaryContainer
            else
                MaterialTheme.colorScheme.primary
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (card.isFlipped || card.isMatched) {
                Text(
                    text = card.value.toString(),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}