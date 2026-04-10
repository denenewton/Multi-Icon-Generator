package com.example.multiicongenerator

import androidx.compose.ui.graphics.ImageBitmap

/**
 * Enumeração das plataformas suportadas para a geração de ícones.
 * 
 * @property displayName Nome legível da plataforma para exibição na UI.
 */
enum class TargetPlatform(val displayName: String) {
    ANDROID("Android"),
    IOS("iOS"),
    WINDOWS("Windows"),
    MACOS("macOS"),
    LINUX("Linux"),
    WEB("Web")
}

/**
 * Interface para seleção de imagem. 
 * Implementada de forma específica em cada plataforma (Android, Desktop).
 */
interface ImagePicker {
    /**
     * Abre o seletor de arquivos do sistema e retorna a imagem selecionada como [ImageBitmap].
     */
    suspend fun pickImage(): ImageBitmap?
}

/**
 * Interface para exportação de arquivos de ícones.
 * Implementada de forma específica em cada plataforma.
 */
interface FileExporter {
    /**
     * Gera e salva os ícones para as plataformas especificadas.
     * 
     * @param bitmap A imagem de origem.
     * @param platforms O conjunto de plataformas alvo.
     * @param onProgress Callback para informar o progresso ou status atual.
     * @return Result contendo o caminho da pasta de exportação em caso de sucesso.
     */
    suspend fun exportIcons(bitmap: ImageBitmap, platforms: Set<TargetPlatform>, onProgress: (String) -> Unit): Result<String>
}

/**
 * Retorna a instância de [ImagePicker] específica da plataforma.
 */
expect fun getImagePicker(): ImagePicker

/**
 * Retorna a instância de [FileExporter] específica da plataforma.
 */
expect fun getFileExporter(): FileExporter
