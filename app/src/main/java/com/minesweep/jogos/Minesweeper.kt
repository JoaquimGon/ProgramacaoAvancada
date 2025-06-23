package com.minesweep.jogos

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList


class Minesweeper (var dificuldade :  Int) {
    var estado = mutableStateOf(EstadoJogo.JOGAR) // Inicio de estado a começo
    val tamanhoGrelha get() = grelhas[dificuldade]!!
    val bombas get() = numBombas[dificuldade]!!
    val grelha = mutableListOf<MutableList<MutableState<Int>>>() // Grelha do jogo (números e bombas)
    val listaDescoberto = mutableListOf<MutableList<MutableState<Boolean>>>() // Quadrados descobertos
    val listaSinalizado = mutableListOf<MutableList<MutableState<Boolean>>>() // Quadrados sinalizados
    // Contar quadrados sinalizados e descobertos para ver se ganhou
    var numDescobertos = 0
    // Lista para randomizar as coordenadas para as bombas
    var coordsBombas = mutableListOf<Pair<Int, Int>>()

    // Estático para acessar sem objeto
    companion object {
        // Configuracoes dependendo da dificuldade
        val grelhas: Map<Int, Int> = mapOf(0 to 9, 1 to 12, 2 to 12)
        val numBombas: Map<Int, Int> = mapOf(0 to 15, 1 to 30, 2 to 50)

        enum class EstadoJogo {
            GANHOU,
            PERDEU,
            JOGAR
        }

        // Fazer lista das direcoes de cada casa
        val direcoes = listOf(
            -1 to -1, -1 to 0, -1 to 1,
            0 to -1,          0 to 1,
            1 to -1,  1 to 0, 1 to 1
        )
    }

    // Setters e getters
    fun setEstadoJogo(estado : EstadoJogo) {
        this.estado.value = estado
    }
    fun getEstadoJogo() : EstadoJogo {
        return estado.value
    }

    fun mudarDificuldade(dificuldade : Int) {
        this.dificuldade = dificuldade
        limparJogo()
        criarGrelhas()
    }

    // Cria e preenche a grelha com bombas
    // Tamanho e número de bombas depende da dificuldade
    fun criarGrelhas(){
        // Limpa e criar grelhas novas
        limparJogo()
        setEstadoJogo(EstadoJogo.JOGAR)

        for (linhas in 0 until tamanhoGrelha) {
            listaDescoberto.add(MutableList(tamanhoGrelha) { mutableStateOf(false) })
            listaSinalizado.add(MutableList(tamanhoGrelha) { mutableStateOf(false) })
            grelha.add(MutableList(tamanhoGrelha) { mutableIntStateOf(0) })
        }

        // Lista para meter as coordenadas das bombas
        var listaBombas = mutableListOf<Pair<Int, Int>>()

        coordsBombas.clear()
        // Buscar coordenadas aleatorios
        for (x in 0 until tamanhoGrelha) {
            for (y in 0 until tamanhoGrelha) {
                coordsBombas.add(Pair(x, y))
            }
        }

        // Randomizar coordendas
        coordsBombas.shuffle()

        // Meter bombas partindo das coords aleatorias, guarda a posicao das bombas

        for(coordenada in 0..bombas-1){
            var (x, y) = coordsBombas[coordenada]
            listaBombas.add(Pair(x,y))
            grelha[x][y].value = -1
        }

        // Iterar pelas direcoes
        for ((linhas, colunas) in listaBombas) {
            // Iterar pelas direcoes
            for ((y, x) in direcoes) {
                // Calcular coordenadas novas
                val verficarY = linhas + y
                val verficarX = colunas + x
                if (verficarY in 0 until tamanhoGrelha && verficarX in 0 until tamanhoGrelha &&
                    grelha[verficarY][verficarX].value != -1
                ) {
                    grelha[verficarY][verficarX].value += 1
                }
            }
        }

        for(linha in 0..<tamanhoGrelha){
            for(coluna in 0..<tamanhoGrelha)
                print("[${grelha[linha][coluna].value}]")
            println()
        }
    }


    // Descobre o quadrado selecionado, devolve um estado jogo caso perdeu (clicou
    // em bomba) ou a grelha de descobertos
    fun descobrir(linha: Int, coluna: Int){
        // Casos para prevenir erros (out of bounds ou
        if (linha !in 0..<tamanhoGrelha || coluna !in 0..<tamanhoGrelha) {
            println("Coordenadas a descobrir estão incorretas!")
            return
        }
        // Já descoberto, devolve lista inalterada
        if (listaDescoberto[linha][coluna].value) return
        // Atualiza a lista
        listaDescoberto[linha][coluna].value = true
        numDescobertos++



        //Verificar se ganhou
        var numSinalizados = 0
        for(bomba in coordsBombas){
            // Se bomba tiver sinalizada corretamente adicionar à contagem
            if(listaSinalizado[bomba.first][bomba.second].value) {
                numSinalizados++
            }
        }
        if(
            numSinalizados == numBombas[dificuldade] &&
            numDescobertos == tamanhoGrelha*tamanhoGrelha - numBombas[dificuldade]!!
        ){
            setEstadoJogo(EstadoJogo.GANHOU)
        }


        // Caso base (não é um quadrado vazio)
        if (grelha[linha][coluna].value == -1) {
            setEstadoJogo(EstadoJogo.PERDEU)
            abrirMapa()
            return
        } else if (grelha[linha][coluna].value > 0) {
            return
        }


        // Caso recursivo (é um quadrado vazio, itera pelos 8 quadrados à volta)
        for ((offsetLinha, offsetColuna) in direcoes) {
            descobrir(linha + offsetLinha, coluna + offsetColuna)
        }
    }

    fun limparJogo(){
        grelha.clear()
        listaDescoberto.clear()
        listaSinalizado.clear()
    }


    fun abrirMapa(){
        for(linha in 0..<tamanhoGrelha){
            for(coluna in 0..<tamanhoGrelha){
                listaDescoberto[linha][coluna].value = true
            }
        }
    }





    // Vai sinalizar ou tirar sinalizador (importante para verificar se
    // ou não)
    fun sinalizar (linha : Int, coluna : Int){
        // Apenas os não desobertos
        if (!listaDescoberto[linha][coluna].value){
            listaSinalizado[linha][coluna].value = !listaSinalizado[linha][coluna].value
        }
    }
}

