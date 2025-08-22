import agenda.google.GoogleCalendarService;
import principale.GoogleAuthorizeUtil;
import principale.MainFrame;
import ressources.LoadingDialog;
import ressources.Message;
import ressources.dataBase.QueryResult;
import ressources.dataBase.Requete;

import javax.swing.*;
import java.io.IOException;
import java.net.InetAddress;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import static ressources.DateParser.parseStringToLocalDate;

public class MainApplication {
    public static void main(String[] args){
        Locale.setDefault(Locale.FRENCH);

        AtomicReference<LoadingDialog> loadingDialog = new AtomicReference<>(new LoadingDialog(null, "Vérification des l'accès Google..."));
        SwingUtilities.invokeLater(() -> loadingDialog.get().setVisible(true));

        new Thread(() -> {
            try {
                GoogleAuthorizeUtil.authorize();
            } catch (Exception e) {}

            SwingUtilities.invokeLater(() -> {
                loadingDialog.get().dispose();
                loadingDialog.set(new LoadingDialog(null, "Chargement du nombre d'Email..."));
                loadingDialog.get().setVisible(true);
            });

            MainFrame mainFrame = new MainFrame();

            SwingUtilities.invokeLater(() -> {
                loadingDialog.get().setVisible(false);
                loadingDialog.get().dispose();
            });

            mainFrame.setVisible(true);
        }).start();

        try {
            ajouterAgenda();
        } catch (Exception e) {
            Message.showErrorMessage("Erreur de l'ajouterAgenda", e.getMessage());
        }
    }

    private static void ajouterAgenda() throws GeneralSecurityException, IOException {
        int nbLocation = 0;
        
        if(isInternetAvailable()) {
            QueryResult queryResult = null;
            List<EventToSync> events = new ArrayList<>();

            try {
                queryResult = Requete.executeQuery("SELECT * FROM AjoutAgenda");

                while (queryResult.getResultSet().next()) {
                    int id = Integer.parseInt(queryResult.getResultSet().getString("id"));
                    String agenda = queryResult.getResultSet().getString("Agenda");
                    String titre = queryResult.getResultSet().getString("titre");
                    String description = queryResult.getResultSet().getString("description");
                    String date = queryResult.getResultSet().getString("date");

                    events.add(new EventToSync(id, agenda, titre, description, date));
                }
            } catch (Exception e) {
                Message.showErrorMessage("Erreur de la base de données", e.getMessage());
            } finally {
                if (queryResult != null)
                    queryResult.close();
            }

            for (EventToSync event : events) {
                boolean success = addEventToGoogleCalendar(event.agenda, event.titre, event.description, event.date);

                if (success) {
                    try {
                        Requete.executeUpdate("DELETE FROM AjoutAgenda WHERE id = " + event.id);
                        nbLocation += 1;
                    } catch (Exception e) {
                        Message.showErrorMessage("Erreur de la base de données", e.getMessage());
                    }
                }
            }
            if(nbLocation > 0) {
                Message.showValidMessage("Ajout de location", nbLocation + " ont été ajoutée(s) dans l'agenda.");
            }
        }
    }

    private static boolean isInternetAvailable() {
        try {
            InetAddress address = InetAddress.getByName("8.8.8.8");
            return address.isReachable(2000);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean addEventToGoogleCalendar(String agenda, String titre, String description, String date) throws GeneralSecurityException, IOException {
        GoogleCalendarService googleCalendarService = new GoogleCalendarService();
        try {
            googleCalendarService.ajouterEvenementPlageJours(
                    agenda, titre, description,
                    parseStringToLocalDate(date),
                    parseStringToLocalDate(date).plusDays(1)
            );
            return true;
        } catch (IOException e) {
            Message.showErrorMessage("Erreur lors de l'ajout de location", e.getMessage());
            return false;
        }
    }
}