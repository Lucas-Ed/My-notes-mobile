package com.example.my_notes.ui.dialogs

import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.my_notes.R
import com.example.my_notes.data.model.Category
import com.example.my_notes.data.model.Note
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Diálogo para criar ou editar uma nota (XML + MaterialAlertDialogBuilder).
 *
 * - Fundo claro padrão do Material; a cor da categoria aparece nos chips.
 * - Campos: categoria (chips), título e conteúdo.
 * - Botão Excluir aparece apenas ao editar nota existente.
 * - Botão Salvar habilitado somente com formulário válido
 *   (título, conteúdo e categoria ≠ 0).
 * - Os campos aceitam caracteres especiais exatamente como digitados
 *   (sem autocorreção).
 *
 * @param activity Activity usada como contexto do diálogo
 * @param note Nota em edição, ou null para criação
 * @param categories Lista de categorias disponíveis para seleção
 * @param onSave Chamado com a nota pronta para salvar
 * @param onDelete Chamado para excluir a nota (apenas em edição)
 * @return O [AlertDialog] exibido (para controle da Activity)
 */
object NoteDialog {

    fun show(
        activity: Activity,
        note: Note?,
        categories: List<Category>,
        onSave: (Note) -> Unit,
        onDelete: (Note) -> Unit
    ): AlertDialog {
        val isEditing = note != null

        // Se a categoria da nota foi excluída, inicia em 0 para o usuário
        // escolher outra antes de salvar (validação exige categoria != 0).
        var categoryId: Int = note?.let { n ->
            if (categories.any { it.id == n.categoryId }) n.categoryId else 0
        } ?: (categories.firstOrNull()?.id ?: 0)

        // ContextThemeWrapper aplica o tema claro ao diálogo E aos layouts
        // inflados (fundo branco, campos escuros), independentemente do
        // tema escuro da Activity.
        val dialogCtx = android.view.ContextThemeWrapper(
            activity, R.style.Theme_Mynotes_Dialog
        )
        val dialog = MaterialAlertDialogBuilder(dialogCtx)
            .setTitle(
                activity.getString(
                    if (isEditing) R.string.note_dialog_edit_title
                    else R.string.note_dialog_new_title
                )
            )
            .setView(R.layout.dialog_note)
            .setPositiveButton(R.string.button_save, null)
            .setNegativeButton(R.string.button_cancel, null)
            .apply {
                if (isEditing) {
                    setNeutralButton(R.string.button_delete) { _, _ ->
                        onDelete(note)
                    }
                }
            }
            .create()

        dialog.setOnShowListener {
            val titleField =
                requireNotNull(
                    dialog.findViewById<com.google.android.material.textfield.TextInputEditText>(
                        R.id.etNoteTitle
                    )
                )
            val contentField =
                requireNotNull(
                    dialog.findViewById<com.google.android.material.textfield.TextInputEditText>(
                        R.id.etNoteContent
                    )
                )
            val chipsContainer =
                requireNotNull(dialog.findViewById<LinearLayout>(R.id.dialogNoteChips))
            val noCategories =
                requireNotNull(dialog.findViewById<TextView>(R.id.noCategoriesText))
            val saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)

            // Tipo de texto permissivo garantido em runtime: aceita
            // qualquer caractere (acentos, símbolos, emojis) exatamente
            // como digitado, sem filtros.
            titleField.inputType =
                android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            contentField.inputType =
                android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or
                    android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE

            // Pré-preenche ao editar (no modo criação permanece vazio)
            titleField.setText(note?.title ?: "")
            contentField.setText(note?.content ?: "")

            // Botão Excluir em vermelho (hierarquia destrutiva)
            if (isEditing) {
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL)
                    .setTextColor(activity.getColor(R.color.destructive))
            }

            fun isFormValid(): Boolean {
                return titleField.text.toString().isNotBlank() &&
                    contentField.text.toString().isNotBlank() &&
                    categoryId != 0
            }

            fun updateSaveButton() {
                saveButton.isEnabled = isFormValid()
            }

            // Preenche os chips de categoria com seleção reativa
            fun refreshChips() {
                chipsContainer.removeAllViews()
                val inflater = LayoutInflater.from(activity)
                categories.forEach { category ->
                    val chip = inflater.inflate(
                        R.layout.item_category, chipsContainer, false
                    )
                    requireNotNull(
                        chip.findViewById<TextView>(R.id.txtCategoryName)
                    ).apply {
                        text = category.name
                        setTextColor(
                            activity.getColor(R.color.text_dark)
                        )
                    }
                    requireNotNull(
                        chip.findViewById<View>(R.id.imgDot)
                    ).background = chipDot(activity, category.color)
                    chip.background = androidx.core.content.ContextCompat.getDrawable(
                        activity,
                        if (category.id == categoryId) R.drawable.chip_bg_selected
                        else R.drawable.chip_bg_dialog
                    )
                    chip.setOnClickListener {
                        categoryId = category.id
                        refreshChips()
                        updateSaveButton()
                    }
                    chipsContainer.addView(chip)
                }
            }

            if (categories.isEmpty()) {
                noCategories.visibility = View.VISIBLE
            } else {
                refreshChips()
            }

            // Estado inicial do botão Salvar
            updateSaveButton()

            // Revalida a cada digitação
            val watcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
                override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
                override fun afterTextChanged(s: Editable?) = updateSaveButton()
            }
            titleField.addTextChangedListener(watcher)
            contentField.addTextChangedListener(watcher)

            // Validação manual: mantém o diálogo aberto se inválido.
            // O fechamento em caso de sucesso vem do estado (via Activity).
            saveButton.setOnClickListener {
                if (isFormValid()) {
                    onSave(
                        Note(
                            id = note?.id ?: 0,
                            title = titleField.text.toString().trim(),
                            content = contentField.text.toString().trim(),
                            categoryId = categoryId
                        )
                    )
                }
            }
        }

        dialog.show()
        return dialog
    }
}
