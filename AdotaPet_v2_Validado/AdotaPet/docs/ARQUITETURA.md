# Arquitetura do AdotaPet

## Divisão de responsabilidades

- **Modelo (`modelo/`)**: classes abstratas `Animal` e `Usuario`, subclasses e seus atributos. Encapsulamento, construtores, validações e polimorfismo.
- **Serviço (`servico/GerenciadorPets`)**: regras de negócio; gerencia uma coleção `ArrayList<Animal>`; recebe um `Usuario` nas operações e impede alterações sem permissão administrativa. Exibe apenas animais disponíveis ao visitante.
- **Persistência (`servico/ArquivoPets`)**: serializa/deserializa registros sem dependências externas. Exige cabeçalho válido, valida IDs duplicados e usa arquivo temporário para salvar.
- **Exceções (`excecao/PetException`)**: sinaliza ações negadas ou animal não encontrado. Erros técnicos de arquivos usam `IOException`.
- **Interface (`tela/`)**: janelas base em `JFrame`, painel reutilizado, modelo `AbstractTableModel`, editor modal e classe de tema. Botões encaminham operações ao gerenciador, que valida as permissões novamente.

## Fluxo de cadastro

1. O administrador faz login e abre o painel de gestão.
2. Clica em **+ Novo pet**, preenche o formulário e confirma.
3. `DialogoPet` valida os campos; cria um `Cachorro` ou `Gato` de ID 0 (ainda não cadastrado).
4. `GerenciadorPets.cadastrar()` confirma permissão e gera ID único.
5. `ArquivoPets.salvar()` grava a lista proposta num TXT temporário e substitui o arquivo oficial.
6. Somente depois do salvamento concluído, `GerenciadorPets` atualiza o `ArrayList` em memória.
7. A tela atualiza a tabela e o resumo, com o novo ID selecionado.

Em edição, exclusão e alteração de situação, a operação segue a mesma estratégia: **propor -> salvar -> confirmar na memória**. Caso a escrita falhe, a coleção continua intacta.

## Fluxo de visitante

1. O visitante acessa sem senha.
2. A chamada `gerenciador.listar(visitante, ...)` filtra obrigatoriamente `disponivel == true`.
3. A tela não oferece botões administrativos.
4. Mesmo que outra classe tente chamar métodos de alteração usando um `Visitante`, o serviço dispara `PetException`.
5. A listagem retorna **cópias** dos animais para impedir alterações acidentais na coleção interna.

## Polimorfismo: exemplos para apresentar

```java
Animal pet = new Cachorro(1, "Thor", 3, "Labrador", "Macho", true, "Grande", "Alta");
System.out.println(pet.getCuidadosEspeciais());

pet = new Gato(2, "Luna", 2, "SRD", "Fêmea", true, "Curta");
System.out.println(pet.getCuidadosEspeciais());
```

`getCuidadosEspeciais()` é um método **abstrato** declarado em `Animal` e **sobrescrito** nas subclasses. Não é necessário converter a referência para cada espécie para pedir os cuidados.

O segundo exemplo de polimorfismo é `Usuario.podeGerenciar()`: retorna `true` no administrador e `false` no visitante, e o serviço consulta esse método antes de permitir mudanças.

## Formato de dados

Cabeçalho: `#ADOTAPET|1|<proximoId>`.

Cão: `CAO|id|nome|idade|raca|sexo|disponivel|porte|energia`.

Gato: `GATO|id|nome|idade|raca|sexo|disponivel|pelagem`.

Regras:
- IDs positivos e exclusivos; próximo ID deve ser maior que qualquer ID atual.
- Sexo: `Macho` ou `Fêmea`. Situação: `true` (disponível) ou `false` (adotado).
- Porte: `Pequeno`, `Médio` ou `Grande`. Energia: `Baixa`, `Média` ou `Alta`.
- Pelagem: `Curta`, `Média`, `Longa`, `Sem pelos`.
- `\|` representa o caractere literal `|`; `\\` representa `\`; `\n` e `\r` representam quebras.
- UTF-8, inclusive acentuação. O programa detecta erros estruturais e não sobrescreve arquivo inválido.

## Observações sobre concorrência e integridade

O gerenciador sincroniza as operações dentro do processo (`synchronized`). A gravação usa `Files.move(..., ATOMIC_MOVE, REPLACE_EXISTING)` quando o sistema de arquivos aceita; caso contrário, usa `REPLACE_EXISTING` após finalizar o arquivo temporário. Isso **reduz risco de corrupção** durante gravação, mas não substitui backups e não transforma o TXT em uma solução multiusuário distribuída.

## Diálogo do formulário

`DialogoPet` muda os campos específicos conforme a espécie; para cachorro exibe porte e energia, para gato exibe pelagem. O mesmo diálogo é usado para cadastrar e editar. Valida também idade e nome, com mensagens em `JOptionPane`.

## GUI Builder do NetBeans

A base de `TelaInicial`, `TelaAdmin` e `TelaVisitante` tem os pares `.java/.form` e um painel de conteúdo. O detalhamento interno é montado em Java para reutilização e manutenção. Por isso, o editor visual mostra apenas o contêiner das janelas, e **não** os controles criados dinamicamente. É importante validar a abertura dos arquivos `.form` no NetBeans que será utilizado na apresentação.
