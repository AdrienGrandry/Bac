package mouvement;

public class Boisson {
    private int id;
    private String nom;

    public Boisson(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return nom;
    }
}