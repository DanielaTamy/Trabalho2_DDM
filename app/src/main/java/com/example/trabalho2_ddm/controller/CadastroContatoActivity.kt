package com.example.trabalho2_ddm.controller

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.trabalho2_ddm.R
import com.example.trabalho2_ddm.data.dao.ContatoDAO
import com.example.trabalho2_ddm.model.Contato

class CadastroContatoActivity : AppCompatActivity() {

    private lateinit var contatoDAO: ContatoDAO
    private var contatoId: Int = 0

    private lateinit var etNome: EditText
    private lateinit var etTelefone: EditText
    private lateinit var etEmail: EditText
    private lateinit var spCategoria: Spinner
    private lateinit var btnExcluir: Button

    private val categorias = listOf(
        "Família",
        "Amigos",
        "Trabalho"
    )

    override fun onCreate(estadoSalvo: Bundle?) {
        super.onCreate(estadoSalvo)
        setContentView(R.layout.activity_cadastro_contato)

        contatoDAO = ContatoDAO(this)

        etNome = findViewById(R.id.etNome)
        etTelefone = findViewById(R.id.etTelefone)
        etEmail = findViewById(R.id.etEmail)
        spCategoria = findViewById(R.id.spCategoria)
        btnExcluir = findViewById(R.id.btnExcluir)

        val adaptador = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categorias
        )

        adaptador.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spCategoria.adapter = adaptador

        contatoId = intent.getIntExtra("contatoID", 0)

        if (contatoId != 0) {
            btnExcluir.visibility = View.VISIBLE
            carregarContato()
        }
    }

    private fun carregarContato() {
        val contato = contatoDAO.buscarContatoPorId(contatoId)

        if (contato == null) {
            Toast.makeText(
                this,
                "Contato não encontrado",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        etNome.setText(contato.nome)
        etTelefone.setText(contato.telefone)
        etEmail.setText(contato.email)

        val posicao = categorias.indexOf(contato.categoria)

        if (posicao >= 0) {
            spCategoria.setSelection(posicao)
        }
    }

    fun salvarContato(visao: View) {
        val nome = etNome.text.toString().trim()
        val telefoneDigitado = etTelefone.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val categoria = spCategoria.selectedItem.toString()

        if (nome.isEmpty()) {
            mostrarErro(etNome, "Informe o nome")
            return
        }

        // Aceita dígitos e os separadores usuais de telefone.
        val formatoTelefone = Regex("[0-9+()\\s-]+")
        val telefone = telefoneDigitado.replace(Regex("[^0-9]"), "")

        if (!telefoneDigitado.matches(formatoTelefone) ||
            telefone.isEmpty()
        ) {
            mostrarErro(etTelefone, "Informe um telefone válido")
            return
        }

        if (email.isNotEmpty() &&
            !Patterns.EMAIL_ADDRESS.matcher(email).matches()
        ) {
            mostrarErro(
                etEmail,
                "Informe um e-mail válido ou deixe vazio"
            )
            return
        }

        if (contatoDAO.telefoneJaCadastrado(telefone, contatoId)) {
            mostrarErro(etTelefone, "Este telefone já está cadastrado")
            return
        }

        val contato = Contato(
            contatoId,
            nome,
            telefone,
            email,
            categoria
        )

        val sucesso: Boolean

        if (contatoId == 0) {
            sucesso = contatoDAO.inserirContato(contato) != -1L
        } else {
            sucesso = contatoDAO.atualizarContato(contato) == 1
        }

        if (sucesso) {
            Toast.makeText(
                this,
                "Contato salvo",
                Toast.LENGTH_SHORT
            ).show()

            finish()
        } else {
            Toast.makeText(
                this,
                "Não foi possível salvar o contato",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun excluirContato(visao: View) {
        if (contatoId == 0) return

        AlertDialog.Builder(this)
            .setTitle("Excluir contato")
            .setMessage("Deseja excluir este contato?")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Excluir") { janela, opcao ->
                val linhasExcluidas =
                    contatoDAO.excluirContato(contatoId)

                if (linhasExcluidas == 1) {
                    Toast.makeText(
                        this,
                        "Contato excluído",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Não foi possível excluir",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .show()
    }

    fun cancelar(visao: View) {
        finish()
    }

    private fun mostrarErro(
        campo: EditText,
        mensagem: String
    ) {
        campo.error = mensagem
        campo.requestFocus()

        Toast.makeText(
            this,
            mensagem,
            Toast.LENGTH_SHORT
        ).show()
    }
}