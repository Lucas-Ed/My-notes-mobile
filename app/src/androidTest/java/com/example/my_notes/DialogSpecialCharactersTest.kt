package com.example.my_notes

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.my_notes.data.model.Category
import com.example.my_notes.data.model.Note
import com.example.my_notes.ui.components.CategoryDialog
import com.example.my_notes.ui.components.NoteDialog
import com.example.my_notes.ui.theme.MyNotesTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test

/**
 * Testes de UI (instrumentação) que garantem que os campos de
 * texto dos modais aceitam caracteres especiais — acentos,
 * símbolos e pontuação — exatamente como digitados.
 *
 * Rodar com um emulador/dispositivo conectado:
 * ./gradlew connectedDebugAndroidTest
 */
class DialogSpecialCharactersTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun noteDialog_tituloEConteudoAceitamCaracteresEspeciais() {
        val specialTitle = "Ação! @#\$ ção & <tag> — \"ok\" 100%"
        val specialContent = "Configurações (v2.0); R\$ 5,00 | ç,ã,é 🚀"
        var savedNote: Note? = null

        rule.setContent {
            MyNotesTheme {
                NoteDialog(
                    note = null,
                    categories = listOf(
                        Category(id = 1, name = "Ação & Revisão", color = "#ffffff")
                    ),
                    onDismiss = {},
                    onSave = { savedNote = it },
                    onDelete = {}
                )
            }
        }

        rule.onNodeWithText("Título").performTextInput(specialTitle)
        rule.onNodeWithText("Conteúdo").performTextInput(specialContent)

        // Os caracteres digitados aparecem no campo
        rule.onNodeWithText(specialTitle).assertExists()
        rule.onNodeWithText(specialContent).assertExists()

        // Com formulário válido, o botão Salvar está habilitado
        rule.onNodeWithText("Salvar").assertIsEnabled()
        rule.onNodeWithText("Salvar").performClick()

        rule.runOnIdle {
            assertNotNull(savedNote)
            assertEquals(specialTitle, savedNote!!.title)
            assertEquals(specialContent, savedNote!!.content)
        }
    }

    @Test
    fun categoryDialog_nomeAceitaCaracteresEspeciais() {
        val specialName = "Configurações ç,ã,é & \"favoritas\" #1 !?"
        var savedCategory: Category? = null

        rule.setContent {
            MyNotesTheme {
                CategoryDialog(
                    category = null,
                    onDismiss = {},
                    onSave = { savedCategory = it },
                    onDelete = {}
                )
            }
        }

        rule.onNodeWithText("Nome").performTextInput(specialName)
        rule.onNodeWithText(specialName).assertExists()

        rule.onNodeWithText("Criar").assertIsEnabled()
        rule.onNodeWithText("Criar").performClick()

        rule.runOnIdle {
            assertNotNull(savedCategory)
            assertEquals(specialName, savedCategory!!.name)
        }
    }
}
