package adotapet.modelo;

/** Subclasse de Animal com características próprias dos cães. */
public class Cachorro extends Animal {
    private String porte;
    private String energia;

    public Cachorro(int id, String nome, int idade, String raca, String sexo,
                    boolean disponivel, String porte, String energia) {
        super(id, nome, idade, raca, sexo, disponivel);
        setPorte(porte);
        setEnergia(energia);
    }

    public String getPorte() {
        return porte;
    }
    public void setPorte(String porte) {
        if (!"Pequeno".equals(porte) && !"Médio".equals(porte) && !"Grande".equals(porte)) {
            throw new IllegalArgumentException("Selecione um porte válido para o cachorro.");
        }
        this.porte = porte;
    }

    public String getEnergia() {
        return energia;
    }
    public void setEnergia(String energia) {
        if (!"Baixa".equals(energia) && !"Média".equals(energia) && !"Alta".equals(energia)) {
            throw new IllegalArgumentException("Selecione um nível de energia válido.");
        }
        this.energia = energia;
    }

    @Override
    public String getEspecie() {
        return "Cachorro";
    }

    @Override
    public String getCuidadosEspeciais() {
        String atividade = "Alta".equals(energia) ? "passeios e brincadeiras frequentes"
                : "Média".equals(energia) ? "passeios diários e atividades moderadas"
                : "passeios leves e descanso adequado";
        return "Cachorro de porte " + porte.toLowerCase() + " e energia " + energia.toLowerCase()
                + ": precisa de " + atividade + ", alimentação adequada e acompanhamento veterinário.";
    }

    @Override
    public Animal copiaComId(int novoId) {
        Cachorro copia = new Cachorro(novoId, getNome(), getIdade(), getRaca(), getSexo(),
                isDisponivel(), porte, energia);
        copia.setFoto(getFoto());
        return copia;
    }
}
