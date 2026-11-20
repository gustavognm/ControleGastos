package br.edu.iftm.controlegastos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import br.edu.iftm.controlegastos.ui.navegacao.AppNavHost
import br.edu.iftm.controlegastos.ui.theme.ControleGastosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ControleGastosTheme {
                AppNavHost()
            }
        }
    }
}
