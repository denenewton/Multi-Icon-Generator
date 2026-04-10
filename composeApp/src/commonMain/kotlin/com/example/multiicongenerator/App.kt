package com.example.multiicongenerator

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.multiicongenerator.ui.MainScreen
import com.example.multiicongenerator.ui.theme.MultiIconGeneratorTheme
import com.example.multiicongenerator.viewmodel.MainViewModel

/**
 * Componente raiz da aplicação compartilhado entre as plataformas.
 * Configura o tema e inicializa o ViewModel com as dependências específicas de cada plataforma.
 */
@Composable
fun App() {
    MultiIconGeneratorTheme {
        // Inicializa o ViewModel injetando as implementações de ImagePicker e FileExporter
        val viewModel: MainViewModel = viewModel {
            MainViewModel(getImagePicker(), getFileExporter())
        }
        
        // Exibe a tela principal passando o ViewModel inicializado
        MainScreen(viewModel)
    }
}
