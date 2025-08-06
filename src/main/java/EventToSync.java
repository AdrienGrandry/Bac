public class EventToSync {
    public final int id;
    public final String agenda;
    public final String titre;
    public final String description;
    public final String date;

    public EventToSync(int id, String agenda, String titre, String description, String date) {
        this.id = id;
        this.agenda = agenda;
        this.titre = titre;
        this.description = description;
        this.date = date;
    }
}