package com.example.mathprofessor

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mathprofessor.ui.theme.MathProfessorTheme



@Composable
fun Start(
    gameViewModel: GameViewModel = viewModel(),
    ) {

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.StartScreen.route) {
        composable(route = Screen.StartScreen.route) {
            StartScreen(gameViewModel, navController = navController)
        }
        composable(
            route = Screen.GameScreen.route
        ) { entry ->
            GameScreen(navController, gameViewModel.taskTable.value, gameViewModel)
        }
    }
}


@Composable
fun StartScreen(gameViewModel: GameViewModel, navController: NavController) {
    gameViewModel.clearInput()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(50.dp)
    ) {
        val mContext = LocalContext.current
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(vertical = 10.dp),
            onClick = {
                navController.navigate(Screen.GameScreen.route)
                gameViewModel.startGame(1)
            }
        ) {
            Text(stringResource(R.string.beginner))
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(vertical = 10.dp),
            onClick = {
                navController.navigate(Screen.GameScreen.route)
                gameViewModel.startGame(2)
            }
        ) {
            Text(stringResource(R.string.expert))
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(vertical = 10.dp),
            onClick = {
                navController.navigate(Screen.GameScreen.route)
                gameViewModel.startGame(3)
            }
        ) {
            Text(stringResource(R.string.professor))
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .height(50.dp),
            onClick = {
                navController.navigate(Screen.GameScreen.route)
                gameViewModel.startGame(4)
            }
        ) {
            Text(stringResource(R.string.genius))
        }
    }
}

@Composable
fun GameScreen( navController: NavController, taskTable: TaskTable, gameViewModel: GameViewModel) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        val configuration = remember { mutableStateOf(LocalConfiguration) }

        TopRow(navController = navController, taskTable)
        Spacer(modifier = Modifier.height(50.dp))
        Table(taskTable = taskTable, gameViewModel, configuration)
        Column() {
            Hints()
            UserInput(gameViewModel, configuration)
        }
        Column() {
            NumberButtons(gameViewModel, taskTable)
            OperatorButtons(gameViewModel)
        }
    }
}

@Composable
fun NumberButtons(gameViewModel: GameViewModel, taskTable: TaskTable) {
    Row(
        modifier = Modifier
            .padding(vertical = 15.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {// die Buttons für die Zahlen
        for (i in 0 until (taskTable.firstCalculation.size + 1) step 2) {
            Button(
                modifier = Modifier
                    .size(50.dp),
                onClick = { gameViewModel.outputOnScreen(taskTable.firstCalculation[i]) }
            ) {
                Text(
                    text = taskTable.firstCalculation[i],
                    softWrap = false
                )
            }
        }
    }
}

@Composable
fun OperatorButtons(gameViewModel: GameViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {// die Buttons für die Operatoren
        Button(
            modifier = Modifier
                .size(50.dp)
                .clip(shape = CircleShape),
            onClick = { gameViewModel.outputOnScreen("+") }
        ) {
            Text("+")
        }
        Button(
            modifier = Modifier
                .size(50.dp)
                .clip(shape = CircleShape),
            onClick = { gameViewModel.outputOnScreen("-") }
        ) {
            Text("-")
        }
        Button(
            modifier = Modifier
                .size(50.dp)
                .clip(shape = CircleShape),
            onClick = { gameViewModel.outputOnScreen("*") }
        ) {
            Text("*")
        }
        Button(
            modifier = Modifier
                .size(50.dp)
                .clip(shape = CircleShape),
            onClick = { gameViewModel.outputOnScreen("/") }
        ) {
            Text("/")
        }
    }
}


@Composable
fun UserInput(gameViewModel: GameViewModel, configuration: MutableState<ProvidableCompositionLocal<Configuration>>) { // die Zeile mit den 2 Buttons aussen und Textfeld innen

    val calculationOnScreen by remember { gameViewModel.calculationOnScreen }

    Row(
        modifier = Modifier
            .padding(vertical = 10.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            modifier = Modifier
                .size(50.dp),
            onClick = { gameViewModel.showSolution() }
        ) {
            Text("?")
        }
        // Textfeld für die Ausgabe des eingetippten Tasks
        Box(
            modifier = Modifier
                .height(50.dp)
                .width((configuration.component1().current.screenWidthDp - 140).dp) // 2x50 für die Buttons 2x20 padding
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Text(text = gameViewModel.convertListToString(calculationOnScreen))
        }
        // Button "löschen"
        Button(
            modifier = Modifier
                .size(50.dp),
            onClick = { gameViewModel.clearInput() }
        ) {
            Text("X")
        }
    }

}


@Composable
fun Hints() {
    Box(  // die Anzeige von Jokern und Punkten
        modifier = Modifier
            .padding(vertical = 10.dp)
            .fillMaxWidth(),
    ) {
        Text(
            text = "Joker(=Lösung?) und Punkte",
            modifier = Modifier.align(Center)
        )
    }
}


@Composable
fun Table(taskTable: TaskTable,  gameViewModel: GameViewModel, configuration: MutableState<ProvidableCompositionLocal<Configuration>>) {

    Column(  // alle Gleichungen
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(100.dp)
    ) {
        if (taskTable.numbersOfCalculations.size > 0) {
            for (i in 0 until taskTable.numbersOfCalculations[0] + taskTable.numbersOfCalculations[1] + taskTable.numbersOfCalculations[2]) {
                OneRowOfTheTable(taskTable.calculations[i], configuration)
            }
        }
    }



}


@Composable
fun TopRow(navController: NavController, taskTable: TaskTable) {
    Row(  // oberste Zeile
        modifier = Modifier
            .padding(vertical = 10.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) { // Back-Button und Ergebnis
        Button(
            modifier = Modifier
                .size(50.dp),
            onClick = { navController.navigate(Screen.StartScreen.route) }
        ) {
            Text("<")
        }
        Box(
            modifier = Modifier
                .size(50.dp)
                .padding(0.dp)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center

        ) {
            Text(text = taskTable.solution.toString())
        }
        Box(
            modifier = Modifier
                .size(50.dp),
        ) {} // als Platzhalter für die Ausrichtung
    }

}

@Composable
fun OneRowOfTheTable(calculation: MutableList<String>, configuration: MutableState<ProvidableCompositionLocal<Configuration>>) {
    val countNumbers = (calculation.size + 1) / 2;  // die Länge der auszugebenden Gleichung

    val widthOfElement = configuration.component1().current.screenWidthDp / 7

    Row(
        modifier = Modifier
            .height(widthOfElement.dp)
            .fillMaxWidth()

    ) {// Zahlen
        for (j in 1..countNumbers) {
            Box(
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .size(widthOfElement.dp)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                    Text(text = calculation[j * 2 - 2])
            }
            if (j < countNumbers) { // die Operatoren
                Box(
                    modifier = Modifier
                        .size((widthOfElement * 0.6f).dp)
                        .clip(shape = CircleShape)
                        .align(CenterVertically)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center

                ) {
                    Text(text = calculation[j * 2 - 1])
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewStartScreen() {
    MathProfessorTheme {
        StartScreen(viewModel(), rememberNavController())
    }
}


///////////////////////////////////////////////////////////////////////////

@Preview(showBackground = true)
@Composable
fun PreviewGameScreen() {
    MathProfessorTheme {
        GameScreen(rememberNavController(), TaskTable(
            firstCalculation = listOf("10", "+", "5", "*", "8", "+", "7"),
            calculations = listOf(mutableListOf("10", "+", "5", "*", "8", "+", "7"), mutableListOf("10", "*", "7", "-", "8", "-", "5"), mutableListOf("10", "*", "5", "+", "7")),
            solution = 3,
            numbersOfCalculations=listOf(0, 1, 2)),
            gameViewModel = GameViewModel()
            )
    }
}