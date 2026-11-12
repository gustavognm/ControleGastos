package br.edu.iftm.controlegastos.ui.componentes

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.edu.iftm.controlegastos.R
import br.edu.iftm.controlegastos.data.model.Categoria

/** Desenha um ícone vetorial (SVG convertido) com a cor e o tamanho pedidos. */
@Composable
fun Icone(
    @DrawableRes id: Int,
    descricao: String?,
    modifier: Modifier = Modifier,
    tamanho: Dp = 24.dp,
    cor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Icon(
        painter = painterResource(id),
        contentDescription = descricao,
        tint = cor,
        modifier = modifier.size(tamanho),
    )
}

/** Ícone da categoria dentro de um círculo colorido (usado no painel e na lista). */
@Composable
fun IconeCategoria(categoria: Categoria, modifier: Modifier = Modifier, tamanho: Dp = 40.dp) {
    Box(
        modifier = modifier
            .size(tamanho)
            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icone(
            id = categoria.icone,
            descricao = categoria.rotulo,
            tamanho = tamanho * 0.55f,
            cor = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}

/** Botão de voltar da barra superior. */
@Composable
fun BotaoVoltar(aoVoltar: () -> Unit) {
    IconButton(onClick = aoVoltar) {
        Icone(R.drawable.ic_voltar, descricao = "Voltar")
    }
}
