# Multi Icon Generator 🚀

**Multi Icon Generator** é uma ferramenta poderosa e intuitiva desenvolvida com **Compose Multiplatform**. Ela permite que desenvolvedores gerem, de forma automatizada, todos os tamanhos de ícones necessários para as principais plataformas do mercado a partir de uma única imagem de alta resolução.

![Compose Multiplatform](https://img.shields.io/badge/Compose-Multiplatform-blue?style=flat-square&logo=kotlin)
![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20Desktop-green?style=flat-square)

## ✨ Principais Funcionalidades

- **Geração Multi-Resolução**: Cria automaticamente dezenas de variações de tamanho para garantir nitidez em todas as densidades de tela.
- **Suporte a Formatos Nativos de Desktop**:
    - **Windows (.ico)**: Gera um único arquivo container contendo resoluções de 16px a 256px.
    - **macOS (.icns)**: Gera o formato oficial da Apple com suporte a telas Retina (até 1024px).
- **Visualização em Tempo Real (Live Preview)**: Veja como seu ícone ficará aplicado contextualmente no Dock do macOS, na barra de tarefas do Windows, na Home do Android/iOS e em abas de navegadores.
- **Processamento Inteligente**:
    - Corte automático para formato quadrado (Center Crop).
    - Redimensionamento com filtragem de alta qualidade para evitar serrilhados.
- **Interface Moderna (Material Design 3)**: UI limpa, responsiva e com feedback visual imediato.
- **Exportação Estruturada**: Organiza os arquivos em pastas prontas para importação direta nos projetos (ex: estruturas `mipmap` para Android).

## 🛠️ Tecnologias

- **Kotlin Multiplatform (KMP)**: Lógica compartilhada entre mobile e desktop.
- **Compose Multiplatform**: UI declarativa e moderna.
- **Jetpack ViewModel**: Gerenciamento de estado robusto.
- **Coroutines**: Processamento de imagem em background para manter a UI fluida.

## 📱 Plataformas Suportadas

| Plataforma | Formatos Gerados | Detalhes |
| :--- | :--- | :--- |
| **Android** | PNG | Mipmaps (mdpi a xxxhdpi) + Ícone da Play Store (512px). |
| **iOS** | PNG | Todos os tamanhos de AppIcon (20pt a 1024px). |
| **Windows** | PNG, **.ico** | Ícones individuais + Container `.ico` multi-resolução. |
| **macOS** | PNG, **.icns** | Ícones individuais + Container `.icns` de alta qualidade. |
| **Web** | PNG | Favicons, Apple Touch Icon e Manifest Icons. |

## 🚀 Como Executar

### Pré-requisitos
- JDK 17 ou superior.
- Android Studio ou IntelliJ IDEA.

### Desktop (JVM)
Para rodar a versão Desktop (Windows/macOS/Linux):
```bash
./gradlew :composeApp:run
```

### Android
Para instalar no seu dispositivo ou emulador Android:
```bash
./gradlew :composeApp:installDebug
```

## 📂 Estrutura do Projeto

- `composeApp/src/commonMain`: Toda a lógica de UI e processamento de imagem compartilhada.
- `composeApp/src/desktopMain`: Implementações nativas para Desktop (Swing File Chooser, Encoders ICO/ICNS).
- `composeApp/src/androidMain`: Implementações nativas para Android (MediaStore, Activity Results).
- `docs/`: Documentação técnica detalhada de cada módulo.

---
Desenvolvido para facilitar o fluxo de trabalho de design-to-code. 🎨📦

**Desenvolvedor:** Daniel dos Santos Araújo  
**Ano:** 2026  
**Empresa:** DENENEWTON
