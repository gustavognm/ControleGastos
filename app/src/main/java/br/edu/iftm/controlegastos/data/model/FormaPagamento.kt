package br.edu.iftm.controlegastos.data.model

enum class FormaPagamento(val rotulo: String) {
    PIX("Pix"),
    DINHEIRO("Dinheiro"),
    CARTAO_CREDITO("Cartão de Crédito"),
    CARTAO_DEBITO("Cartão de Débito"),
}
