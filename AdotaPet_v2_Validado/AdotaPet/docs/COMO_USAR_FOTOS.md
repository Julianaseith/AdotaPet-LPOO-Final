# AdotaPet — fotos dos pets

## Cadastrar um novo pet com foto

1. Abra a pasta do projeto `AdotaPet` e execute `executar.bat` (Windows com Java instalado) ou use o NetBeans (**F6**).
2. Entre em **Administrador** (`admin` / `admin123`).
3. Clique em **+ Novo pet**.
4. Preencha as informações do animal.
5. Clique em **Selecionar foto**, escolha um arquivo **.jpg, .jpeg ou .png** com até 12 MB.
6. Confira a prévia e clique **Cadastrar pet**.
7. Selecione o animal na tabela: a imagem aparece na ficha à direita.

## Ver foto como visitante

Volte ao início e clique **Explorar pets**. Selecione um animal disponível na tabela. A foto aparece na ficha, à direita, sem botão de edição. Se um animal já estiver adotado, ele não aparece na lista pública.

## Editar ou remover foto

No Administrador, selecione o pet, clique **Editar** e use **Selecionar foto** para trocar ou **Remover foto** para apagar. Clique em **Salvar alterações**. O arquivo anterior sem uso também é removido após salvar com sucesso.

## Onde ficam os arquivos?

- **Dados:** `AdotaPet/dados/pets.txt`
- **Imagens:** `AdotaPet/dados/fotos/`
- O TXT grava apenas o **nome** da foto, como `pet-4-xxx.png`; o arquivo da imagem fica na pasta `fotos`.
- A foto é convertida para PNG e reduzida para até 1200 pixels por lado.
- **Não é necessário colar fotos manualmente** na pasta: o programa faz isso quando o cadastro é salvo.

## Migrar cadastros antigos com segurança

**Antes de extrair ou substituir arquivos, faça backup de `dados/pets.txt` e de `dados/fotos/` (se existir).** Se você já cadastrou Flor, gatinha e Romildo, conserve seu próprio arquivo TXT. A versão atualizada reconhece registros antigos sem a coluna da foto. Abra a versão nova e depois copie seu antigo `pets.txt` para o diretório `dados` dela, preferencialmente com o aplicativo fechado.

## Testar se persiste

1. Cadastre um pet com imagem e confirme a ficha.
2. Feche o programa **completamente**.
3. Abra outra vez pelo mesmo projeto.
4. Selecione o pet e veja se a foto reapareceu.
5. Abra `dados/pets.txt`: deve existir um campo extra com um nome `.png` na linha desse pet. Veja também a imagem em `dados/fotos/`.

Se a foto não aparecer após reabrir, confirme se os dois arquivos (`pets.txt` e a imagem na pasta `fotos`) continuam juntos na mesma pasta do projeto, e se executou o programa na pasta correta.
