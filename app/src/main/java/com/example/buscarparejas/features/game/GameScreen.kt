package com.example.buscarparejas.features.game

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.buscarparejas.domain.model.MemoryCard

@Composable
fun GameScreen(
    name: String,
    onNavigateToResults: (String, Int) -> Unit,
    onExit: () -> Unit,
    viewModel: GameViewModel = viewModel()
) {
    val cards by viewModel.cards.collectAsState()
    var showExitDialog by remember { mutableStateOf(false) }
    BackHandler(enabled = true) {
        showExitDialog = true
    }
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false }, // Si tocan fuera, se cierra
            title = { Text(text = "¡cuidado!") },
            text = { Text(text = "¿Estás seguro de que quieres salir del juego? Perderás tu progreso actual") },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    onExit()
                }) {
                    Text("Salir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
            Box(modifier = Modifier.fillMaxSize()) {
        // Botón de salir en la esquina superior izquierda
        IconButton(
            onClick = { showExitDialog = true},
            modifier = Modifier
                .padding(20.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Salir del juego",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp, start = 30.dp, end = 30.dp, bottom = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Jugador: $name",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(100.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(15.dp),
                horizontalArrangement = Arrangement.spacedBy(15.dp)
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
}

@Composable
fun CardItem(card: MemoryCard, onClick: () -> Unit) {
    val rotation by animateFloatAsState(
        targetValue = if (card.isFlipped || card.isMatched) 180f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "cardFlip"
    )

    // Animación de escala (Pulse effect)
    val scale by animateFloatAsState(
        targetValue = if (card.isSuccess) 1.2f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 200f),
        label = "cardScale"
    )

    // Animación de color cuando hay éxito
    val containerColor by animateColorAsState(
        targetValue = when {
            card.isSuccess -> Color.Green
            rotation > 90f -> MaterialTheme.colorScheme.secondaryContainer
            else -> MaterialTheme.colorScheme.primary
        },
        animationSpec = tween(durationMillis = 300),
        label = "cardColor"
    )

    val borderStroke = if (card.isError) BorderStroke(4.dp, Color.Red) else null

    Card(
        modifier = Modifier
            .aspectRatio(0.7f)
            .graphicsLayer {
                rotationY = rotation
                scaleX = scale
                scaleY = scale
                cameraDistance = 12f * density
            }
            .clickable { if (rotation <= 90f) onClick() },
        border = borderStroke,
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    if (rotation > 90f) {
                        rotationY = 180f
                    }
                }
        ) {
            if (rotation > 90f) {
                Text(
                    text = card.value.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    color = if (card.isSuccess) Color.White else Color.Unspecified
                )
            }
        }
    }
}
