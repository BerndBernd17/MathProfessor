package com.example.mathprofessor


sealed class Screen(val route: String) {
    object StartScreen: Screen("start_screen")
    object GameScreen: Screen("game_screen")

}
