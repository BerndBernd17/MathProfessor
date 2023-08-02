package com.example.mathprofessor

import android.util.Log
import java.util.*

class RPN() {
    companion object {
        fun calculateRPN(calculation: List<String>): Int {
            val rpn = toRPN(calculation)
            if (evaluateRPN(rpn).toFloat() == evaluateRPN(rpn).toInt().toFloat()) {
                return evaluateRPN(rpn).toInt()
            }
            return -1
        }


        fun toRPN(expression: List<String>): String {

            val outputQueue = mutableListOf<String>()
            val operatorStack = Stack<String>()

            val operators = setOf("+", "-", "*", "/")

            for (token in expression) {
                when {
                    token.toDoubleOrNull() != null -> { outputQueue.add(token.toString())
                    }

                    operators.contains(token) -> {
                        while (!operatorStack.isEmpty() && hasHigherPrecedence(
                                operatorStack.peek(),
                                token
                            )
                        ) {
                            outputQueue.add(operatorStack.pop().toString())
                        }
                        operatorStack.push(token)
                    }

                    token == "(" -> operatorStack.push(token)
                    token == ")" -> {
                        while (!operatorStack.isEmpty() && operatorStack.peek() != "(") {
                            outputQueue.add(operatorStack.pop().toString())
                        }
                        operatorStack.pop()
                    }
                }
            }

            while (!operatorStack.isEmpty()) {
                outputQueue.add(operatorStack.pop().toString())
            }

            return outputQueue.joinToString(" ")
        }

        fun hasHigherPrecedence(operator1: String, operator2: String): Boolean {
            val precedence1 = getPrecedence(operator1)
            val precedence2 = getPrecedence(operator2)
            return precedence1 >= precedence2
        }

        fun getPrecedence(operator: String): Int {
            return when (operator) {
                "+", "-" -> 1
                "*", "/" -> 2
                else -> 0
            }
        }


        fun evaluateRPN(expression: String): Double {
            val stack = Stack<Double>()
            for (token in expression.split(" ")) {
                when (token) {
                    "+" -> stack.push(stack.pop() + stack.pop())
                    "-" -> {
                        val a = stack.pop()
                        val b = stack.pop()
                        stack.push(b - a)
                    }

                    "*" -> stack.push(stack.pop() * stack.pop())
                    "/" -> {
                        val a = stack.pop()
                        val b = stack.pop()
                        stack.push(b / a)
                    }

                    else -> stack.push(token.toDouble())
                }
            }
            return stack.pop()
        }
    }
}







/*
class RPN() {
    companion object {
        fun calculate(task: Calculable): Float {
            return if (task is CalculationWith2Numbers) {
                calculate2Numbers(task)
            } else if (task is CalculationWith3Numbers) {
                calculate3Numbers(task)
            } else {
                calculate4Numbers(task as CalculationWith4Numbers)
            }
        }

        fun calculate2Numbers(task: CalculationWith2Numbers): Float {
            return task.operation1.operation(task.number1, task.number2)
        }

        fun calculate3Numbers(task: CalculationWith3Numbers): Float {
            if (task.operation2 == Operation.MULTIPLY || task.operation2 == Operation.DIVIDE) {
                val interimResult = task.operation2.operation(task.number2, task.number3)
                return task.operation1.operation(task.number1, interimResult)
            } else {
                val interimResult = task.operation1.operation(task.number1, task.number2)
                return task.operation2.operation(interimResult, task.number3)
            }
        }

        fun calculate4Numbers(task: CalculationWith4Numbers): Float {
            if (task.operation3 == Operation.MULTIPLY || task.operation3 == Operation.DIVIDE) {
                val interimResult: Float = task.operation3.operation(task.number3, task.number4)
                return calculate3Numbers(
                    CalculationWith3Numbers(
                        task.number1,
                        task.number2,
                        interimResult,
                        task.operation1,
                        task.operation2,
                        0f
                    )
                )
            } else {
                val interimResult =  calculate3Numbers(
                    CalculationWith3Numbers(
                        task.number1,
                        task.number2,
                        task.number3,
                        task.operation1,
                        task.operation2,
                        0f
                    )
                )
                return task.operation3.operation(interimResult, task.number4)
            }
        }
    }
}
 */