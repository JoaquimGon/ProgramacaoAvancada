package com.minesweep.jogos

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList


class Minesweeper (var dificuldade :  Int) {
    var estado = EstadoJogo.COMECO // Inicio de estado a começo
    val tamanhoGrelha = grelhas[dificuldade]!! // Tamanho da grelha
    val bombas = numBombas[dificuldade]!! // Numero de bombas
    val grelha = mutableListOf<MutableList<Int>>() // Grelha do jogo (números e bombas)
    val listaDescoberto = mutableListOf<MutableList<Boolean>>() // Quadrados descobertos
    val listaSinalizado = mutableListOf<MutableList<Boolean>>() // Quadrados sinalizados

    // Estático para acessar sem objeto
    companion object {
        // Configuracoes dependendo da dificuldade
        val grelhas: Map<Int, Int> = mapOf(0 to 9, 1 to 16, 2 to 16)
        val numBombas: Map<Int, Int> = mapOf(0 to 15, 1 to 40, 2 to 128)

        enum class EstadoJogo {
            GANHOU,
            PERDEU,
            PROXIMO,
            COMECO
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
        this.estado = estado
    }
    fun getEstadoJogo() : EstadoJogo {
        return estado
    }
    fun setDificuldade(dificuldade : Int) {
        this.dificuldade = dificuldade
        // Atualizar grelhas
        criarGrelhas()
    }
    fun getDificuldade() : Int {
        return dificuldade
    }

    // Cria e preenche a grelha com bombas
    // Tamanho e número de bombas depende da dificuldade
    fun criarGrelhas(): List<MutableList<Int>> {
        // Cria a grelha
        for(linhas in 0..<tamanhoGrelha){
            grelha.add(MutableList(tamanhoGrelha) { 0 }.toMutableStateList())
        }
        // Criar lista de quadrados descobertos
        for(linhas in 0..<tamanhoGrelha){
            listaDescoberto.add(MutableList(tamanhoGrelha) { false }.toMutableStateList())
        }
        // Criar lista de quadrados descobertos
        for(linhas in 0..<tamanhoGrelha){
            listaDescoberto.add(MutableList(tamanhoGrelha) { false }.toMutableStateList())
        }

        // Lista para meter as coordenadas das bombas
        var listaBombas = mutableListOf<Pair<Int, Int>>()
        // Lista para randomizar as coordenadas para as bombas
        var coordsAleatorias = mutableListOf<Pair<Int, Int>>()

        // Buscar coordenadas aleatorios
        for (x in 0 until tamanhoGrelha) {
            for (y in 0 until tamanhoGrelha) {
                coordsAleatorias.add(Pair(x, y))
            }
        }

        // Randomizar coordendas
        coordsAleatorias.shuffle()

        // Meter bombas partindo das coords aleatorias, guarda a posicao das bombas
        for(coordenada in 0..bombas-1){
            var (x, y) = coordsAleatorias[coordenada]
            listaBombas.add(Pair(x,y))
            grelha[x][y] = -1
        }


        // Iterar pel
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

        for(linha in 0..<tamanhoGrelha){
            for(coluna in 0..<tamanhoGrelha)
                print("[${grelha[linha][coluna]}]")
            println()
        }

        return grelha
    }

    fun limparJogo(){
        grelha.clear()
        listaSinalizado.clear()
        listaDescoberto.clear()
    }

    // TODO: return type de jeito, ANY não é o melhor

    // Descobre o quadrado selecionado, devolve um estado jogo caso perdeu (clicou
    // em bomba) ou a grelha de descobertos
    fun descobrir(linha: Int, coluna: Int) : Any{
        // Casos para prevenir erros (out of bounds ou
        if (linha !in 0..<tamanhoGrelha || coluna !in 0..<tamanhoGrelha) {
            println("Coordenadas a descobrir estão incorretas!")
            return -1
        }
        // Já descoberto, devolve 0
        if (listaDescoberto[linha][coluna]) return listaDescoberto
        // Atualiza a lista
        listaDescoberto[linha][coluna] = true

        // Caso base (não é uma quadrado vazio)
        if (grelha[linha][coluna] == -1){ // Tem bomba, perde
            return EstadoJogo.PERDEU
        } else if (grelha[linha][coluna] > 0){ // Não é livre, devolve
            return listaDescoberto
        }

        // Caso recursivo (é um quadrado vazio, itera pelos 8 quadrados à volta)
        for ((offsetLinha, offsetColuna) in direcoes) {
            descobrir(linha + offsetLinha, coluna + offsetColuna)
        }
        return listaDescoberto
    }

    // Vai sinalizar ou tirar sinalizador (importante para verificar se
    // ou não)
    fun sinalizar (linha : Int, coluna : Int){
        listaSinalizado[linha][coluna] = !listaSinalizado[linha][coluna]
    }
}

fun main(){
    var jogo = Minesweeper(1)
    jogo.criarGrelhas()
}



















