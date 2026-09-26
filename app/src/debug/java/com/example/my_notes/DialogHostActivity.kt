package com.example.my_notes

import androidx.appcompat.app.AppCompatActivity

/**
 * Activity hospedeira usada apenas nos testes de instrumentação
 * (declarada no AndroidManifest de src/debug). Exibe os diálogos
 * do app sem exigir Hilt/Room, mantendo o tema Theme.Mynotes.
 * Estar no source set debug garante que tema e layouts resolvam
 * a partir dos recursos do app em tempo de execução.
 */
class DialogHostActivity : AppCompatActivity()
