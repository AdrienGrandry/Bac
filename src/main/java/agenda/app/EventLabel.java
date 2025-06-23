package agenda.app;

import agenda.model.EventModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Composant JLabel personnalisé représentant un événement avec interaction au clic.
 */
public class EventLabel extends JLabel {
    private final EventModel event;

    /**
     * Constructeur.
     * @param event l'objet EventModel associé
     */
    public EventLabel(EventModel event) {
        super(event.getTitle());
        this.event = event;

        // Configuration visuelle de base
        setOpaque(true);
        setBackground(new Color(66, 133, 244));  // bleu Google officiel
        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI", Font.PLAIN, 12));
        setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 20)); // hauteur fixe, largeur max flexible
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // curseur main
    }
}