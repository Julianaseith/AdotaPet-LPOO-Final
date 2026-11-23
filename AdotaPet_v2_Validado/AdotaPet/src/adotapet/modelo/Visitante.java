package adotapet.modelo;

/** Perfil público: pode consultar, mas nunca modificar os cadastros. */
public class Visitante extends Usuario {
    public Visitante() {
        super("Visitante");
    }

    @Override
    public String getPermissoes() {
        return "Consultar animais disponíveis";
    }
    @Override
    public boolean podeGerenciar() {
        return false;
    }
}
