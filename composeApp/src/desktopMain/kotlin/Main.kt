import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.window.MenuBar
import com.example.multiicongenerator.App
import java.awt.Dimension

/**
 * Ponto de entrada principal para a aplicação Desktop.
 * Configura o estado inicial da janela, menu nativo e inicia o Compose UI.
 */
fun main() = application {
    // 1. Define o estado da janela: centralizada e com tamanho inicial sugerido
    val windowState = rememberWindowState(
        position = WindowPosition(Alignment.Center),
        size = DpSize(1100.dp, 800.dp)
    )

    Window(
        onCloseRequest = ::exitApplication,
        title = "Multi Icon Generator",
        state = windowState,
        // icon = painterResource(Res.drawable.app_icon) // Carregue o ícone do app aqui
    ) {
        // 2. Define um tamanho mínimo para evitar que a UI quebre em janelas muito pequenas
        window.minimumSize = Dimension(800, 600)

        // 3. Barra de Menu Nativa (opcional)
        MenuBar {
            Menu("Arquivo") {
                Item("Sair", onClick = ::exitApplication)
            }
        }

        // Renderiza o componente principal da aplicação
        App()
    }
}
