import principale.GoogleAuthorizeUtil;
import principale.MainFrame;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Locale;

public class MainApplication {
    public static void main(String[] args) throws GeneralSecurityException, IOException {
        GoogleAuthorizeUtil.authorize();
        Locale.setDefault(Locale.FRENCH);

        MainFrame mainFrame = new MainFrame();

        mainFrame.setVisible(true);
    }
}
