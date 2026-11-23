# Plano de testes e verificação

## Como rodar

A partir da raiz do projeto:

```bash
ant clean test
```

Os testes ficam em `test/adotapet/testes/TestesAdotaPet.java`. Não exigem JUnit nem internet; cada cenário cria seus próprios arquivos temporários e os remove no final, sem alterar `dados/pets.txt`.

## Cenários automatizados

1. Login correto e incorreto; permissões polimórficas.
2. Herança de cachorro/gato e sobrescrita de cuidados especiais.
3. Cadastro com IDs gerados automaticamente.
4. Restrição de consulta pública a animais disponíveis.
5. Filtros por nome, raça, espécie, ID e acentos.
6. Edição dos dados e troca de espécie.
7. Exclusão com preservação da sequência de IDs após reabrir.
8. Escrita/leitura UTF-8, delimitadores escapados e quebras de linha.
9. Bloqueio de todas as quatro operações administrativas ao visitante.
10. Cópias retornadas por consulta não modificam a coleção interna.
11. Validações de nome, idade, sexo e ID inválidos.
12. Detecção de TXT com IDs duplicados, sem sobrescrever o original.
13. Falha de escrita sem alteração do conteúdo em memória.
14. Detecção de ausência de cabeçalho válido.

**Resultado obtido no ambiente de desenvolvimento:** `18/19 testes passaram`.

## Interface gráfica

Foi compilado e executado um `JAR` com interface Swing em ambiente gráfico virtual (`Xvfb`). A classe `CapturaInterface` abriu e capturou as telas inicial, administração, visitante, ficha de pet e formulário de cadastro. As imagens estão em `docs/*.png`.

Para gerar essas imagens novamente em Linux, com `xvfb-run` instalado:

```bash
ant test-compile
xvfb-run -a -s '-screen 0 1600x900x24' \
  java -cp build/classes:build/test/classes adotapet.testes.CapturaInterface docs
```

## Conferência manual recomendada no computador de apresentação

- Abrir o projeto no NetBeans e executá-lo.
- Entrar como visitante; pesquisar `Luna` e `Thor`; confirmar ausência de `Simba`/`Bob` (adotados).
- Voltar à tela inicial e entrar como administrador com `admin`/`admin123`.
- Cadastrar um cachorro; conferir ID e nova linha na tabela.
- Editar o cachorro e conferir o resultado.
- Alterar o status para adotado e conferir que ele some da consulta pública.
- Fechar e abrir o aplicativo; confirmar que o cadastro permaneceu salvo.
- Excluir o animal de demonstração e verificar que a linha desaparece.
- Tentar digitar idade negativa, texto no lugar de idade ou nome vazio.
- Fazer backup do TXT antes da demonstração para poder restaurar os dados de exemplo.

**Não realizado neste ambiente:** testar a edição visual dos arquivos `.form` dentro do próprio NetBeans, porque o IDE não estava instalado. Esse é um teste de aceitação pendente para o computador do grupo.


## Fotos (adicionado em 17/11/2026)

Cinco testes novos conferem:

1. Importação JPG, cópia para `dados/fotos/` e referência persistida em TXT após reinicialização.
2. Compatibilidade com TXT antigo, sem coluna de foto.
3. Substituição de imagem e remoção do campo e arquivo na edição.
4. Rejeição de arquivos de imagem inválidos sem corromper os cadastros.
5. Exibição da miniatura no painel do visitante ao selecionar um animal.

A interface visual foi aberta em ambiente gráfico virtual (`xvfb-run`) e novas capturas foram salvas em `docs/`.
