package com.example.my_notes

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.my_notes.data.model.Category
import com.example.my_notes.data.model.Note
import com.example.my_notes.ui.dialogs.CategoryDialog
import com.example.my_notes.ui.dialogs.NoteDialog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Testes de UI (instrumentação) que garantem que os campos de
 * texto dos modais aceitam caracteres especiais — acentos,
 * símbolos e pontuação — exatamente como digitados.
 *
 * Rodar com um emulador/dispositivo conectado:
 * ./gradlew connectedDebugAndroidTest
 */
@RunWith(AndroidJUnit4::class)
class DialogSpecialCharactersTest {

    private fun launchHost(): ActivityScenario<DialogHostActivity> =
        ActivityScenario.launch(DialogHostActivity::class.java)

    @Test
    fun noteDialog_tituloEConteudoAceitamCaracteresEspeciais() {
        val specialTitle = "Ação! @#\$ ção & <tag> — \"ok\" 100%"
        val specialContent = "Configurações (v2.0); R\$ 5,00 | ç,ã,é 🚀"
        var savedNote: Note? = null

        launchHost().use { scenario ->
            scenario.onActivity { activity ->
                NoteDialog.show(
                    activity = activity,
                    note = null,
                    categories = listOf(
                        Category(id = 1, name = "Ação & Revisão", color = "#ffffff")
                    ),
                    onSave = { savedNote = it },
                    onDelete = {}
                )
            }

            // replaceText cobre qualquer caractere (acentos, símbolos,
            // emojis) sem depender de key events do teclado virtual
            onView(withId(R.id.etNoteTitle)).perform(replaceText(specialTitle))
            onView(withId(R.id.etNoteContent)).perform(replaceText(specialContent))

            // Os caracteres digitados aparecem no campo
            onView(withId(R.id.etNoteTitle))
                .check(matches(withText(specialTitle)))
            onView(withId(R.id.etNoteContent))
                .check(matches(withText(specialContent)))

            // Com formulário válido, o botão Salvar está habilitado
            onView(withText("Salvar")).check(matches(isEnabled()))
            onView(withText("Salvar")).perform(click())
        }

        assertNotNull(savedNote)
        assertEquals(specialTitle, savedNote!!.title)
        assertEquals(specialContent, savedNote!!.content)
    }

    @Test
    fun categoryDialog_nomeAceitaCaracteresEspeciais() {
        val specialName = "Configurações ç,ã,é & \"favoritas\" #1 !?"
        var savedCategory: Category? = null

        launchHost().use { scenario ->
            scenario.onActivity { activity ->
                CategoryDialog.show(
                    activity = activity,
                    category = null,
                    onSave = { savedCategory = it },
                    onDelete = {}
                )
            }

            onView(withId(R.id.etCategoryName)).perform(replaceText(specialName))

            onView(withId(R.id.etCategoryName))
                .check(matches(withText(specialName)))

            onView(withText("Criar")).check(matches(isEnabled()))
            onView(withText("Criar")).perform(click())
        }

        assertNotNull(savedCategory)
        assertEquals(specialName, savedCategory!!.name)
    }
}
