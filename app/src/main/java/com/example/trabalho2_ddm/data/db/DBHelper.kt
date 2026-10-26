package com.example.trabalho2_ddm.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(contexto: Context) :
    SQLiteOpenHelper(contexto, NOME_BANCO, null, VERSAO_BANCO) {

    companion object {
        const val NOME_BANCO = "contatos.db"
        const val VERSAO_BANCO = 1
        const val NOME_TABELA = "contatos"
    }

    override fun onCreate(banco: SQLiteDatabase) {
        val criarTabela = """
            CREATE TABLE $NOME_TABELA (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                telefone TEXT NOT NULL UNIQUE,
                email TEXT NOT NULL,
                categoria TEXT NOT NULL
            )
        """.trimIndent()
        banco.execSQL(criarTabela)
    }

    override fun onUpgrade(
        banco: SQLiteDatabase,
        versaoAntiga: Int,
        versaoNova: Int
    ) {
        banco.execSQL("DROP TABLE IF EXISTS $NOME_TABELA")
        onCreate(banco)
    }
}
