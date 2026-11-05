package br.edu.iftm.controlegastos.data.model

import androidx.annotation.DrawableRes
import br.edu.iftm.controlegastos.R

/**
 * Categorias de gasto. As três primeiras são as "essenciais" do dia a dia;
 * OUTROS serve para qualquer compra que não se encaixe nelas.
 *
 * O ícone de cada categoria é um SVG convertido para Vector Drawable
 * (arquivos em res/drawable, com os originais em docs/icones).
 */
enum class Categoria(
    val rotulo: String,
    @param:DrawableRes val icone: Int,
    val exemploEstabelecimento: String,
) {
    SUPERMERCADO("Supermercado/Feira", R.drawable.ic_supermercado, "Ex.: Supermercado Bretas"),
    GASOLINA("Combustível/Transporte", R.drawable.ic_combustivel, "Ex.: Posto Petrobras"),
    FARMACIA("Farmácia/Saúde", R.drawable.ic_farmacia, "Ex.: Drogaria Minas"),
    OUTROS("Outros", R.drawable.ic_outros, "Ex.: Loja do bairro"),
}
