package com.example.buscarparejas.features.results

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ResultsScreen(
    name: String,
    score: Int,
    onRestart: () -> Unit
) {
    // Utilizamos una Column centrada para organizar los elementos visuales [3], [4]
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título de felicitaciones con estilo de Material Design [5]
        Text(
            text = "¡Felicidades, $name!",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp)) [6]

        // Se muestra el puntaje final obtenido del juego [1]
        Text(
            text = "Has completado el juego en",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "$score intentos",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botón para reiniciar el juego y volver a la pantalla de inicio [7]
        Button(
            onClick = { onRestart() }
        ) {
            Text(text = "Volver a jugar")
        }
    }
}