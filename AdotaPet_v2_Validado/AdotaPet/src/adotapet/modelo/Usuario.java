package adotapet.modelo;

/** Classe-base dos perfis; permissões são definidas por polimorfismo. */
public abstract class Usuario {
    private final String nome;

    protected Usuario(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O usuário precisa ter um nome.");
        }
        this.nome = nome.trim();
    }

    public String getNome() {
        return nome;
    }
    public abstract String getPermissoes();
    public abstract boolean podeGerenciar();
}
