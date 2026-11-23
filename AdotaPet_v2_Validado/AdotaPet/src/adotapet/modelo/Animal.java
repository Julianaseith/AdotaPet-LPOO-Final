package adotapet.modelo;

/**
 * Classe-base para os animais da ONG. Os atributos comuns são encapsulados;
 * cada espécie define seus cuidados especiais por polimorfismo.
 */
public abstract class Animal {
    private final int id;
    private String nome;
    private int idade;
    private String raca;
    private String sexo;
    private boolean disponivel;
    /** Nome do arquivo da foto em dados/fotos; vazio quando não há foto. */
    private String foto = "";

    protected Animal(int id, String nome, int idade, String raca, String sexo, boolean disponivel) {
        if (id < 0) {
            throw new IllegalArgumentException("O ID não pode ser negativo.");
        }
        this.id = id;
        setNome(nome);
        setIdade(idade);
        setRaca(raca);
        setSexo(sexo);
        this.disponivel = disponivel;
    }

    private static String obrigatorio(String texto, String campo) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe " + campo + ".");
        }
        return texto.trim();
    }

    public int getId() {
        return id;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = obrigatorio(nome, "o nome");
    }
    public int getIdade() {
        return idade;
    }
    public void setIdade(int idade) {
        if (idade < 0 || idade > 100) {
            throw new IllegalArgumentException("A idade deve estar entre 0 e 100 anos.");
        }
        this.idade = idade;
    }
    public String getRaca() {
        return raca;
    }
    public void setRaca(String raca) {
        this.raca = obrigatorio(raca, "a raça");
    }
    public String getSexo() {
        return sexo;
    }
    public void setSexo(String sexo) {
        String valor = obrigatorio(sexo, "o sexo");
        if (!valor.equals("Macho") && !valor.equals("Fêmea")) {
            throw new IllegalArgumentException("O sexo deve ser Macho ou Fêmea.");
        }
        this.sexo = valor;
    }
    public boolean isDisponivel() {
        return disponivel;
    }
    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }

    public String getSituacao() {
        return disponivel ? "Disponível" : "Adotado";
    }

    public String getFoto() {
        return foto;
    }

    /** Guarda somente o nome do arquivo, nunca um caminho absoluto. */
    public void setFoto(String nomeArquivo) {
        String nome = nomeArquivo == null ? "" : nomeArquivo.trim();
        if (!nome.isEmpty() && (!nome.matches("[A-Za-z0-9][A-Za-z0-9_.-]{0,180}")
                || nome.contains(".."))) {
            throw new IllegalArgumentException("Nome de arquivo de foto inválido.");
        }
        foto = nome;
    }

    /** O tipo é usado para exibição e persistência. */
    public abstract String getEspecie();

    /** Método abstrato sobrescrito em Cachorro e Gato. */
    public abstract String getCuidadosEspeciais();

    /** Cria uma cópia independente com outro ID (0 significa ainda não cadastrado). */
    public abstract Animal copiaComId(int novoId);

    @Override
    public String toString() {
        return "#" + id + " - " + nome + " (" + getEspecie() + ")";
    }
}
