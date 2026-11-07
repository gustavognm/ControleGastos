package br.edu.iftm.controlegastos.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.edu.iftm.controlegastos.data.model.Compra
import br.edu.iftm.controlegastos.data.model.Teto

@Database(entities = [Compra::class, Teto::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun compraDao(): CompraDao
    abstract fun tetoDao(): TetoDao

    companion object {
        fun criar(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "controle_gastos.db").build()
    }
}
