package com.example.mathprofessor

import android.util.Log
import kotlin.random.Random

class CalculationAssembler {
    companion object {

        fun generateFirstCalculation(gameStartArguments: GameStartArguments): List<String> {

            val firstCalculation = mutableListOf<String>()
            do {
                firstCalculation.clear()
                firstCalculation.add((Random.nextInt(11 - gameStartArguments.numberStartsWith) + gameStartArguments.numberStartsWith).toString())
                firstCalculation.add(getOperation1().symbol)
                firstCalculation.add((Random.nextInt(11 - gameStartArguments.numberStartsWith) + gameStartArguments.numberStartsWith).toString())
                firstCalculation.add(getOperation2().symbol)
                firstCalculation.add((Random.nextInt(11 - gameStartArguments.numberStartsWith) + gameStartArguments.numberStartsWith).toString())
                if (gameStartArguments.maxTasksWith4Numbers != 0) {
                    firstCalculation.add(getOperation1().symbol)
                    firstCalculation.add((Random.nextInt(11 - gameStartArguments.numberStartsWith) + gameStartArguments.numberStartsWith).toString())
                }
                val solution = RPN.calculateRPN(firstCalculation)
            } while (solution <= 0)

            return firstCalculation
            //      return mutableListOf("9", "-", "6", "/", "3","+","2")
        }


        fun findAllCalculations(firstCalculation: List<String>): List<MutableList<String>> {
            Log.w("Bernd", "findAllCalculations")
            // findet die Gleichungen, welche mit diesen Zahlen zum gleichen Ergebnis führen
            val calculationString =
                mutableListOf(firstCalculation[0], firstCalculation[2], firstCalculation[4])
            val solution = RPN.calculateRPN(firstCalculation)
            if (firstCalculation.size > 5) {
                calculationString.add(firstCalculation[6])
            }
            val permutations = permute(calculationString)  // Permutation = Durchtauschen aller Zahlen
            val permutationsWithPlaceholder = insertPlaceholder(permutations)  // "+" als Platzhalter für die Operatoren einfügen
            val firstValidCalculationList = findValidCalculations(permutationsWithPlaceholder, solution) // alle Gleichungen mit der ursprünglichen Anzahl Zahlen
            val otherValidCalculationList = findValidCalculations(getShorterCalculations(permutationsWithPlaceholder), solution) // alle Gleichungen mit weniger Zahlen
            val validCalculationList = firstValidCalculationList + otherValidCalculationList  // alle validen Gleichungen (inkl. Dopplungen)
            val calculationList = deleteDoubles(validCalculationList) // alle validen Gleicungen ohne Dopplungen
            return calculationList
        }


        fun deleteDoubles(calculationList: List<MutableList<String>>) : List<MutableList<String>> {
            val returnList: MutableList<MutableList<String>> = mutableListOf()
            val helperList: MutableList<MutableList<String>> = mutableListOf()
            for (i in 0 until calculationList.size) {
                if (!(calculationList[i].sorted() in helperList)) {
                    returnList.add(calculationList[i]) // in die neue Liste aufnehmen
                    helperList.add(calculationList[i].sorted().toMutableList())
                }
            }
            return returnList
        }

        fun getShorterCalculations(
            permutationsWithPlaceholder: MutableList<MutableList<String>>
        ): MutableList<MutableList<String>> { // rekursiv!
            // liefert Gleichungen mit den verfügbaren Zahlen - aber nicht mit allen Zahlen, sondern mit weniger Zahlen
            val newPermutationsWithPlaceholder: MutableList<MutableList<String>> = mutableListOf()
            for (i in 0 until permutationsWithPlaceholder.size) {
                newPermutationsWithPlaceholder.add(mutableListOf())
                for (j in 0 until permutationsWithPlaceholder[i].size - 2) { // fügt immer eine Gleichung mehr dazu
                    newPermutationsWithPlaceholder[i].add(permutationsWithPlaceholder[i][j])
                }
            }
            if (newPermutationsWithPlaceholder[0].size > 3) {
                getShorterCalculations(newPermutationsWithPlaceholder)
            }
            return newPermutationsWithPlaceholder
        }



        fun findValidCalculations(permutations: MutableList<MutableList<String>>, solution: Int): MutableList<MutableList<String>> {
            var validCalculations: MutableList<MutableList<String>> = mutableListOf()
            validCalculations.clear()
            for (permutation in permutations) {
                val validList = validCalculations + switchOperators(solution, permutation)
                validCalculations = validList.toMutableList()
            }
            return validCalculations
        }


        fun switchOperators(solution: Int,
                            permutation: MutableList<String>,
                            indexOperator: Int = 1,
                            validCalculations: MutableList<MutableList<String>> = mutableListOf()
        ): MutableList<MutableList<String>> { // rekursiv!!
            val localPermutation = permutation.toMutableList()
            for (i in 0 until 4) {
                localPermutation[indexOperator] = Operation.values()[i].symbol
                if (RPN.calculateRPN(localPermutation) == solution) {
                    validCalculations.add(localPermutation.toMutableList())
                }
                if (indexOperator + 2 < localPermutation.size) {
                    switchOperators(solution, localPermutation, indexOperator + 2, validCalculations)
                }
            }
            return validCalculations
        }


        fun permute(strings: MutableList<String>,  // rekursiv !!
                    start: Int = 0,
                    permutations: MutableList<MutableList<String>> = mutableListOf()
        ): MutableList<MutableList<String>> {
            if (start == strings.size - 1) {
                permutations.add(strings.toMutableList())
            } else {
                for (i in start until strings.size) {
                    swap(strings, start, i)
                    permute(strings, start + 1, permutations)
                    swap(strings, start, i) // Zurücktauschen für die nächste Iteration
                }
            }
            return permutations
        }

        fun swap(strings: MutableList<String>, i: Int, j: Int) {
            val temp = strings[i]
            strings[i] = strings[j]
            strings[j] = temp
        }

        fun insertPlaceholder(listOfTasks: MutableList<MutableList<String>>): MutableList<MutableList<String>> {
            for (task in listOfTasks) {
                for (i in task.size - 1 downTo 1 ) {
                    task.add(i,"+")
               }
            }
            return listOfTasks
        }

        fun getOperation1(): Operation {
            val randomIndex = (Math.random() * 2).toInt()
            return Operation.values()[randomIndex]
        }
        fun getOperation2(): Operation {
            val randomIndex = (Math.random() * 2).toInt() + 2
            return Operation.values()[randomIndex]
        }

        fun calculationExists(validCalculation: MutableList<String>, taskTable: TaskTable): Boolean {
            // guckt ob validCalculation schon auf taskTable ausgegeben wurde
            for (i in 0 until taskTable.calculations.size) {
                if (validCalculation.sorted() == taskTable.calculations[i].sorted()) {
                    return true
                }
            }
            return false
        }


    }

}