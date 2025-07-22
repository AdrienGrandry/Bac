package ressources;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {

    /**
     * Vérifie si une connexion Internet est disponible en essayant de se connecter à l'API Google.
     * @return true si l'accès Internet est disponible, false sinon.
     */
    public static boolean hasInternetAccess() {
        try {
            URL url = new URL("https://www.google.com"); // Serveur fiable
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000); // 5 secondes de timeout
            connection.connect();
            int responseCode = connection.getResponseCode();

            // 200 = OK, 403 = Forbidden mais réseau disponible
            return responseCode == 200 || responseCode == 403;
        } catch (IOException e) {
            return false;
        }
    }
}
