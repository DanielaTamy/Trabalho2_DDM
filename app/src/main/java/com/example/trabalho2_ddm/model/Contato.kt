package com.example.trabalho2_ddm.model

data class Contato(
    val id: Int = 0,
    val nome: String,
    val telefone: String,
    val email: String,
    val categoria: String
) {
    override fun toString(): String {
        return nome
    }
}
