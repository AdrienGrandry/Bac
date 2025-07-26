import principale.MainFrame;
import java.util.Locale;

public class MainApplication {
    public static void main(String[] args)
    {
        Locale.setDefault(Locale.FRENCH);

        MainFrame mainFrame = new MainFrame();

        mainFrame.setVisible(true);
    }
}
