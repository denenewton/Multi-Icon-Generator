# Platform.desktop.kt Documentation

This document explains the implementation of platform-specific features for the Desktop target (macOS/Windows/Linux) in the MultiIconGenerator project.

## 1. The `expect/actual` Pattern

MultiIconGenerator uses the Kotlin Multiplatform `expect/actual` mechanism to abstract platform-specific operations.
- **`commonMain`**: Defines `expect` functions and interfaces like `ImagePicker` and `FileExporter`.
- **`desktopMain`**: Provides the `actual` implementation using Java/AWT/Swing APIs appropriate for a desktop environment.

## 2. DesktopImagePicker Implementation

The `DesktopImagePicker` handles image selection using standard Java Desktop APIs:
- **`JFileChooser`**: Opens a native-style file picker dialog.
- **`FileNameExtensionFilter`**: Restricts selection to common image formats (JPG, PNG, BMP).
- **`ImageIO`**: Reads the selected file into a `BufferedImage`, which is then converted to a Compose `ImageBitmap` via the `toComposeImageBitmap()` extension.

## 3. DesktopFileExporter Implementation

The `DesktopFileExporter` manages the batch export of generated icons:
- **Default Directory**: Automatically points to the user's `Downloads` folder using `System.getProperty("user.home")`.
- **Directory Selection**: Uses `JFileChooser` in `DIRECTORIES_ONLY` mode.
- **Suggested Name**: Pre-fills the dialog with `MultiIconGenerator_Exports` as the target folder name.
- **Session Organization**: Creates a timestamped subfolder (`Export_YYYYMMdd_HHmm`) to avoid overwriting previous exports.
- **Platform Hierarchy**: Automatically generates subfolders for each platform (Android, iOS, etc.) and respects the internal directory structures (e.g., `mipmap-hdpi/`).

## 4. Skia and AWT Interoperability

Since Compose Multiplatform uses **Skia** for rendering and standard Java Desktop uses **AWT/Swing**, interoperability is key:
- **Processing**: All scaling and cropping happen in `commonMain` using Skia-backed Compose `Canvas` for high performance and quality.
- **Saving**: The `desktopMain` implementation uses `ImageBitmap.toAwtImage()` to convert the Skia-rendered bitmap into a `BufferedImage`.
- **Encoding**: `ImageIO.write()` is then used to encode and save the image as a standard PNG file on the local file system.
