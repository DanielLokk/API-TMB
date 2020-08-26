import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {

        JsonReadFile jreader = new JsonReadFile();
        Menu menu = new Menu();
        DataModel dm = null;

        try {
            // read locations of the json if not throws IOException and exits the app
            dm = jreader.load();

            // asks for the necessary information
            Usuari user = new Usuari();

            menu.mainLoop(user, dm);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }



}
