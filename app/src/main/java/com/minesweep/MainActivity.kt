package com.minesweep

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.semantics.SemanticsActions.OnClick
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.minesweep.jogos.Minesweeper
import com.minesweep.ui.theme.MinesweepTheme
import kotlin.math.sin


// TODO: Meter venceu ou perdeu, tempo, meter visuais de geito (se me apetecer  hehehe)


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MinesweepTheme (){
                var dificuldadeUI = remember { mutableStateOf("Fácil") }
                var flag = remember { mutableStateOf(false) }
                var versaoJogo = remember { mutableStateOf(0) } // new version state
                var dificuldade = when(dificuldadeUI.value){
                    "Fácil" -> 0
                    "Médio" -> 1
                    "Difícil" -> 2
                    else -> 0
                }

                val jogo = remember(dificuldade, versaoJogo.value) {
                    Minesweeper(dificuldade).apply { criarGrelhas() }
                }

                Column (
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    JogoDifTempoUI(
                        dificuldadeUI = dificuldadeUI,
                    )
                    JogoGrelhaUI(
                        dificuldade = dificuldade,
                        flag = flag.value,
                        jogo = jogo
                    )
                    FlagRecomecarUI(
                        flag = flag,
                        recomecar = {versaoJogo.value++ },
                    )
                }
            }
        }
    }
}


// Dificuldade e Temporizador (DropdownMenu e
@Composable
fun JogoDifTempoUI(dificuldadeUI : MutableState<String>){
    var expanded by remember { mutableStateOf(false) }


    val opcoes = listOf("Fácil", "Médio", "Difícil")

    // Seccao (para paddings etc)
    Box() {
        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ){
                Text("Dificuldade")
                Text("Tempo")
            }
            Row() {
                Box (modifier = Modifier.width(100.dp)) {
                    Row (
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = dificuldadeUI.value,
                            modifier = Modifier
                                .padding(end = 10.dp)
                        )

                        Button(onClick = { expanded = !expanded }) {
                            Text("v")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(opcoes[0]) },
                                onClick = {
                                    dificuldadeUI.value = "Fácil"
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(opcoes[1]) },
                                onClick = {
                                    dificuldadeUI.value = "Médio"
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(opcoes[2]) },
                                onClick = {
                                    dificuldadeUI.value = "Difícil"
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Box {

                }
            }
        }
    }
}



@Composable
// Para atualizar
fun JogoGrelhaUI(dificuldade : Int, jogo : Minesweeper, flag : Boolean) {
    Box(contentAlignment = Alignment.Center) {
        // Coluna de linhas
        Column (
                Modifier
                    .height(600.dp)
                    .aspectRatio(1f)
                    .background(Color.Black)
        ) {
            // Várias linhas
            for(linha in 0..<jogo.tamanhoGrelha) {
                Row (
                    Modifier
                        .fillMaxWidth()
                ) {
                    // Várias colunas
                    for(coluna in 0..<jogo.tamanhoGrelha){
                        var texto = ""
                        var corBackground = 0xFFD3D3D3

                        if (jogo.listaDescoberto[linha][coluna].value){
                            corBackground =0xFFA9A9A9
                        }

                        texto = when {
                            jogo.listaSinalizado[linha][coluna].value -> "🚩"
                            !jogo.listaDescoberto[linha][coluna].value -> ""
                            jogo.grelha[linha][coluna].value == -1 -> "💣"
                            jogo.grelha[linha][coluna].value > 0 -> jogo.grelha[linha][coluna].value.toString()
                            else -> ""
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)        // equal width for each column
                                .aspectRatio(1f)   // ensures square cell
                                .border(1.dp, Color.Black)
                                .padding(0.2f.dp)
                                .clickable {
                                    if (flag) {
                                        jogo.sinalizar(linha, coluna)
                                    } else if (!jogo.listaSinalizado[linha][coluna].value) {
                                        jogo.descobrir(linha, coluna)
                                    }
                                }
                                .background(color = Color(corBackground)), // blue-like color
                            contentAlignment = Alignment.Center
                        ) {
                            Text(texto)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FlagRecomecarUI(
    flag : MutableState<Boolean>,
    recomecar : () -> Unit
){
    Column (Modifier.fillMaxWidth()) {
        Button (
            onClick = { flag.value = !flag.value },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (!flag.value) {
                Text("ENTRAR modo sinalizador (🚩)")
            } else {
                Text("SAIR modo sinalizador (🚩)")
            }
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { recomecar() }
        ) {
            Text("Recomeçar jogo")
        }
    }
}

