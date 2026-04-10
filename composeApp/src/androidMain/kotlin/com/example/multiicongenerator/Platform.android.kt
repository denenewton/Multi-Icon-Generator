package com.example.multiicongenerator

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.multiicongenerator.util.ImageProcessor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume

/**
 * Implementação do seletor de imagens para Android.
 */
class AndroidImagePicker(private val activity: ComponentActivity) : ImagePicker {
    override suspend fun pickImage(): ImageBitmap? = suspendCancellableCoroutine { continuation ->
        val launcher = activity.activityResultRegistry.register(
            "pick_image",
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            if (uri != null) {
                try {
                    val inputStream = activity.contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    continuation.resume(bitmap?.asImageBitmap())
                } catch (e: Exception) {
                    continuation.resume(null)
                }
            } else {
                continuation.resume(null)
            }
        }
        launcher.launch("image/*")
    }
}

/**
 * Implementação do exportador de arquivos para Android com suporte a .ico e .icns.
 */
class AndroidFileExporter(private val context: Context) : FileExporter {
    override suspend fun exportIcons(
        bitmap: ImageBitmap,
        platforms: Set<TargetPlatform>,
        onProgress: (String) -> Unit
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val sdf = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault())
            val timestamp = sdf.format(Date())
            val rootFolderName = "MultiIconGenerator_Exports"
            val sessionFolderName = "Export_$timestamp"
            
            var displayPath = ""
            val androidBitmap = bitmap.asAndroidBitmap()

            platforms.forEach { platform ->
                onProgress("Exportando ${platform.displayName}...")
                val configs = ImageProcessor.platformConfigs[platform.displayName] ?: emptyList()
                val generatedBitmaps = mutableMapOf<Int, Bitmap>()

                configs.forEach { config ->
                    var scaled = Bitmap.createScaledBitmap(androidBitmap, config.size, config.size, true)
                    
                    if (config.isRound) {
                        val composeBitmap = scaled.asImageBitmap()
                        val circularCompose = ImageProcessor.applyCircularMask(composeBitmap)
                        scaled = circularCompose.asAndroidBitmap()
                    }

                    generatedBitmaps[config.size] = scaled

                    val subPath = if (config.name.contains("/")) config.name.substringBeforeLast("/") else ""
                    val fileName = if (config.name.contains("/")) config.name.substringAfterLast("/") else config.name

                    when (config.format) {
                        "png" -> {
                            saveFile(platform, sessionFolderName, rootFolderName, subPath, fileName, "image/png") { out ->
                                scaled.compress(Bitmap.CompressFormat.PNG, 100, out)
                            }
                        }
                        "ico" -> {
                            if (config.size == 256) {
                                saveFile(platform, sessionFolderName, rootFolderName, subPath, fileName, "image/x-icon") { out ->
                                    saveAsIco(generatedBitmaps, out)
                                }
                            }
                        }
                        "icns" -> {
                            if (config.size == 1024) {
                                saveFile(platform, sessionFolderName, rootFolderName, subPath, fileName, "image/x-icns") { out ->
                                    saveAsIcns(generatedBitmaps, out)
                                }
                            }
                        }
                    }
                }
                if (displayPath.isEmpty()) displayPath = "Downloads/$rootFolderName/$sessionFolderName"
            }
            Result.success(displayPath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun saveFile(
        platform: TargetPlatform,
        sessionDir: String,
        rootDir: String,
        subPath: String,
        fileName: String,
        mimeType: String,
        writeBlock: (OutputStream) -> Unit
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val relativePath = "${Environment.DIRECTORY_DOWNLOADS}/$rootDir/$sessionDir/${platform.displayName}/$subPath/".replace("//", "/")
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
            }
            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let { context.contentResolver.openOutputStream(it)?.use(writeBlock) }
        } else {
            val baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val folder = File(baseDir, "$rootDir/$sessionDir/${platform.displayName}/$subPath")
            folder.mkdirs()
            FileOutputStream(File(folder, fileName)).use(writeBlock)
        }
    }

    private fun saveAsIco(bitmaps: Map<Int, Bitmap>, output: OutputStream) {
        val bos = ByteArrayOutputStream()
        bos.write(byteArrayOf(0, 0, 1, 0))
        val sizes = listOf(16, 32, 48, 64, 128, 256).filter { bitmaps.containsKey(it) }
        bos.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(sizes.size.toShort()).array())

        val data = mutableListOf<ByteArray>()
        var offset = 6 + (sizes.size * 16)
        sizes.forEach { size ->
            val imgBos = ByteArrayOutputStream()
            bitmaps[size]!!.compress(Bitmap.CompressFormat.PNG, 100, imgBos)
            val bytes = imgBos.toByteArray()
            data.add(bytes)
            bos.write(if (size >= 256) 0 else size)
            bos.write(if (size >= 256) 0 else size)
            bos.write(byteArrayOf(0, 0, 1, 0, 32, 0))
            bos.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(bytes.size).array())
            bos.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(offset).array())
            offset += bytes.size
        }
        data.forEach { bos.write(it) }
        output.write(bos.toByteArray())
    }

    private fun saveAsIcns(bitmaps: Map<Int, Bitmap>, output: OutputStream) {
        val bos = ByteArrayOutputStream()
        bos.write("icns".toByteArray())
        val dataBos = ByteArrayOutputStream()
        val types = mapOf(16 to "icp4", 32 to "icp5", 64 to "icp6", 128 to "ic07", 256 to "ic08", 512 to "ic09", 1024 to "ic10")
        bitmaps.forEach { (size, img) ->
            val type = types[size] ?: return@forEach
            val imgBos = ByteArrayOutputStream()
            img.compress(Bitmap.CompressFormat.PNG, 100, imgBos)
            val bytes = imgBos.toByteArray()
            dataBos.write(type.toByteArray())
            dataBos.write(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(bytes.size + 8).array())
            dataBos.write(bytes)
        }
        bos.write(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(dataBos.size() + 8).array())
        bos.write(dataBos.toByteArray())
        output.write(bos.toByteArray())
    }
}

private var currentActivity: ComponentActivity? = null
fun initAndroidPlatform(activity: ComponentActivity) { currentActivity = activity }
actual fun getImagePicker(): ImagePicker = AndroidImagePicker(currentActivity!!)
actual fun getFileExporter(): FileExporter = AndroidFileExporter(currentActivity!!)
