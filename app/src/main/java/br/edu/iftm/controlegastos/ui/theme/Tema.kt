package br.edu.iftm.controlegastos.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import br.edu.iftm.controlegastos.util.NivelAlerta

// Cores do alerta de consumo (mesmas do protótipo no Figma)
val VerdeOk = Color(0xFF2E7D32)
val AmareloAtencao = Color(0xFFF9A825)
val VermelhoEstouro = Color(0xFFC62828)
val CinzaSemTeto = Color(0xFF9E9E9E)

fun corDoNivel(nivel: NivelAlerta): Color = when (nivel) {
    NivelAlerta.VERDE -> VerdeOk
    NivelAlerta.AMARELO -> AmareloAtencao
    NivelAlerta.VERMELHO -> VermelhoEstouro
    NivelAlerta.SEM_TETO -> CinzaSemTeto
}

private val CoresClaras = lightColorScheme(
    primary = Color(0xFF1B5E20),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC8E6C9),
    onPrimaryContainer = Color(0xFF0A2E0C),
    secondary = Color(0xFF455A64),
    background = Color(0xFFF6F8F6),
    surface = Color.White,
    error = VermelhoEstouro,
)

private val CoresEscuras = darkColorScheme(
    primary = Color(0xFF81C784),
    onPrimary = Color(0xFF0A2E0C),
    primaryContainer = Color(0xFF2E5E31),
    onPrimaryContainer = Color(0xFFC8E6C9),
    secondary = Color(0xFFB0BEC5),
    error = Color(0xFFEF9A9A),
)

@Composable
fun ControleGastosTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) CoresEscuras else CoresClaras,
        content = content,
    )
}
