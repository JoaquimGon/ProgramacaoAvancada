package com.minesweep.jogos

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import kotlin.random.Random


class Minesweep (var dificuldade :  Int) {
    var estadoJogo = EstadoJogo.COMECO

    companion object {
        val grelhaTam: Map<Int, Int> = mapOf(0 to 9, 1 to 16, 2 to 16)
        val numBombas: Map<Int, Int> = mapOf(0 to 15, 1 to 40, 2 to 128)
        val densidadeBombas: Map<Int, Int> = mapOf(0 to 12, 1 to 16, 2 to 50)

        enum class EstadoJogo {
            GANHOU,
            PERDEU,
            PROXIMO,
            COMECO
        }

    }



    // Cria e preenche a grelha com bombas
    // Tamanho e número de bombas depende da dificuldade
    fun criarJogo(): List<MutableList<Int>> {
        val grelha = mutableListOf<MutableList<Int>>()
        var tamanhoGrelha = grelhaTam[dificuldade]!!
        var bombas = numBombas[dificuldade]!!
        var listaBombas = mutableListOf<Pair<Int, Int>>()

        // Cria a grelha
        for(linhas in 0..tamanhoGrelha-1){
            grelha.add(MutableList(tamanhoGrelha) { 0 }.toMutableStateList())
        }

        // Mete bombas
        var coordsAleatorias = mutableListOf<Pair<Int, Int>>()
        for (x in 0 until tamanhoGrelha) {
            for (y in 0 until tamanhoGrelha) {
                coordsAleatorias.add(Pair(x, y))
            }
        }
        coordsAleatorias.shuffle()
        for(coordenada in 0..bombas-1){
            var (x, y) = coordsAleatorias[coordenada]
            listaBombas.add(Pair(x,y))
            grelha[x][y] = -1
        }


        // Fazer lista das direcoes de cada bomba
        val direcoes = listOf(
            -1 to -1, -1 to 0, -1 to 1,
            0 to -1,          0 to 1,
            1 to -1,  1 to 0, 1 to 1
        )

        for ((linhas, colunas) in listaBombas) {
            // Iterar pelas direcoes
            for ((y, x) in direcoes) {
                // Calcular coordenadas novas
                val verficarY = linhas + y
                val verficarX = colunas + x
                if (verficarY in 0 until tamanhoGrelha && verficarX in 0 until tamanhoGrelha &&
                    grelha[verficarY][verficarX] != -1
                ) {
                    grelha[verficarY][verficarX] += 1
                }
            }
        }

        for(linha in 0..tamanhoGrelha-1){
            for(coluna in 0..tamanhoGrelha-1)
                print("[${grelha[linha][coluna]}]")
            println()
        }

        return grelha
    }
}

fun main(){
    var jogo = Minesweep(0)
    jogo.criarJogo()
}



















