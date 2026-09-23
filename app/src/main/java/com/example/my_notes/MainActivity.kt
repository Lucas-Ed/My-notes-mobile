package com.example.my_notes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.my_notes.ui.feature.NotesScreen
import com.example.my_notes.ui.theme.MyNotesTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity principal do aplicativo My Notes.
 * Inicializa a interface Jetpack Compose com o tema do aplicativo
 * e exibe a tela de notas/categorias.
 *
 * A anotação @AndroidEntryPoint permite que o Hilt injete
 * automaticamente as dependências nesta Activity.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Habilita a exibição de borda a borda (edge-to-edge)
        enableEdgeToEdge()
        // Define o conteúdo da Activity em Jetpack Compose
        setContent {
            MyNotesTheme {
                NotesScreen()
            }
        }
    }
}
