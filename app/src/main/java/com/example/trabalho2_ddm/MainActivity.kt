package com.example.trabalho2_ddm.controller

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.trabalho2_ddm.R
import com.example.trabalho2_ddm.data.dao.ContatoDAO
import com.example.trabalho2_ddm.model.Contato

class MainActivity : AppCompatActivity() {

    private lateinit var contatoDAO: ContatoDAO
    private lateinit var lvContatos: ListView
    private lateinit var tvVazio: TextView
    private lateinit var etBusca: EditText
    private lateinit var spFiltroCategoria: Spinner

    private var nomeFiltrado = ""
    private var categoriaFiltrada = "Todas"

    override fun onCreate(estadoSalvo: Bundle?) {
        super.onCreate(estadoSalvo)
        setContentView(R.layout.activity_main)

        contatoDAO = ContatoDAO(this)

        lvContatos = findViewById(R.id.lvContatos)
        tvVazio = findViewById(R.id.tvVazio)
        etBusca = findViewById(R.id.etBusca)
        spFiltroCategoria = findViewById(R.id.spFiltroCategoria)

        val categorias = listOf(
            "Todas",
            "Família",
            "Amigos",
            "Trabalho"
        )

        val adaptador = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categorias
        )

        adaptador.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spFiltroCategoria.adapter = adaptador

        nomeFiltrado = estadoSalvo?.getString("nomeFiltrado") ?: ""

        categoriaFiltrada = estadoSalvo?.getString("categoriaFiltrada")
            ?: "Todas"

        lvContatos.setOnItemClickListener { lista, visao, posicao, id ->
            val contatoSelecionado =
                lista.getItemAtPosition(posicao) as Contato

            val intencao = Intent(
                this,
                CadastroContatoActivity::class.java
            )

            intencao.putExtra("contatoID", contatoSelecionado.id)

            startActivity(intencao)
        }
    }

    override fun onResume() {
        super.onResume()
        listarContatos()
    }

    override fun onSaveInstanceState(estado: Bundle) {
        estado.putString("nomeFiltrado", nomeFiltrado)
        estado.putString("categoriaFiltrada", categoriaFiltrada)

        super.onSaveInstanceState(estado)
    }

    private fun listarContatos() {
        val contatos = contatoDAO.listarContatos(
            nomeFiltrado,
            categoriaFiltrada
        )

        if (contatos.isEmpty()) {
            lvContatos.visibility = View.GONE
            tvVazio.visibility = View.VISIBLE
        } else {
            lvContatos.visibility = View.VISIBLE
            tvVazio.visibility = View.GONE
        }

        val adaptador: ArrayAdapter<Contato> = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            contatos
        )

        lvContatos.adapter = adaptador
    }

    fun novoContato(visao: View) {
        val intencao = Intent(
            this,
            CadastroContatoActivity::class.java
        )

        startActivity(intencao)
    }

    fun filtrarContatos(visao: View) {
        nomeFiltrado = etBusca.text.toString().trim()
        categoriaFiltrada = spFiltroCategoria.selectedItem.toString()

        listarContatos()
    }

    fun listarTodos(visao: View) {
        etBusca.setText("")
        spFiltroCategoria.setSelection(0)

        nomeFiltrado = ""
        categoriaFiltrada = "Todas"

        listarContatos()
    }
}