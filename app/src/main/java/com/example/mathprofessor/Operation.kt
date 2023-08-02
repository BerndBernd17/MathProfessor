package com.example.mathprofessor

enum class Operation(val symbol: String, val operation: (Float, Float) -> Float) {
    ADD("+", { a, b -> a + b }),
    SUBTRACT("-", { a, b -> a - b }),
    MULTIPLY("*", { a, b -> a * b }),
    DIVIDE("/", { a, b -> if (b != 0f) a / b else throw IllegalArgumentException("Cannot divide by zero") })
}