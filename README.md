# MineSweep - Joaquim Gonçalves

Este projeto tem como objetivo recriar um jogo chamado "minesweep", para um dispositivo android. O jogo em si consiste numa grelha,
onde quando uma casa é revelada, mostra um número, vazia, ou uma bomba. Vazia ou números, representam se existe alguma bomba nas suas 8 direções.
O objetivo é descobrir apenas as casas sem bombas, e marcar as casa que têm bombas.

## Como Jogar

1. **Início da partida**  
   Selecciona-se o nível de dificuldade: Fácil (dificuldade padrão) , Médio ou Difícil. A grelha é gerada com o número de linhas, colunas e minas correspondentes à dificuladade escolhida.
   
3. **Descobrir células**  
   - Clique simples sobre uma célula para a revelar:  
     - Se não contiver mina, apresenta o número de minas adjacentes (0 a 8).  
     - Se não houver minas na vizinhança, as células adjacentes são abertas automaticamente (efeito cascata).  
     - Se contiver mina, a partida termina em derrota.

4. **Marcar minas suspeitas**  
   - Clica-se no toggle de indicador, quando clicar em alguma célula ainda não selecionada, irá marcar com uma bandeira.

5. **Condições de vitória e derrota**  
   - **Vitória**: todas as células que não contêm mina foram reveladas.  
   - **Derrota**: foi aberta uma célula contendo mina.

---

## Funcionalidades

- Escolher as **Dificuldades**  
  - Fácil: 9×9, 10 minas  
  - Médio: 16×16, 40 minas  
  - Difícil: 24×24, 70 minas  

- **Controlo de células**  
  - Clique simples: abrir célula  
  - Clique prolongado: marcar/desmarcar sinalizador  

- **Temporizador**  
  - Inicia assim que recomeça ou comece um jogo

- **Revelação em domino**  
  - Abre automaticamente células adjacentes vazias (sem bombas à volta)

- **Condições de vitória de jogo**  
  - Vitória: todas as casas sem mina abertas e todas as bombas marcadas  
  - Derrota: descobrir casa com mina  

- **Reiniciar partida**  
  - Repõe a grelha e o temporizador, mantendo a dificuldade  

---

## Arquitetura usada:
- Jetpack Compose (UI)
- Kotlin (Lógica)

---

## Instruções instalação / Uso
Abrir como projeto usando o IDE Android Studio, compilar e simular fazendo uso do device manager ( Medium Phone API 36.0)

---
## Algoritmos Usados

### Posicionamento de bombas
Busca uma quantidade correspondente de coordenadas aleatórias correspondente ao número de bombas e posicionar as bombas consoante essa lista aleatória


```kotlin
  var coordsBombas = mutableListOf<Pair<Int, Int>>()

  // (...)

  var listaBombas = mutableListOf<Pair<Int, Int>>()

  coordsBombas.clear()
  // Buscar coordenadas aleatorios
  for (x in 0 until tamanhoGrelha) {
      for (y in 0 until tamanhoGrelha) {
          coordsBombas.add(Pair(x, y))
      }
  }

  // Randomizar coordenadas
  coordsBombas.shuffle()

  // Meter bombas partindo das coords coordenadas, guarda a posicao das bombas
  for(coordenada in 0..bombas-1){
      var (x, y) = coordsBombas[coordenada]
      listaBombas.add(Pair(x,y))
      grelha[x][y].value = -1
  }
```

## Construção da grelha
Para atribuir números aos quadrados, foi usado um sistema de números inteiros: 
  - -1 -> Bomba
 - 0 -> Vazio
 - 1 a 8 -> Número

Tendo esta definição, é usada a lista de bombas previamente mostrada e todos os quadrados é volta (8 sentidos) é incrementado 1.
Isto resulta em, se uma casa tiver bombas em 3 das 8 direções terá o número 3 atribuido, se a casa não tiver bombas mantém o seu valor inicial, 0 visto
que não é incrementado.

```kotlin

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

```

## Descobrir quadrados vazios interligados
Uma mecância comum em jogos de Minesweep. Caso se descobrir uma casa vazia descobre todas as casas vazias ligadas a esse mesmo quadrado.
Foi usada recursão, sendo o caso recursivo um quadrado vazio e o caso base um quadrado com número ou bomba

```kotlin
  // funcao descobrir:

  // (...)

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

```






