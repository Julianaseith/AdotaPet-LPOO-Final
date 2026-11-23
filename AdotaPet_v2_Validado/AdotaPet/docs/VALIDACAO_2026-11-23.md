# Validação técnica do AdotaPet — 23/11/2026

Ambiente: OpenJDK 21.0.12, Apache Ant 1.10.15, Linux com tela gráfica virtual (Xvfb).

## Resultados

- `ant clean jar`: **BUILD SUCCESSFUL**. Foram compilados os 19 arquivos `.java` da aplicação e gerado `dist/AdotaPet.jar`.
- `xvfb-run ant test`: **19/19 testes aprovados**. Cobertura: autenticação didática, herança, polimorfismo, CRUD, IDs, busca, permissões, validações, persistência TXT, tolerância a erros, fotos e visualização de miniatura.
- Ferramenta `CapturaInterface`: telas inicial, administrador, visitante, formulário de cadastro e detalhes de pet foram abertas com sucesso em ambiente gráfico virtual.
- `java -jar dist/AdotaPet.jar`: a aplicação iniciou sem emitir erros e permaneceu aberta até o encerramento planejado do teste gráfico.
- Classe principal: `adotapet.Principal`. Requisito `main.class` do projeto NetBeans e manifesto do JAR confirmados.

## Observações

- O projeto usa **Java Swing + Ant** e possui `build.xml` e `nbproject/`, para abrir como projeto no NetBeans.
- O código foi compilado e a interface executada fora do NetBeans. Não foi possível conferir a edição visual dos arquivos `.form` no editor do NetBeans nem executar diretamente no Windows; recomenda-se uma conferência final local.
- Avisos de compatibilidade do compilador com o alvo Java 8 no JDK 21 não representam erros de compilação.
- Os testes não garantem ausência total de falhas não cobertas.
- O projeto inclui seis animais fictícios em `dados/`. Antes de sobrescrever uma cópia com cadastros reais, faça backup da pasta `dados/`.

## Comandos úteis

```text
ant clean jar
ant test
ant run
```

No NetBeans, abra a pasta `AdotaPet` (contém `nbproject`, `build.xml`, `src`) e use F6 para executar o projeto.
