package principale;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.List;

public class GmailStats {

    public static int[] getNbMail() throws IOException, GeneralSecurityException {
        Date dt = new Date();
        int year = dt.getYear();
        int current_Year = year + 1900;

        // Authentification
        var credential = GoogleAuthorizeUtil.authorize();

        // Création du service Gmail
        Gmail service = new Gmail.Builder(
                com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport(),
                com.google.api.client.json.jackson2.JacksonFactory.getDefaultInstance(),
                credential)
                .setApplicationName("GestiBac")
                .build();

        // Appel et affichage
        int unreadCount = countMessages(service, "me", "is:unread");
        int sentCount = countMessages(service, "me", "in:sent after:" + current_Year + "/01/01 before:"+ (current_Year+1) + "/01/01");
        int receivedCount = countMessages(service, "me", " after:" + current_Year + "/01/01 before:" + (current_Year+1) + "/01/01");

        int tab[] = {unreadCount, sentCount, receivedCount};

        return tab;
    }

    private static int countMessages(Gmail service, String userId, String query) throws IOException {
        int count = 0;
        String nextPageToken = null;

        do {
            Gmail.Users.Messages.List request = service.users().messages().list(userId)
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
}