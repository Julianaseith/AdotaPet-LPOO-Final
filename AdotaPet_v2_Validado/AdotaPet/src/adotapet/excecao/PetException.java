package adotapet.excecao;

/** Representa uma violação das regras de negócio do cadastro de animais. */
public class PetException extends Exception {
    public PetException(String mensagem) {
        super(mensagem);
    }
}
