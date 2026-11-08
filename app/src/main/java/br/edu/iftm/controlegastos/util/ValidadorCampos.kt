package br.edu.iftm.controlegastos.util

/** Validações de texto usadas nas telas de login e cadastro. */
object ValidadorCampos {

    private val formatoEmail = Regex("""^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$""")

    const val TAMANHO_MINIMO_SENHA = 6

    /** Retorna a mensagem de erro, ou null se o e-mail estiver correto. */
    fun erroEmail(email: String): String? = when {
        email.isBlank() -> "Informe o e-mail."
        !formatoEmail.matches(email.trim()) -> "E-mail inválido."
        else -> null
    }

    fun erroSenha(senha: String): String? = when {
        senha.isEmpty() -> "Informe a senha."
        senha.length < TAMANHO_MINIMO_SENHA -> "A senha precisa de pelo menos $TAMANHO_MINIMO_SENHA caracteres."
        else -> null
    }
}
