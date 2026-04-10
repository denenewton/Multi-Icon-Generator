package com.example.multiicongenerator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview as CommonPreview

/**
 * Ponto de entrada principal para a aplicação no Android.
 */
class MainActivity : ComponentActivity() {
    /**
     * Inicializa a plataforma Android e define o conteúdo da UI como o componente [App].
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicializa dependências específicas do Android (como o seletor de imagens)
        initAndroidPlatform(this)
        setContent {
            App()
        }
    }
}

/**
 * Visualização prévia da aplicação para o ambiente de design do Android Studio.
 */
@CommonPreview
@Composable
fun AppAndroidPreview() {
    App()
}
