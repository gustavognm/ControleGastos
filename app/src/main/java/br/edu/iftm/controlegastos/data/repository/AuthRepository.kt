package br.edu.iftm.controlegastos.data.repository

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.tasks.await

/**
 * Único ponto do app que conversa com o Firebase Authentication.
 * Os ViewModels não conhecem o Firebase: só chamam estas funções.
 */
class AuthRepository(private val auth: FirebaseAuth = FirebaseAuth.getInstance()) {

    /** Identificador do usuário logado, ou null se ninguém estiver logado. */
    val uidAtual: String? get() = auth.currentUser?.uid

    val emailAtual: String? get() = auth.currentUser?.email

    suspend fun entrar(email: String, senha: String): Result<Unit> = executar {
        auth.signInWithEmailAndPassword(email, senha).await()
    }

    suspend fun cadastrar(email: String, senha: String): Result<Unit> = executar {
        auth.createUserWithEmailAndPassword(email, senha).await()
    }

    fun sair() = auth.signOut()

    private suspend fun executar(acao: suspend () -> Unit): Result<Unit> =
        try {
            acao()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(traduzirErro(e)))
        }

    /** Converte os erros do Firebase (em inglês) em mensagens amigáveis. */
    private fun traduzirErro(e: Exception): String = when (e) {
        is FirebaseAuthWeakPasswordException -> "Senha fraca: use pelo menos 6 caracteres."
        is FirebaseAuthUserCollisionException -> "Já existe uma conta com este e-mail."
        is FirebaseAuthInvalidUserException -> "Conta não encontrada ou desativada."
        is FirebaseAuthInvalidCredentialsException -> "E-mail ou senha incorretos."
        is FirebaseNetworkException -> "Sem conexão com a internet. Tente novamente."
        else -> "Não foi possível concluir: ${e.localizedMessage ?: "erro desconhecido"}"
    }
}
