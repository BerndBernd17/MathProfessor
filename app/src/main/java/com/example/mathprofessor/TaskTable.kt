package com.example.mathprofessor

data class TaskTable(
    val firstCalculation: List<String> = emptyList(),
    val calculations: List<MutableList<String>> = emptyList(),
    val solution: Int = 0,
    val numbersOfCalculations: List<Int> = emptyList(),  // das sind die Anzahlen von Gleichungen mit 2, 3 oder 4 Zahlen
    var triggerCompose: Int = 0,
)
