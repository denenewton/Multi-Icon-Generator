package com.example.multiicongenerator

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.example.multiicongenerator.util.ImageProcessor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.imageio.ImageIO
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

/**
 * Implementação do seletor de imagens para Desktop usando Swing [JFileChooser].
 */
class DesktopImagePicker : ImagePicker {
    override suspend fun pickImage(): ImageBitmap? = withContext(Dispatchers.IO) {
        val fileChooser = JFileChooser().apply {
            dialogTitle = "Selecionar Imagem"
            fileFilter = FileNameExtensionFilter("Imagens", "jpg", "jpeg", "png", "bmp")
        }
        val result = fileChooser.showOpenDialog(null)
        if (result == JFileChooser.APPROVE_OPTION) {
            val file = fileChooser.selectedFile
            try {
                val bufferedImage = ImageIO.read(file)
                bufferedImage.toComposeImageBitmap()
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }
}

/**
 * Implementação do exportador de arquivos para Desktop.
 */
class DesktopFileExporter : FileExporter {
    override suspend fun exportIcons(
        bitmap: ImageBitmap,
        platforms: Set<TargetPlatform>,
        onProgress: (String) -> Unit
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val userHome = System.getProperty("user.home")
            val downloadsDir = File(userHome, "Downloads")
            
            val fileChooser = JFileChooser().apply {
                dialogTitle = "Selecionar Destino"
                fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                if (downloadsDir.exists()) {
                    currentDirectory = downloadsDir
                }
                selectedFile = File("MultiIconGenerator_Exports")
            }
            val result = fileChooser.showSaveDialog(null)
            if (result != JFileChooser.APPROVE_OPTION) {
                return@withContext Result.failure(Exception("Exportação cancelada"))
            }

            val baseDir = fileChooser.selectedFile
            val sdf = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault())
            val timestamp = sdf.format(Date())
            val sessionDir = File(baseDir, "Export_$timestamp")
            sessionDir.mkdirs()

            platforms.forEach { platform ->
                onProgress("Exportando ${platform.displayName}...")
                val configs = ImageProcessor.platformConfigs[platform.displayName] ?: emptyList()
                val generatedBitmaps = mutableMapOf<Int, ImageBitmap>()

                configs.forEach { config ->
                    var scaled = ImageProcessor.scaleBitmap(bitmap, config.size, config.size)
                    
                    if (config.isRound) {
                        scaled = ImageProcessor.applyCircularMask(scaled)
                    }
                    
                    generatedBitmaps[config.size] = scaled

                    val subPath = if (config.name.contains("/")) config.name.substringBeforeLast("/") else ""
                    val fileName = if (config.name.contains("/")) config.name.substringAfterLast("/") else config.name
                    val targetFolder = File(sessionDir, "${platform.displayName}/$subPath")
                    targetFolder.mkdirs()

                    val iconFile = File(targetFolder, fileName)

                    when (config.format) {
                        "png" -> {
                            ImageIO.write(scaled.toAwtImage(), "png", iconFile)
                        }
                        "ico" -> {
                            // Salva apenas quando chegar no tamanho final ou se for o único arquivo ico definido
                            if (config.size == 256) {
                                saveAsIco(generatedBitmaps, iconFile)
                            }
                        }
                        "icns" -> {
                            // Salva apenas no tamanho final para garantir que todos os tamanhos menores já foram processados
                            if (config.size == 1024) {
                                saveAsIcns(generatedBitmaps, iconFile)
                            }
                        }
                    }
                }
            }
            Result.success(sessionDir.absolutePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun saveAsIco(bitmaps: Map<Int, ImageBitmap>, outputFile: File) {
        val bos = ByteArrayOutputStream()
        bos.write(byteArrayOf(0, 0, 1, 0))
        
        val sizesToInclude = listOf(16, 32, 48, 64, 128, 256).filter { bitmaps.containsKey(it) }
        val count = sizesToInclude.size.toShort()
        val countBuffer = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(count)
        bos.write(countBuffer.array())

        val imageDataList = mutableListOf<ByteArray>()
        var offset = 6 + (sizesToInclude.size * 16)

        sizesToInclude.forEach { size ->
            val img = bitmaps[size]!!
            val imgByteStream = ByteArrayOutputStream()
            ImageIO.write(img.toAwtImage(), "png", imgByteStream)
            val bytes = imgByteStream.toByteArray()
            imageDataList.add(bytes)

            bos.write(if (size >= 256) 0 else size)
            bos.write(if (size >= 256) 0 else size)
            bos.write(0)
            bos.write(0)
            bos.write(byteArrayOf(1, 0))
            bos.write(byteArrayOf(32, 0))
            
            val sizeBuffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(bytes.size)
            bos.write(sizeBuffer.array())
            
            val offsetBuffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(offset)
            bos.write(offsetBuffer.array())
            
            offset += bytes.size
        }

        imageDataList.forEach { bos.write(it) }
        FileOutputStream(outputFile).use { it.write(bos.toByteArray()) }
    }

    private fun saveAsIcns(bitmaps: Map<Int, ImageBitmap>, outputFile: File) {
        val bos = ByteArrayOutputStream()
        bos.write("icns".toByteArray())
        
        val dataBos = ByteArrayOutputStream()
        val types = mapOf(
            16 to "icp4", 32 to "icp5", 64 to "icp6",
            128 to "ic07", 256 to "ic08", 512 to "ic09", 1024 to "ic10"
        )

        bitmaps.forEach { (size, img) ->
            val type = types[size] ?: return@forEach
            val imgByteStream = ByteArrayOutputStream()
            ImageIO.write(img.toAwtImage(), "png", imgByteStream)
            val bytes = imgByteStream.toByteArray()
            
            dataBos.write(type.toByteArray())
            val blockSize = bytes.size + 8
            val lengthBuffer = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(blockSize)
            dataBos.write(lengthBuffer.array())
            dataBos.write(bytes)
        }

        val totalSize = dataBos.size() + 8
        val totalSizeBuffer = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(totalSize)
        bos.write(totalSizeBuffer.array())
        bos.write(dataBos.toByteArray())

        FileOutputStream(outputFile).use { it.write(bos.toByteArray()) }
    }
}

actual fun getImagePicker(): ImagePicker = DesktopImagePicker()
actual fun getFileExporter(): FileExporter = DesktopFileExporter()
