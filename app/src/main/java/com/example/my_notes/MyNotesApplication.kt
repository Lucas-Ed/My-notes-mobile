package com.example.my_notes

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Classe Application do My Notes.
 * Anotada com @HiltAndroidApp para inicializar o grafo de
 * dependências do Hilt no início do aplicativo.
 */
@HiltAndroidApp
class MyNotesApplication : Application()
