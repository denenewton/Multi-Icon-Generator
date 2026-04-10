# MainViewModel

O `MainViewModel` é o coração da lógica de negócio da aplicação. Ele gerencia o estado da interface (`UiState`) e coordena as interações entre o usuário e os serviços de plataforma.

## Responsabilidades

- **Gerenciamento de Estado**: Mantém o `UiState`, que inclui a imagem selecionada, as plataformas escolhidas, o status de carregamento e mensagens de erro/sucesso.
- **Seleção de Imagem**: Orquestra a abertura do seletor de arquivos nativo e processa a imagem resultante (ex: cortando para quadrado).
- **Seleção de Plataformas**: Permite ao usuário alternar quais plataformas ele deseja gerar ícones.
- **Exportação em Lote**: Dispara o processo de geração e salvamento dos ícones, atualizando a UI com o progresso em tempo real.

## Principais Funções

### `onPickImage()`
Inicia o fluxo de seleção de imagem. Utiliza o `ImagePicker` injetado (específico da plataforma) e, após receber o bitmap, utiliza o `ImageProcessor` para garantir que a imagem seja quadrada antes de exibi-la.

### `togglePlatform(platform: TargetPlatform)`
Adiciona ou remove uma plataforma da lista de exportação. Isso reflete instantaneamente nas visualizações prévias da tela.

### `onExport()`
Valida se os requisitos mínimos (imagem e plataformas) foram atendidos e chama o `fileExporter`. Ele observa o callback de progresso para manter o usuário informado.

## Fluxo de Dados
Utiliza `StateFlow` (`_uiState`) para garantir que a UI seja reativa e sempre reflita o estado mais recente de forma consistente.
