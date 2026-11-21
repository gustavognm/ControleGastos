package br.edu.iftm.controlegastos.ui.componentes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.edu.iftm.controlegastos.data.model.Compra
import br.edu.iftm.controlegastos.util.Formatador

/**
 * Uma linha da lista de compras (usada no Painel e no Extrato).
 * Se [aoExcluir] for null, o botão "Excluir" não aparece.
 */
@Composable
fun ItemCompra(compra: Compra, aoExcluir: (() -> Unit)? = null) {
    Card(Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = if (aoExcluir != null) 4.dp else 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconeCategoria(compra.categoria)
            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(compra.descricao, fontWeight = FontWeight.SemiBold)
                Text(
                    "${compra.formaPagamento.rotulo} • ${Formatador.dataHora(compra.data)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(Formatador.moeda(compra.valorCentavos), fontWeight = FontWeight.Bold)
                if (aoExcluir != null) {
                    TextButton(onClick = aoExcluir, contentPadding = PaddingValues(horizontal = 8.dp)) {
                        Text("Excluir", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

/** Pede confirmação antes de apagar uma compra. */
@Composable
fun DialogoExcluirCompra(compra: Compra, aoConfirmar: () -> Unit, aoCancelar: () -> Unit) {
    AlertDialog(
        onDismissRequest = aoCancelar,
        title = { Text("Excluir compra?") },
        text = { Text("${compra.descricao}: ${Formatador.moeda(compra.valorCentavos)}") },
        confirmButton = {
            TextButton(onClick = aoConfirmar) { Text("Excluir", color = MaterialTheme.colorScheme.error) }
        },
        dismissButton = { TextButton(onClick = aoCancelar) { Text("Cancelar") } },
    )
}
