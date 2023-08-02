package com.example.mathprofessor

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel: ViewModel()  {

    private val _taskTable: MutableState<TaskTable> = mutableStateOf(TaskTable())
    val taskTable: MutableState<TaskTable>
        get() {
            return _taskTable
        }

    var validCalculations: List<MutableList<String>> = emptyList()

    val calculationOnScreen = mutableStateOf<MutableList<String>>(mutableStateListOf())  // das wird gerade eingetipt



    fun startGame(level: Int) {
        val gameStartArguments = when (level) {
            1 -> { GameStartArguments(0, 2, 1, 2, 0, 0, 1, 3, 3, 1) }
            2 -> { GameStartArguments(0, 3, 1, 4, 0, 0, 2, 5, 3, 2) }
            3 -> { GameStartArguments(0, 2, 1, 3, 0, 3, 2, 5, 4, 2) }
            else -> { GameStartArguments(0, 2, 1, 3, 0, 3, 2, 5, 4, 2) }
        }

        _taskTable.value = clearTaskTable(getTaskTable(gameStartArguments))



    }

    fun clearTaskTable(table: TaskTable): TaskTable {

        val calculations =  mutableListOf<MutableList<String>>()

        for (i in 0 until table.calculations.size) {  // erstellt eine DeepCopy
            calculations.add(table.calculations[i].toMutableList())
            for (j in 0 until table.calculations[i].size) {
                calculations[i][j] = ""
            }
        }
        return TaskTable(table.firstCalculation, calculations, table.solution, table.numbersOfCalculations)
    }

    @SuppressLint("LongLogTag")
    fun getTaskTable(gameStartArguments: GameStartArguments): TaskTable {

        var firstCalculation: List<String>
//        var validCalculations: List<MutableList<String>>
        var numbersOfCalculations: List<Int>

        do {
            firstCalculation = CalculationAssembler.generateFirstCalculation(gameStartArguments)  // eine erste Gleichung finden
            validCalculations = CalculationAssembler.findAllCalculations(firstCalculation)  // alle anderen passenden Gleichungen finden
            numbersOfCalculations = getNumbersOfCalculations(validCalculations)
        } while (!(
                    numbersOfCalculations[0] >= gameStartArguments.minTasksWith2Numbers &&
                            numbersOfCalculations[0] <= gameStartArguments.maxTasksWith2Numbers &&
                            numbersOfCalculations[1] >= gameStartArguments.minTasksWith3Numbers &&
                            numbersOfCalculations[1] <= gameStartArguments.maxTasksWith3Numbers &&
                            numbersOfCalculations[2] >= gameStartArguments.minTasksWith4Numbers &&
                            numbersOfCalculations[2] <= gameStartArguments.maxTasksWith4Numbers &&
                            numbersOfCalculations[0] + numbersOfCalculations[1] + numbersOfCalculations[2] >= gameStartArguments.minTasksTotal &&
                            numbersOfCalculations[0] + numbersOfCalculations[1] + numbersOfCalculations[2] <= gameStartArguments.maxTasksTotal
                    )
        )
        Log.w("Bernd validCalculations", validCalculations.toString())

        return TaskTable(firstCalculation, validCalculations, RPN.calculateRPN(firstCalculation), numbersOfCalculations)
    }

    fun getNumbersOfCalculations(calculations: List<MutableList<String>>): List<Int> {
        val numbersOfCalculations = mutableListOf(0,0,0)
        for (i in 0 until calculations.size) {
            if (calculations[i].size == 3) {
                numbersOfCalculations[0]++
            }
            if (calculations[i].size == 5) {
                numbersOfCalculations[1]++
            }
            if (calculations[i].size == 7) {
                numbersOfCalculations[2]++
            }
        }
        return numbersOfCalculations
    }

    fun outputOnScreen(str: String) {
        // hier wird nach jeder Eingabe geprüft, ob das eine Lösung ist
        calculationOnScreen.value.add(str)
        if (calculationOnScreen.value.size >= 3 && (calculationOnScreen.value.size + 1) % 2 == 0) {
           if (_taskTable.value.solution == RPN.calculateRPN(calculationOnScreen.value)) { // wenn es eine Lösung ist
               if (!CalculationAssembler.calculationExists(calculationOnScreen.value, _taskTable.value)) { // wenn diese Lösung noch nicht existiert
                   outputOnTable(calculationOnScreen.value)
                   calculationOnScreen.value.clear()
               } else { // wenn es eine Lösung ist, dieses Ergebnis aber schon existiert - gucken ob es noch eine Lösung werden kann

                   Log.w("Bernd schon vorhanden:", calculationOnScreen.value.toTypedArray().toString())

                   canBecomeASolution()



                   // TODO Fehlermeldung: Lösung schon vorhanden
                   // TODO siehe GameControler N1414 - N1450
               }
            }
        }
    }


    private fun canBecomeASolution(): Boolean {
        // wenn keine Ausgabemöglichkeit für einen String besteht, der länger als calculationOnScreen ist, dann return
        if (calculationOnScreen.value.size >= longestEmptyCalculation()) {
            return false
        }



        // noch offene Felder suchen, gucken ob schon alle Zahlen getipt
        calculationOnScreen.value
        _taskTable.value.numbersOfCalculations

        Log.w("Bernd calculation", calculationOnScreen.value.toString())
        Log.w("Bernd _taskTable", _taskTable.value.toString())  // hier sind die bereits ausgegebenen Lösungen drin

        return true
    }

    private fun longestEmptyCalculation(): Int {
        // gibt die Länge der längsten noch freien Ausgabemöglichkeit aus
        for (i in 0 until _taskTable.value.calculations.size) {
            if (_taskTable.value.calculations[i][0] == "") {
                // wenn eine leere Zeile gefunden wird, dann ist das automatisch die längste leere Zeile, weil die Prüfung wird mit den längsten Zeilen angefangen
                return _taskTable.value.calculations[i].size
            }
        }
        return -1
    }


    private fun outputOnTable(calculation: MutableList<String>) {
        // gibt eine Gleichung auf den Table aus
        val table =  _taskTable.value.copy()
        for (i in 0 until table.calculations.size) {  // geht die Zeilen des Tables durch
            if (table.calculations[i].size == calculation.size && table.calculations[i][0] == "") {
                for (j in 0 until table.calculations[i].size) {    // geht alle Zeichen einer Zeile durch
                    table.calculations[i][j] = calculation[j]
                    table.triggerCompose++
                }
                break
            }
        }
        _taskTable.value = table.copy()
    }


    fun clearInput() {
        calculationOnScreen.value.clear()
    }

    fun convertListToString(list: List<String>): String {
        var str = ""
        for (item in list) {
            str += item
        }
        return str
    }


    fun showSolution() {
        // das soll eine Lösung anzeigen, wenn der Benutzer einen "Joker" zieht
        rows@ for (i in 0 until _taskTable.value.calculations.size) {   // geht durch alle Zeilen auf TaskTable
            if (_taskTable.value.calculations[i][0] == "") {  // die erste leere Zeile finden
                val size = _taskTable.value.calculations[i].size // die Länge dieser Zeile abfragen
                for (j in 0 until validCalculations.size) { // durch die validen Lösungen gehen und eine passende Calculation ausgeben
                    if (validCalculations[j].size == size && !CalculationAssembler.calculationExists(validCalculations[j], _taskTable.value)) {
                        outputOnTable(validCalculations[j])
                        calculationOnScreen.value.clear()
                        break@rows
                    }
                }
            }
        }
    }
}