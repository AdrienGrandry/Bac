package principale;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Year;
import java.util.List;

public class GmailStats {

    private static int mailsNonLus = 0;
    private static int mailsRecus = 0;
    private static int mailsEnvoyes = 0;

    private final Gmail service;

    public GmailStats() throws IOException, GeneralSecurityException {
        this.service = createGmailService();
    }

    /**
     * Initialise et calcule les statistiques pour l’année courante.
     */
    public void fetchMailStats() throws IOException {
        int currentYear = Year.now().getValue();

        this.mailsNonLus = countMessages("is:unread in:inbox");
        this.mailsEnvoyes = countMessages("in:sent after:" + currentYear + "/01/01 before:" + (currentYear + 1) + "/01/01");
        this.mailsRecus = countMessages("after:" + currentYear + "/01/01 before:" + (currentYear + 1) + "/01/01 -in:sent");
    }

    /**
     * Crée le service Gmail avec authentification et timeouts.
     */
    private Gmail createGmailService() throws IOException, GeneralSecurityException {
        final int TIMEOUT = 5_000;
        var credential = GoogleAuthorizeUtil.authorize();

        return new Gmail.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                request -> {
                    credential.initialize(request);
                    request.setConnectTimeout(TIMEOUT);
                    request.setReadTimeout(TIMEOUT);
                })
                .setApplicationName("GestiBac")
                .build();
    }

    /**
     * Compte les messages Gmail correspondant à une requête.
     */
    private int countMessages(String query) throws IOException {
        int count = 0;
        String nextPageToken = null;

        do {
            Gmail.Users.Messages.List request = service.users().messages()
                    .list("me")
                    .setQ(query)
                    .setPageToken(nextPageToken);

            ListMessagesResponse response = request.execute();
            List<Message> messages = response.getMessages();

            if (messages != null) {
                count += messages.size();
            }

            nextPageToken = response.getNextPageToken();
        } while (nextPageToken != null);

        return count;
    }

    // --- Getters ---

    public static int getMailsNonLus() {
        return mailsNonLus;
    }

    public static int getMailsRecus() {
        return mailsRecus;
    }

    public static int getMailsEnvoyes() {
        return mailsEnvoyes;
    }
}