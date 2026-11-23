package adotapet.modelo;

/** Credenciais fixas para fins exclusivamente didáticos, não para uso real. */
public class Administrador extends Usuario {
    public static final String LOGIN_DEMONSTRACAO = "admin";
    public static final String SENHA_DEMONSTRACAO = "admin123";
    private final String login;

    private Administrador(String nome, String login) {
        super(nome);
        this.login = login;
    }

    public static Administrador autenticar(String login, char[] senha) {
        // A autenticação simples existe apenas para demonstrar perfis na disciplina.
        String informada = senha == null ? "" : new String(senha);
        if (LOGIN_DEMONSTRACAO.equals(login == null ? "" : login.trim())
                && SENHA_DEMONSTRACAO.equals(informada)) {
            return new Administrador("Equipe da ONG", LOGIN_DEMONSTRACAO);
        }
        return null;
    }

    public String getLogin() {
        return login;
    }
    @Override
    public String getPermissoes() {
        return "Consultar e gerenciar animais";
    }
    @Override
    public boolean podeGerenciar() {
        return true;
    }
}
