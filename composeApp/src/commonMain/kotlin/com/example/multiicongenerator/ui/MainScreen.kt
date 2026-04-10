package com.example.multiicongenerator.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.multiicongenerator.TargetPlatform
import com.example.multiicongenerator.viewmodel.MainViewModel
import com.example.multiicongenerator.ui.components.IconPreviews

/**
 * Tela principal da aplicação com layout modernizado. 
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var showAboutDialog by remember { mutableStateOf(false) }

    if (showAboutDialog) {
        AboutDialog(onDismiss = { showAboutDialog = false })
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(32.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Icon(
                                Icons.Default.FileDownload, 
                                contentDescription = null, 
                                modifier = Modifier.padding(4.dp),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Text("Multi Icon Generator", fontWeight = FontWeight.ExtraBold)
                    }
                },
                actions = {
                    IconButton(onClick = { showAboutDialog = true }) {
                        Icon(Icons.Default.Info, contentDescription = "Sobre o App")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = uiState.sourceBitmap != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.onExport() },
                    icon = { Icon(Icons.Default.FileDownload, contentDescription = null) },
                    text = { Text("Exportar em Lote") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    expanded = !uiState.isLoading
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 80.dp, start = 24.dp, end = 24.dp, top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            item {
                SectionHeader("1. Imagem de Origem", "Selecione uma imagem quadrada de alta resolução")
                Spacer(Modifier.height(16.dp))
                ImagePickerCard(
                    image = uiState.sourceBitmap,
                    onPick = { viewModel.onPickImage() }
                )
            }

            if (uiState.sourceBitmap != null) {
                item {
                    SectionHeader("2. Plataformas Alvo", "Escolha para quais sistemas gerar os ícones")
                    Spacer(Modifier.height(16.dp))
                    PlatformSelectorCard(
                        selectedPlatforms = uiState.selectedPlatforms,
                        onToggle = { viewModel.togglePlatform(it) }
                    )
                }

                item {
                    IconPreviews(
                        bitmap = uiState.sourceBitmap,
                        selectedPlatforms = uiState.selectedPlatforms
                    )
                }
            }

            item {
                StatusCard(
                    isLoading = uiState.isLoading,
                    error = uiState.errorMessage,
                    status = uiState.exportStatus
                )
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String) {
    Column {
        Text(text = title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun ImagePickerCard(image: androidx.compose.ui.graphics.ImageBitmap?, onPick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(240.dp).clickable { onPick() },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (image != null) {
                Image(
                    bitmap = image,
                    contentDescription = "Imagem selecionada",
                    modifier = Modifier.fillMaxSize().padding(16.dp).clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Fit
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(modifier = Modifier.size(64.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.padding(16.dp), tint = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(Modifier.height(16.dp))
                    Text("Toque para carregar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        "Recomendado: 1024x1024 (PNG/JPG)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlatformSelectorCard(selectedPlatforms: Set<TargetPlatform>, onToggle: (TargetPlatform) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp))
    ) {
        FlowRow(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TargetPlatform.entries.forEach { platform ->
                val isSelected = selectedPlatforms.contains(platform)
                FilterChip(
                    selected = isSelected,
                    onClick = { onToggle(platform) },
                    label = { Text(platform.displayName, modifier = Modifier.padding(vertical = 8.dp)) },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = MaterialTheme.colorScheme.outlineVariant,
                        enabled = true,
                        selected = isSelected
                    )
                )
            }
        }
    }
}

@Composable
fun StatusCard(isLoading: Boolean, error: String?, status: String?) {
    AnimatedVisibility(
        visible = isLoading || error != null || status != null,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = when {
                    error != null -> MaterialTheme.colorScheme.errorContainer
                    isLoading -> MaterialTheme.colorScheme.secondaryContainer
                    else -> MaterialTheme.colorScheme.tertiaryContainer
                }
            )
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 3.dp)
                } else if (error != null) {
                    Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                } else {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                }
                Spacer(Modifier.width(16.dp))
                Text(
                    text = error ?: status ?: "Processando...",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = when {
                        error != null -> MaterialTheme.colorScheme.onErrorContainer
                        isLoading -> MaterialTheme.colorScheme.onSecondaryContainer
                        else -> MaterialTheme.colorScheme.onTertiaryContainer
                    }
                )
            }
        }
    }
}

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onDismiss) { Text("Fechar") } },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(12.dp))
                Text("Sobre o App")
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "O Multi Icon Generator é uma solução completa para automação de design de ícones. " +
                    "Ele permite que você transforme uma única imagem em um pacote completo de recursos para diversas plataformas, " +
                    "garantindo que cada ícone atenda aos padrões exatos de tamanho e formato exigidos pelos sistemas modernos.",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Text(
                    "Destaques:\n" +
                    "• Geração de containers .ico e .icns nativos.\n" +
                    "• Estrutura de pastas organizada para Android (Mipmaps).\n" +
                    "• Algoritmos de redimensionamento de alta fidelidade.\n" +
                    "• Interface reativa com Compose Multiplatform.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant)
                
                AboutItem("Desenvolvedor", "Daniel dos Santos Araújo")
                AboutItem("Ano", "2026")
                AboutItem("Empresa", "DENENEWTON")
                
                Spacer(Modifier.height(8.dp))
                
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Tecnologias: Compose Multiplatform, Kotlin Multiplatform, Material Design 3.",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        },
        shape = RoundedCornerShape(28.dp)
    )
}

@Composable
fun AboutItem(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
        Text(value, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
    }
}
