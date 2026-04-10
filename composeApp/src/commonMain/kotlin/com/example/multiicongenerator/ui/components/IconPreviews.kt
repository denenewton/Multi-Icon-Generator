package com.example.multiicongenerator.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.multiicongenerator.TargetPlatform

/**
 * Exibe uma lista de visualizações em tempo real dos ícones para as plataformas selecionadas.
 *
 * @param bitmap O bitmap da imagem original que será usado para gerar as visualizações.
 * @param selectedPlatforms O conjunto de plataformas para as quais exibir as visualizações.
 */
@Composable
fun IconPreviews(bitmap: ImageBitmap?, selectedPlatforms: Set<TargetPlatform>) {
    if (bitmap == null) return

    Column {
        Text("3. Visualizações em Tempo Real", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(selectedPlatforms.toList()) { platform ->
                PlatformPreviewCard(bitmap, platform)
            }
        }
    }
}

/**
 * Um cartão que contém a visualização específica de uma plataforma.
 *
 * @param bitmap O bitmap da imagem original.
 * @param platform A plataforma alvo para a qual a visualização será gerada.
 */
@Composable
fun PlatformPreviewCard(bitmap: ImageBitmap, platform: TargetPlatform) {
    Card(
        modifier = Modifier.width(280.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(platform.displayName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.DarkGray),
                contentAlignment = Alignment.Center
            ) {
                when (platform) {
                    TargetPlatform.ANDROID -> AndroidPreview(bitmap)
                    TargetPlatform.IOS -> IosPreview(bitmap)
                    TargetPlatform.WEB -> WebPreview(bitmap)
                    TargetPlatform.WINDOWS -> WindowsPreview(bitmap)
                    TargetPlatform.MACOS -> MacOsPreview(bitmap)
                    TargetPlatform.LINUX -> LinuxPreview(bitmap)
                }
            }
        }
    }
}

/**
 * Simula a aparência de um ícone na barra de tarefas do Linux (GNOME/KDE).
 */
@Composable
fun LinuxPreview(bitmap: ImageBitmap) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(44.dp).background(Color.Black.copy(alpha = 0.9f)),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                bitmap = bitmap,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

/**
 * Simula a aparência de um ícone na tela inicial do Android (formato circular).
 */
@Composable
fun AndroidPreview(bitmap: ImageBitmap) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            bitmap = bitmap,
            contentDescription = null,
            modifier = Modifier.size(64.dp).clip(CircleShape),
            contentScale = ContentScale.FillBounds
        )
        Spacer(Modifier.height(8.dp))
        Box(Modifier.width(40.dp).height(4.dp).background(Color.White.copy(alpha = 0.5f), CircleShape))
    }
}

/**
 * Simula a aparência de um ícone na tela inicial do iOS (cantos arredondados específicos).
 */
@Composable
fun IosPreview(bitmap: ImageBitmap) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            bitmap = bitmap,
            contentDescription = null,
            modifier = Modifier.size(60.dp).clip(RoundedCornerShape(13.dp)),
            contentScale = ContentScale.FillBounds
        )
        Spacer(Modifier.height(8.dp))
        Text("App Name", color = Color.White, fontSize = 10.sp)
    }
}

/**
 * Simula a aparência de um ícone (favicon) em uma aba de navegador Web.
 */
@Composable
fun WebPreview(bitmap: ImageBitmap) {
    Row(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(32.dp)
            .background(Color.LightGray, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            bitmap = bitmap,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(8.dp))
        Box(Modifier.width(60.dp).height(4.dp).background(Color.Gray.copy(alpha = 0.5f), CircleShape))
    }
}

/**
 * Simula a aparência de um ícone na barra de tarefas do Windows.
 */
@Composable
fun WindowsPreview(bitmap: ImageBitmap) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(40.dp).background(Color.Black.copy(alpha = 0.8f)),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                bitmap = bitmap,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * Simula a aparência de um ícone no Dock do macOS.
 */
@Composable
fun MacOsPreview(bitmap: ImageBitmap) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .height(50.dp)
                .width(80.dp)
                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                bitmap = bitmap,
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}
