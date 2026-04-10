package com.example.multiicongenerator.viewmodel

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.multiicongenerator.FileExporter
import com.example.multiicongenerator.ImagePicker
import com.example.multiicongenerator.TargetPlatform
import com.example.multiicongenerator.util.ImageProcessor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Representa o estado da interface do usuário para a tela principal.
 *
 * @property sourceBitmap O bitmap da imagem original carregada e processada.
 * @property selectedPlatforms O conjunto de plataformas para as quais os ícones serão gerados.
 * @property isLoading Indica se uma operação demorada (carregamento ou exportação) está em execução.
 * @property errorMessage Mensagem de erro a ser exibida, se houver.
 * @property exportStatus Mensagem de status atual do processo de exportação.
 */
data class UiState(
    val sourceBitmap: ImageBitmap? = null,
    val selectedPlatforms: Set<TargetPlatform> = setOf(TargetPlatform.ANDROID),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val exportStatus: String? = null
)

/**
 * ViewModel principal que gerencia a lógica de negócio da aplicação,
 * incluindo a seleção de imagens, seleção de plataformas e o processo de exportação.
 *
 * @param imagePicker Interface para selecionar imagens do sistema de arquivos.
 * @param fileExporter Interface para exportar os ícones gerados.
 */
class MainViewModel(
    private val imagePicker: ImagePicker,
    private val fileExporter: FileExporter
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    /**
     * Fluxo de estado da UI observável pela View.
     */
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    /**
     * Inicia o processo de seleção de imagem.
     * Após a seleção, a imagem é cortada para um formato quadrado em uma thread separada.
     */
    fun onPickImage() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, exportStatus = null)
        viewModelScope.launch {
            val bitmap = imagePicker.pickImage()
            if (bitmap != null) {
                // Move o processamento pesado para Dispatchers.Default para evitar travamentos na UI
                val squared = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) {
                    ImageProcessor.cropToSquare(bitmap)
                }
                _uiState.value = _uiState.value.copy(sourceBitmap = squared, isLoading = false)
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    /**
     * Alterna a seleção de uma plataforma alvo.
     *
     * @param platform A plataforma a ser adicionada ou removida da seleção.
     */
    fun togglePlatform(platform: TargetPlatform) {
        val current = _uiState.value.selectedPlatforms
        val next = if (current.contains(platform)) current - platform else current + platform
        _uiState.value = _uiState.value.copy(selectedPlatforms = next, exportStatus = null)
    }

    /**
     * Inicia o processo de exportação de ícones para as plataformas selecionadas.
     * Valida se uma imagem foi carregada e se ao menos uma plataforma foi selecionada.
     */
    fun onExport() {
        val bitmap = _uiState.value.sourceBitmap ?: return
        val platforms = _uiState.value.selectedPlatforms
        if (platforms.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Selecione ao menos uma plataforma")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, exportStatus = "Iniciando exportação...")
        viewModelScope.launch {
            val result = fileExporter.exportIcons(bitmap, platforms) { progress ->
                _uiState.value = _uiState.value.copy(exportStatus = progress)
            }
            result.onSuccess { path ->
                _uiState.value = _uiState.value.copy(isLoading = false, exportStatus = "Exportado para: $path")
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Exportação falhou: ${e.message}")
            }
        }
    }
}
