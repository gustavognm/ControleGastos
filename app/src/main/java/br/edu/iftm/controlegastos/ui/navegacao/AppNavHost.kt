package br.edu.iftm.controlegastos.ui.navegacao

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.edu.iftm.controlegastos.ControleGastosApp
import br.edu.iftm.controlegastos.ui.auth.AuthViewModel
import br.edu.iftm.controlegastos.ui.auth.CadastroScreen
import br.edu.iftm.controlegastos.ui.auth.LoginScreen
import br.edu.iftm.controlegastos.ui.compra.NovaCompraScreen
import br.edu.iftm.controlegastos.ui.compra.NovaCompraViewModel
import br.edu.iftm.controlegastos.ui.extrato.ExtratoScreen
import br.edu.iftm.controlegastos.ui.extrato.ExtratoViewModel
import br.edu.iftm.controlegastos.ui.painel.PainelScreen
import br.edu.iftm.controlegastos.ui.painel.PainelViewModel
import br.edu.iftm.controlegastos.ui.tetos.TetosScreen
import br.edu.iftm.controlegastos.ui.tetos.TetosViewModel

/** Nomes das telas (rotas) do app. */
object Rotas {
    const val LOGIN = "login"
    const val CADASTRO = "cadastro"
    const val PAINEL = "painel"
    const val NOVA_COMPRA = "nova_compra"
    const val TETOS = "tetos"
    const val EXTRATO = "extrato"
}

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    val app = LocalContext.current.applicationContext as ControleGastosApp
    val auth = app.authRepository

    // Se já existe usuário logado, abre direto no painel.
    val inicio = if (auth.uidAtual != null) Rotas.PAINEL else Rotas.LOGIN

    fun abrirPainel() = navController.navigate(Rotas.PAINEL) {
        popUpTo(navController.graph.id) { inclusive = true } // remove login/cadastro da pilha
    }

    NavHost(navController = navController, startDestination = inicio) {

        composable(Rotas.LOGIN) {
            LoginScreen(
                viewModel = viewModel { AuthViewModel(auth) },
                aoAutenticar = ::abrirPainel,
                irParaCadastro = { navController.navigate(Rotas.CADASTRO) },
            )
        }

        composable(Rotas.CADASTRO) {
            CadastroScreen(
                viewModel = viewModel { AuthViewModel(auth) },
                aoAutenticar = ::abrirPainel,
                voltarParaLogin = { navController.voltar() },
            )
        }

        composable(Rotas.PAINEL) {
            val uid = auth.uidAtual ?: return@composable
            PainelScreen(
                viewModel = viewModel { PainelViewModel(app.gastosRepository, uid) },
                emailUsuario = auth.emailAtual,
                irParaNovaCompra = { navController.navigate(Rotas.NOVA_COMPRA) },
                irParaTetos = { navController.navigate(Rotas.TETOS) },
                irParaExtrato = { navController.navigate(Rotas.EXTRATO) },
                aoSair = {
                    auth.sair()
                    navController.navigate(Rotas.LOGIN) { popUpTo(navController.graph.id) { inclusive = true } }
                },
            )
        }

        composable(Rotas.NOVA_COMPRA) {
            val uid = auth.uidAtual ?: return@composable
            NovaCompraScreen(
                viewModel = viewModel { NovaCompraViewModel(app.gastosRepository, uid) },
                aoVoltar = { navController.voltar() },
            )
        }

        composable(Rotas.EXTRATO) {
            val uid = auth.uidAtual ?: return@composable
            ExtratoScreen(
                viewModel = viewModel { ExtratoViewModel(app.gastosRepository, uid) },
                aoVoltar = { navController.voltar() },
            )
        }

        composable(Rotas.TETOS) {
            val uid = auth.uidAtual ?: return@composable
            TetosScreen(
                viewModel = viewModel { TetosViewModel(app.gastosRepository, uid) },
                aoVoltar = { navController.voltar() },
            )
        }
    }
}

/** Volta uma tela, ignorando cliques repetidos durante a animação de saída. */
private fun NavHostController.voltar() {
    if (currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) popBackStack()
}
