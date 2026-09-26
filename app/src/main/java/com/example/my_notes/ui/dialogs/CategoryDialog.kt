package com.example.my_notes.ui.dialogs

import android.app.Activity
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.my_notes.R
import com.example.my_notes.data.model.Category
import com.example.my_notes.util.contrastTextColor
import com.example.my_notes.util.parseHexColor
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Diálogo para criar ou editar uma categoria (XML + MaterialAlertDialogBuilder).
 *
 * - Fundo claro padrão do Material; a cor escolhida aparece na
 *   pré-visualização e é salva na categoria.
 * - Permite escolher nome e cor (paleta predefinida).
 * - Valida o formulário antes de habilitar o botão Criar/Atualizar.
 * - Botão Excluir aparece apenas ao editar categoria existente.
 *
 * @param activity Activity usada como contexto do diálogo
 * @param category Categoria em edição, ou null para criação
 * @param onSave Chamado com a categoria pronta para salvar
 * @param onDelete Chamado para excluir a categoria (apenas em edição)
 * @return O [AlertDialog] exibido (para controle da Activity)
 */
object CategoryDialog {

    fun show(
        activity: Activity,
        category: Category?,
        onSave: (Category) -> Unit,
        onDelete: (Category) -> Unit
    ): AlertDialog {
        val isEditing = category != null
        var selectedColor: String = category?.color ?: presetColors.first()

        // ContextThemeWrapper aplica o tema claro ao diálogo E aos layouts
        // inflados (fundo branco, campos escuros), independentemente do
        // tema escuro da Activity.
        val dialogCtx = android.view.ContextThemeWrapper(
            activity, R.style.Theme_Mynotes_Dialog
        )
        val dialog = MaterialAlertDialogBuilder(dialogCtx)
            .setTitle(
                activity.getString(
                    if (isEditing) R.string.category_dialog_edit_title
                    else R.string.category_dialog_new_title
                )
            )
            .setView(R.layout.dialog_category)
            .setPositiveButton(
                if (isEditing) R.string.button_update else R.string.button_create,
                null
            )
            .setNegativeButton(R.string.button_cancel, null)
            .apply {
                if (isEditing) {
                    setNeutralButton(R.string.button_delete) { _, _ ->
                        onDelete(category)
                    }
                }
            }
            .create()

        dialog.setOnShowListener {
            val nameField =
                requireNotNull(
                    dialog.findViewById<com.google.android.material.textfield.TextInputEditText>(
                        R.id.etCategoryName
                    )
                )
            val palette =
                requireNotNull(dialog.findViewById<LinearLayout>(R.id.colorPalette))
            val previewDot =
                requireNotNull(dialog.findViewById<View>(R.id.previewDot))
            val previewName =
                requireNotNull(dialog.findViewById<TextView>(R.id.previewName))
            val saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)

            // Tipo de texto permissivo garantido em runtime: aceita
            // qualquer caractere (acentos, símbolos, emojis) exatamente
            // como digitado, sem filtros.
            nameField.inputType =
                android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES

            // Pré-preenche ao editar (no modo criação permanece vazio)
            nameField.setText(category?.name ?: "")

            // Botão Excluir em vermelho (hierarquia destrutiva)
            if (isEditing) {
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL)
                    .setTextColor(activity.getColor(R.color.destructive))
            }

            fun isFormValid(): Boolean = nameField.text.toString().isNotBlank()

            fun updateSaveButton() {
                saveButton.isEnabled = isFormValid()
            }

            // Pré-visualização do chip como ele aparecerá na tela
            fun refreshPreview() {
                previewDot.background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(parseHexColor(selectedColor))
                }
                previewName.text = nameField.text.toString().trim()
                    .ifBlank { activity.getString(R.string.hint_name) }
            }

            // Paleta de cores com anel/check no selecionado
            fun refreshPalette() {
                palette.removeAllViews()
                val inflater = LayoutInflater.from(activity)
                presetColors.forEach { hex ->
                    val swatch = inflater.inflate(
                        R.layout.item_color_swatch, palette, false
                    )
                    val colorInt = parseHexColor(hex)
                    val isSelected = hex == selectedColor

                    requireNotNull(
                        swatch.findViewById<View>(R.id.swatchColor)
                    ).background =
                        GradientDrawable().apply {
                            shape = GradientDrawable.OVAL
                            setColor(colorInt)
                            setStroke(
                                1,
                                activity.getColor(R.color.swatch_border)
                            )
                        }
                    requireNotNull(
                        swatch.findViewById<View>(R.id.swatchRing)
                    ).visibility =
                        if (isSelected) View.VISIBLE else View.GONE
                    requireNotNull(
                        swatch.findViewById<TextView>(R.id.swatchCheck)
                    ).apply {
                        visibility = if (isSelected) View.VISIBLE else View.GONE
                        setTextColor(colorInt.contrastTextColor())
                    }
                    swatch.contentDescription = "Selecionar cor $hex"
                    swatch.setOnClickListener {
                        selectedColor = hex
                        refreshPalette()
                        refreshPreview()
                    }
                    palette.addView(swatch)
                }
            }

            refreshPalette()
            refreshPreview()
            updateSaveButton()

            nameField.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
                override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    refreshPreview()
                    updateSaveButton()
                }
            })

            // Validação manual: mantém o diálogo aberto se inválido.
            // O fechamento em caso de sucesso vem do estado (via Activity);
            // nomes duplicados mantêm o diálogo aberto com erro no Snackbar.
            saveButton.setOnClickListener {
                if (isFormValid()) {
                    onSave(
                        Category(
                            id = category?.id ?: 0,
                            name = nameField.text.toString().trim(),
                            color = selectedColor
                        )
                    )
                }
            }
        }

        dialog.show()
        return dialog
    }
}
