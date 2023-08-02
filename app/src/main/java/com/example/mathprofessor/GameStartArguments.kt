package com.example.mathprofessor

data class GameStartArguments(
    val minTasksWith2Numbers: Int,
    val maxTasksWith2Numbers: Int,
    val minTasksWith3Numbers: Int,
    val maxTasksWith3Numbers: Int,
    val minTasksWith4Numbers: Int,
    val maxTasksWith4Numbers: Int,
    val minTasksTotal: Int,
    val maxTasksTotal: Int,
    val countNumbers: Int,
    val numberStartsWith: Int,
)
