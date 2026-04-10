package com.example.multiicongenerator.util

import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

/**
 * Utilitário para processamento de imagens, incluindo redimensionamento,
 * corte e configurações de ícones para diferentes plataformas.
 */
object ImageProcessor {

    /**
     * Redimensiona um [ImageBitmap] para a largura e altura especificadas.
     * 
     * @param source A imagem de origem.
     * @param targetWidth A largura desejada.
     * @param targetHeight A altura desejada.
     * @return Um novo [ImageBitmap] redimensionado com alta qualidade de filtragem.
     */
    fun scaleBitmap(source: ImageBitmap, targetWidth: Int, targetHeight: Int): ImageBitmap {
        val output = ImageBitmap(targetWidth, targetHeight)
        val canvas = Canvas(output)
        val paint = Paint().apply {
            isAntiAlias = true
            filterQuality = androidx.compose.ui.graphics.FilterQuality.High
        }
        
        canvas.drawImageRect(
            image = source,
            srcOffset = IntOffset.Zero,
            srcSize = IntSize(source.width, source.height),
            dstOffset = IntOffset.Zero,
            dstSize = IntSize(targetWidth, targetHeight),
            paint = paint
        )
        return output
    }

    /**
     * Corta um [ImageBitmap] para torná-lo quadrado, centralizando o conteúdo.
     * 
     * @param bitmap A imagem de origem.
     * @return Um novo [ImageBitmap] quadrado.
     */
    fun cropToSquare(bitmap: ImageBitmap): ImageBitmap {
        val width = bitmap.width
        val height = bitmap.height
        val newSize = if (height > width) width else height
        val cropX = (width - newSize) / 2
        val cropY = (height - newSize) / 2
        
        val output = ImageBitmap(newSize, newSize)
        val canvas = Canvas(output)
        canvas.drawImageRect(
            image = bitmap,
            srcOffset = IntOffset(cropX, cropY),
            srcSize = IntSize(newSize, newSize),
            dstOffset = IntOffset.Zero,
            dstSize = IntSize(newSize, newSize),
            paint = Paint()
        )
        return output
    }

    /**
     * Aplica uma máscara circular a um [ImageBitmap].
     * 
     * @param bitmap A imagem de origem (deve ser quadrada).
     * @return Um novo [ImageBitmap] recortado em formato de círculo.
     */
    fun applyCircularMask(bitmap: ImageBitmap): ImageBitmap {
        val size = bitmap.width
        val output = ImageBitmap(size, size)
        val canvas = Canvas(output)
        val paint = Paint().apply {
            isAntiAlias = true
        }

        val path = androidx.compose.ui.graphics.Path().apply {
            addOval(androidx.compose.ui.geometry.Rect(0f, 0f, size.toFloat(), size.toFloat()))
        }

        canvas.save()
        canvas.clipPath(path)
        canvas.drawImageRect(
            image = bitmap,
            srcOffset = IntOffset.Zero,
            srcSize = IntSize(size, size),
            dstOffset = IntOffset.Zero,
            dstSize = IntSize(size, size),
            paint = paint
        )
        canvas.restore()
        return output
    }

    /**
     * Configuração de um ícone individual.
     * 
     * @property name Nome do arquivo ou caminho relativo do ícone.
     * @property size Tamanho (largura/altura) do ícone em pixels.
     * @property format Formato de saída desejado (png, ico, icns).
     * @property isRound Define se o ícone deve ser processado com uma máscara circular (exclusivo para Android).
     */
    data class IconConfig(
        val name: String, 
        val size: Int, 
        val format: String = "png",
        val isRound: Boolean = false
    )

    /**
     * Mapeamento de plataformas para suas respectivas listas de configurações de ícones (tamanhos e nomes).
     */
    val platformConfigs = mapOf(
        "Android" to listOf(
            // Ícones Padrão (Legado)
            IconConfig("mipmap-mdpi/ic_launcher.png", 48),
            IconConfig("mipmap-hdpi/ic_launcher.png", 72),
            IconConfig("mipmap-xhdpi/ic_launcher.png", 96),
            IconConfig("mipmap-xxhdpi/ic_launcher.png", 144),
            IconConfig("mipmap-xxxhdpi/ic_launcher.png", 192),
            // Ícones Arredondados (Legado)
            IconConfig("mipmap-mdpi/ic_launcher_round.png", 48, isRound = true),
            IconConfig("mipmap-hdpi/ic_launcher_round.png", 72, isRound = true),
            IconConfig("mipmap-xhdpi/ic_launcher_round.png", 96, isRound = true),
            IconConfig("mipmap-xxhdpi/ic_launcher_round.png", 144, isRound = true),
            IconConfig("mipmap-xxxhdpi/ic_launcher_round.png", 192, isRound = true),
            // Ícones Adaptativos (Moderno - Foreground)
            // O tamanho padrão para adaptive icons é 108dp. 
            // mdpi (108px), hdpi (162px), xhdpi (216px), xxhdpi (324px), xxxhdpi (432px)
            IconConfig("mipmap-mdpi/ic_launcher_foreground.png", 108),
            IconConfig("mipmap-hdpi/ic_launcher_foreground.png", 162),
            IconConfig("mipmap-xhdpi/ic_launcher_foreground.png", 216),
            IconConfig("mipmap-xxhdpi/ic_launcher_foreground.png", 324),
            IconConfig("mipmap-xxxhdpi/ic_launcher_foreground.png", 432),
            IconConfig("playstore-icon.png", 512)
        ),
        "iOS" to listOf(
            IconConfig("AppIcon-20x20@2x.png", 40),
            IconConfig("AppIcon-20x20@3x.png", 60),
            IconConfig("AppIcon-29x29@2x.png", 58),
            IconConfig("AppIcon-29x29@3x.png", 87),
            IconConfig("AppIcon-40x40@2x.png", 80),
            IconConfig("AppIcon-40x40@3x.png", 120),
            IconConfig("AppIcon-60x60@2x.png", 120),
            IconConfig("AppIcon-60x60@3x.png", 180),
            IconConfig("AppIcon-76x76@2x.png", 152),
            IconConfig("AppIcon-83.5x83.5@2x.png", 167),
            IconConfig("AppIcon-1024.png", 1024)
        ),
        "Web" to listOf(
            IconConfig("favicon-16x16.png", 16),
            IconConfig("favicon-32x32.png", 32),
            IconConfig("apple-touch-icon.png", 180),
            IconConfig("android-chrome-192x192.png", 192),
            IconConfig("android-chrome-512x512.png", 512)
        ),
        "Windows" to listOf(
            IconConfig("icon-16.png", 16, isRound = true),
            IconConfig("icon-24.png", 24, isRound = true),
            IconConfig("icon-32.png", 32, isRound = true),
            IconConfig("icon-48.png", 48, isRound = true),
            IconConfig("icon-64.png", 64, isRound = true),
            IconConfig("icon-128.png", 128, isRound = true),
            IconConfig("icon-256.png", 256, isRound = true),
            IconConfig("app_icon.ico", 256, "ico", isRound = true)
        ),
        "macOS" to listOf(
            IconConfig("icon_16x16.png", 16, isRound = true),
            IconConfig("icon_16x16@2x.png", 32, isRound = true),
            IconConfig("icon_32x32.png", 32, isRound = true),
            IconConfig("icon_32x32@2x.png", 64, isRound = true),
            IconConfig("icon_128x128.png", 128, isRound = true),
            IconConfig("icon_128x128@2x.png", 256, isRound = true),
            IconConfig("icon_256x256.png", 256, isRound = true),
            IconConfig("icon_256x256@2x.png", 512, isRound = true),
            IconConfig("icon_512x512.png", 512, isRound = true),
            IconConfig("icon_512x512@2x.png", 1024, isRound = true),
            IconConfig("app_icon.icns", 1024, "icns", isRound = true)
        ),
        "Linux" to listOf(
            IconConfig("icon-16.png", 16, isRound = true),
            IconConfig("icon-32.png", 32, isRound = true),
            IconConfig("icon-48.png", 48, isRound = true),
            IconConfig("icon-64.png", 64, isRound = true),
            IconConfig("icon-96.png", 96, isRound = true),
            IconConfig("icon-128.png", 128, isRound = true),
            IconConfig("icon-256.png", 256, isRound = true),
            IconConfig("icon-512.png", 512, isRound = true)
        )
    )
}
