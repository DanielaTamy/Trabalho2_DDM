package com.example.trabalho2_ddm.data.dao
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.trabalho2_ddm.data.db.DBHelper
import com.example.trabalho2_ddm.model.Contato
class ContatoDAO(private val contexto: Context) {
    private val dbHelper = DBHelper(contexto)
    fun inserirContato(contato: Contato): Long {
        val banco = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("nome", contato.nome)
            put("telefone", contato.telefone)
            put("email", contato.email)
            put("categoria", contato.categoria)
        }
        val id = banco.insert(DBHelper.NOME_TABELA, null, valores)
        banco.close()
        return id
    }
    fun listarContatos(
        nome: String = "",
        categoria: String = "Todas"
    ): List<Contato> {
        val banco = dbHelper.readableDatabase
        val condicoes = mutableListOf<String>()
        val argumentos = mutableListOf<String>()
        if (nome.isNotEmpty()) {
            // Busca literal por parte do nome, sem curingas.
            condicoes.add("instr(lower(nome), lower(?)) > 0")
            argumentos.add(nome)
        }
        if (categoria != "Todas") {
            condicoes.add("categoria = ?")
            argumentos.add(categoria)
        }
        val selecao = if (condicoes.isEmpty()) null
        else condicoes.joinToString(" AND ")
        val parametros = if (argumentos.isEmpty()) null
        else argumentos.toTypedArray()
        val cursor = banco.query(
            DBHelper.NOME_TABELA, null, selecao, parametros,
            null, null, "nome COLLATE NOCASE ASC, id ASC"
        )
        val contatos = mutableListOf<Contato>()
        while (cursor.moveToNext()) {
            contatos.add(lerContato(cursor))
        }
        cursor.close()
        banco.close()
        return contatos
    }
    fun buscarContatoPorId(id: Int): Contato? {
        val banco = dbHelper.readableDatabase
        val cursor = banco.query(
            DBHelper.NOME_TABELA, null, "id = ?",
            arrayOf(id.toString()), null, null, null
        )
        var contato: Contato? = null
        if (cursor.moveToFirst()) {
            contato = lerContato(cursor)
        }
        cursor.close()
        banco.close()
        return contato
    }
    fun telefoneJaCadastrado(telefone: String, id: Int): Boolean {
        val banco = dbHelper.readableDatabase
        val cursor = banco.query(
            DBHelper.NOME_TABELA, arrayOf("id"),
            "telefone = ? AND id <> ?",
            arrayOf(telefone, id.toString()), null, null, null
        )
        val existe = cursor.moveToFirst()
        cursor.close()
        banco.close()

        return existe
    }
    fun atualizarContato(contato: Contato): Int {
        val banco = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("nome", contato.nome)
            put("telefone", contato.telefone)
            put("email", contato.email)
            put("categoria", contato.categoria)
        }
        val linhasAlteradas = banco.update(
            DBHelper.NOME_TABELA, valores, "id = ?",
            arrayOf(contato.id.toString())
        )
        banco.close()
        return linhasAlteradas
    }
    fun excluirContato(id: Int): Int {
        val banco = dbHelper.writableDatabase
        val linhasExcluidas = banco.delete(
            DBHelper.NOME_TABELA, "id = ?", arrayOf(id.toString())
        )
        banco.close()
        return linhasExcluidas
    }
    private fun lerContato(cursor: Cursor): Contato {
        val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
        val nome = cursor.getString(cursor.getColumnIndexOrThrow("nome"))
        val telefone = cursor.getString(
            cursor.getColumnIndexOrThrow("telefone")
        )
        val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
        val categoria = cursor.getString(
            cursor.getColumnIndexOrThrow("categoria")
        )
        return Contato(id, nome, telefone, email, categoria)
    }
}