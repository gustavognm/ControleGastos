package br.edu.iftm.controlegastos

import android.app.Application
import br.edu.iftm.controlegastos.data.local.AppDatabase
import br.edu.iftm.controlegastos.data.repository.AuthRepository
import br.edu.iftm.controlegastos.data.repository.GastosRepository

/**
 * Classe que vive enquanto o app estiver aberto. Cria UMA vez o banco e os
 * repositórios, e os ViewModels os pegam daqui (injeção de dependência manual).
 */
class ControleGastosApp : Application() {

    val authRepository by lazy { AuthRepository() }

    val gastosRepository by lazy {
        val banco = AppDatabase.criar(this)
        GastosRepository(banco.compraDao(), banco.tetoDao())
    }
}
