# MainScreen

O `MainScreen` é o componente central da interface do usuário (UI) da aplicação, construído inteiramente com **Jetpack Compose**. Ele segue os princípios do **Material Design 3** para oferecer uma experiência moderna e intuitiva.

## Estrutura do Layout

A tela utiliza um `Scaffold` como base, organizando os elementos em:
1. **TopAppBar**: Uma barra superior centralizada com o nome do aplicativo e um ícone decorativo.
2. **LazyColumn**: Um contêiner de rolagem eficiente que organiza as seções de interação em uma lista vertical com espaçamento generoso.
3. **FloatingActionButton (FAB)**: Um botão de ação flutuante estendido para a exportação em lote.

## Componentes Principais

### `ImagePickerCard`
Um card interativo com bordas arredondadas (`24.dp`) que serve como o primeiro passo para o usuário. 
- **Estado Vazio**: Exibe uma ilustração minimalista e instruções para carregar uma imagem.
- **Estado com Imagem**: Exibe uma prévia da imagem selecionada em escala adequada.

### `PlatformSelectorCard`
Um card que agrupa `FilterChips` para a seleção de múltiplas plataformas simultaneamente. Utiliza um `FlowRow` para garantir que os chips se ajustem automaticamente à largura da tela.

### `IconPreviews` (Seção)
Exibe carrosséis horizontais com visualizações contextuais de como o ícone ficará em cada sistema operacional (ex: simulando o Dock do macOS ou a tela inicial do Android).

### `StatusCard`
Um componente animado que fornece feedback visual imediato:
- **Carregamento**: Exibe um indicador de progresso circular.
- **Erro**: Fundo vermelho com ícone de alerta.
- **Sucesso**: Fundo verde/terciário confirmando o local da exportação.

## Design Responsivo e Moderno
- **Animações**: Utiliza `AnimatedVisibility` para transições suaves ao exibir o botão de exportação e o card de status.
- **Elevações e Sombras**: Uso estratégico de elevação tonal para separar as seções sem poluir visualmente a interface.
