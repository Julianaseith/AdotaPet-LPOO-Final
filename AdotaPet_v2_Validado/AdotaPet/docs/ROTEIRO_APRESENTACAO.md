# Roteiro de apresentação — AdotaPet

Objetivo: apresentar o aplicativo e demonstrar os **conceitos exigidos na disciplina**. Ajustar a divisão conforme o número real de integrantes; cada um deve conseguir explicar sua parte.

## Sequência sugerida

**1. Problema e solução:** Uma ONG precisa cadastrar cães e gatos e acompanhar quem está disponível. O AdotaPet oferece uma aplicação local, sem necessidade de internet ou banco de dados.

**2. Interface inicial e perfis:** Demonstrar a entrada sem login como visitante. Mostrar que somente animais disponíveis aparecem, que é possível pesquisar e ler os cuidados especiais. Explicar por que não existem botões de alteração nesse perfil.

**3. Login e administração:** Voltar, entrar com `admin`/`admin123` e mostrar o painel com todas as situações. Cadastrar um pet, editar seus dados, alterar para adotado e excluir um registro de demonstração.

**4. Persistência:** Mostrar `dados/pets.txt`, fechar e abrir o aplicativo e confirmar que as alterações permanecem. Ressaltar que o arquivo é lido na inicialização e salvo quando ocorre uma mudança válida.

**5. Conceitos de LPOO:** Abrir `Animal.java`, `Cachorro.java` e `Gato.java` e mostrar `extends` e `@Override getCuidadosEspeciais()`. Abrir `Usuario.java`, `Administrador.java` e `Visitante.java` para explicar os perfis. Abrir `GerenciadorPets.java` para demonstrar `ArrayList` e bloqueio de permissões. Abrir `ArquivoPets.java` e `PetException.java` para demonstrar leitura, escrita e exceções.

**6. Encerramento:** Dizer o que foi testado (incluindo `ant test`) e destacar as limitações: software acadêmico, TXT local, credenciais didáticas e nenhuma integração com a internet.

## Perguntas que podem aparecer

**Por que `Animal` é abstrata?** Porque a classe representa características compartilhadas; apenas espécies concretas (cachorros/gatos) devem ser cadastradas. Cada espécie define seu método de cuidados.

**Onde está o polimorfismo?** O código chama `getCuidadosEspeciais()` em uma variável `Animal`, sem precisar saber antes se é cão ou gato. O mesmo conceito aparece com `Usuario.podeGerenciar()`.

**Para que serve o `ArrayList`?** Para manter em memória um conjunto que pode crescer, ser percorrido, filtrado e ter elementos removidos ou substituídos.

**Por que usar `try/catch`?** Para impedir que entradas inválidas ou falhas de arquivo derrubem o programa; mensagens explicam o problema ao usuário.

**Como o visitante fica impedido de excluir?** A tela não mostra as ações, e o próprio serviço valida `usuario.podeGerenciar()` antes de alterar qualquer registro.

**Qual a diferença entre `getPermissoes()` e `podeGerenciar()`?** O primeiro descreve o perfil e o segundo é usado para aplicar a regra de acesso, ambos sobrescritos nas subclasses de `Usuario`.

**O sistema é seguro para uma ONG real?** Não. A autenticação é didática e o TXT é local. Para produção seriam necessários controle de acesso robusto, backups, gestão de dados pessoais e persistência adequada.

**O que o grupo ainda precisa fazer?** Conferir os arquivos `.form` no NetBeans, validar o fluxo de apresentação em sua máquina, preencher os formulários obrigatórios e entregar no AVA.

## Antes da demonstração

1. Faça cópia de `dados/pets.txt` e preserve os seis animais de exemplo.
2. Feche qualquer instância anterior do AdotaPet.
3. Abra o projeto na pasta correta, ou execute `executar.bat`.
4. Garanta que todos os integrantes sabem localizar e explicar sua parte do código.
