# Multi Icon Generator - Visão Geral do Projeto

O **Multi Icon Generator** é uma ferramenta de produtividade desenvolvida com **Compose Multiplatform**. Seu objetivo é simplificar a vida de desenvolvedores, permitindo gerar todos os tamanhos de ícones necessários para diversas plataformas (Android, iOS, Web, Windows e macOS) a partir de uma única imagem de alta resolução.

## 🚀 Funcionalidades principais

- **Geração Multiplataforma**: Exporta ícones otimizados para 5 sistemas operacionais diferentes em um único clique.
- **Processamento de Imagem Inteligente**: Corta automaticamente a imagem para um formato quadrado e utiliza algoritmos de redimensionamento de alta qualidade.
- **Visualização em Tempo Real**: Veja como seu ícone ficará no Dock do macOS, na tela inicial do Android/iOS e em abas de navegadores antes mesmo de exportar.
- **Interface Moderna**: UI baseada em Material Design 3, com suporte a temas dinâmicos e uma experiência de usuário fluida.
- **Exportação em Lote**: Organiza os arquivos gerados em pastas estruturadas (ex: `mipmap-hdpi` para Android), prontas para serem copiadas para o seu projeto.

## 🛠️ Tecnologias Utilizadas

- **Kotlin Multiplatform (KMP)**: Compartilhamento de lógica de negócio entre Android e Desktop.
- **Compose Multiplatform**: Framework de UI declarativo para criar interfaces nativas em múltiplas plataformas.
- **Jetpack Lifecycle & ViewModel**: Gerenciamento de estado robusto e reativo.
- **Kotlin Coroutines**: Processamento assíncrono para garantir que a interface nunca trave durante o redimensionamento de imagens pesadas.

## 📂 Estrutura de Pastas de Exportação

Ao clicar em "Exportar em Lote", o aplicativo cria a seguinte estrutura organizada por data:

```text
Export_YYYYMMdd_HHmm/
├── Android/
│   ├── mipmap-mdpi/ic_launcher.png
│   ├── ... (outras densidades)
│   └── playstore-icon.png
├── iOS/
│   ├── AppIcon-20x20@2x.png
│   └── ... (tamanhos Apple)
├── Web/
│   ├── favicon-16x16.png
│   └── ...
├── Windows/
├── macOS/
```

## 📖 Como Usar

1. **Carregar**: Clique no card principal para selecionar uma imagem quadrada de alta resolução (PNG, JPG ou BMP).
2. **Selecionar**: Escolha as plataformas para as quais você deseja gerar os ícones.
3. **Validar**: Verifique as visualizações prévias para garantir que o enquadramento está correto.
4. **Exportar**: Clique no botão flutuante "Exportar em Lote" e escolha onde salvar seus novos ícones.
