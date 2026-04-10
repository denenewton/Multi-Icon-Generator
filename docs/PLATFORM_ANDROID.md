# Platform Android

A implementação para Android (`Platform.android.kt`) gerencia o acesso ao seletor de imagens do sistema e à exportação para a pasta de Downloads do dispositivo.

## Funcionalidades de Plataforma

### Seletor de Imagem (`AndroidImagePicker`)
Utiliza a API moderna de `ActivityResultContracts.GetContent()` para solicitar uma imagem ao usuário. 
- **Gestão de Uri**: Converte o URI de retorno em um `InputStream` para decodificação segura em `Bitmap`.
- **Compatibilidade**: Garante que o seletor funcione de forma assíncrona usando `suspendCancellableCoroutine`.

### Exportador de Arquivos (`AndroidFileExporter`)
Lida com as complexidades do sistema de arquivos do Android, variando conforme a versão do SDK.

#### Para Android 10+ (SDK >= 29)
Utiliza o `MediaStore` para inserir arquivos na pasta pública de `Downloads`. 
- **Relative Path**: Organiza os ícones em subdiretórios dentro de `Downloads/MultiIconGenerator_Exports/Export_[Data]`.
- **Sem Permissões**: Esse método moderno não exige a permissão `WRITE_EXTERNAL_STORAGE` para salvar em pastas públicas.

#### Para Versões Anteriores (SDK < 29)
Utiliza a API tradicional de `File` e `FileOutputStream` para salvar os ícones diretamente no diretório retornado por `Environment.getExternalStoragePublicDirectory`.

## Inicialização e Contexto
A plataforma Android exige uma referência de `Context` ou `Activity`. Isso é gerenciado pela função `initAndroidPlatform(activity)`, que deve ser chamada na `MainActivity`. O seletor de imagens e o exportador são então inicializados com esse contexto persistente.
