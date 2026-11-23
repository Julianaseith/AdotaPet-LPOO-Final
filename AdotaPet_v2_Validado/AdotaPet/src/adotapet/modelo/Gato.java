package adotapet.modelo;

/** Subclasse de Animal com informações específicas dos gatos. */
public class Gato extends Animal {
    private String pelagem;

    public Gato(int id, String nome, int idade, String raca, String sexo,
                boolean disponivel, String pelagem) {
        super(id, nome, idade, raca, sexo, disponivel);
        setPelagem(pelagem);
    }

    public String getPelagem() {
        return pelagem;
    }
    public void setPelagem(String pelagem) {
        if (!"Curta".equals(pelagem) && !"Média".equals(pelagem)
                && !"Longa".equals(pelagem) && !"Sem pelos".equals(pelagem)) {
            throw new IllegalArgumentException("Selecione uma pelagem válida para o gato.");
        }
        this.pelagem = pelagem;
    }

    @Override
    public String getEspecie() {
        return "Gato";
    }

    @Override
    public String getCuidadosEspeciais() {
        String cuidado = "Longa".equals(pelagem) || "Média".equals(pelagem)
                ? "escovação regular da pelagem" : "cuidados regulares com a pele e o pelo";
        if ("Sem pelos".equals(pelagem)) {
            cuidado = "proteção da pele e atenção à temperatura";
        }
        return "Gato de pelagem " + pelagem.toLowerCase() + ": precisa de " + cuidado
                + ", caixa de areia limpa, ambiente seguro e acompanhamento veterinário.";
    }

    @Override
    public Animal copiaComId(int novoId) {
        Gato copia = new Gato(novoId, getNome(), getIdade(), getRaca(), getSexo(),
                isDisponivel(), pelagem);
        copia.setFoto(getFoto());
        return copia;
    }
}
