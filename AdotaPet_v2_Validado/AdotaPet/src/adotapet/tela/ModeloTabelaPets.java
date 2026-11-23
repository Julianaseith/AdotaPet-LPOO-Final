package adotapet.tela;

import adotapet.modelo.Animal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/** Modelo da tabela; a lista contém apenas cópias autorizadas pelo serviço. */
public class ModeloTabelaPets extends AbstractTableModel {
    private static final String[] COLUNAS = {"ID", "Nome", "Espécie", "Raça", "Idade", "Sexo", "Situação"};
    private List<Animal> animais = new ArrayList<Animal>();

    public void atualizar(List<Animal> animais) {
        this.animais = new ArrayList<Animal>(animais);
        fireTableDataChanged();
    }

    public Animal getAnimal(int linha) {
        if (linha < 0 || linha >= animais.size()) {
            return null;
        }
        return animais.get(linha);
    }

    @Override
    public int getRowCount() {
        return animais.size();
    }
    @Override
    public int getColumnCount() {
        return COLUNAS.length;
    }
    @Override
    public String getColumnName(int coluna) {
        return COLUNAS[coluna];
    }
    @Override
    public Class<?> getColumnClass(int coluna) {
        return coluna == 0 || coluna == 4 ? Integer.class : String.class;
    }
    @Override
    public boolean isCellEditable(int linha, int coluna) {
        return false;
    }

    @Override
    public Object getValueAt(int linha, int coluna) {
        Animal animal = animais.get(linha);
        switch (coluna) {
            case 0: return animal.getId();
            case 1: return animal.getNome();
            case 2: return animal.getEspecie();
            case 3: return animal.getRaca();
            case 4: return animal.getIdade();
            case 5: return animal.getSexo();
            case 6: return animal.getSituacao();
            default: return "";
        }
    }
}
