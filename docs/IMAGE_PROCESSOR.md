# ImageProcessor

O `ImageProcessor` é um objeto utilitário (`object`) projetado para o processamento eficiente de imagens e centralização das configurações de ícones por plataforma.

## Funções Utilitárias

### `scaleBitmap(source: ImageBitmap, targetWidth: Int, targetHeight: Int)`
Redimensiona uma imagem utilizando o `Canvas` do Compose com o filtro de alta qualidade.
- **Entrada**: Bitmap original e dimensões alvo.
- **Saída**: Novo bitmap redimensionado mantendo a nitidez.

### `cropToSquare(bitmap: ImageBitmap)`
Garante que a imagem resultante seja quadrada.
- **Como Funciona**: Calcula a menor dimensão (largura ou altura) e extrai o centro da imagem original para formar o novo quadrado.

## Configurações de Plataformas

O objeto `platformConfigs` mapeia cada plataforma (`Android`, `iOS`, `Web`, `Windows`, `macOS`) para uma lista de `IconConfig`.
- **IconConfig**: Define o nome do arquivo (e caminhos de subdiretório se necessário, como no Android) e o tamanho exato em pixels para aquele ícone.

### Por que Centralizar Aqui?
- Facilidade de Manutenção: Adicionar um novo tamanho de ícone para qualquer plataforma requer apenas uma linha de mudança nesta lista.
- Consistência: Garante que as visualizações prévias e a exportação final usem exatamente os mesmos tamanhos definidos pela indústria.
