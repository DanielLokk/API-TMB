import java.io.IOException;
import java.util.Scanner;

public class Menu {

    public static final String ERROR_MENU_1 = "\nSiusplau selecciona una opcio del 1 al 5\n";
    public static final String ERROR_MENU_2 = "\nSiusplau selecciona una opcio de la (a) la (f)\n";

    String[] condition = {"1","2","3","4","5"};

    // constructor
    public          Menu() {
    }

    /**
     * prints first menu
     */
    public String   showMenu1() {
        return "1. Gestio d'usuari\n" +
                "2. Buscar localitzacions\n" +
                "3. Planejar Ruta\n" +
                "4. Temps d'espera del bus\n" +
                "5. Sortir\n" +
                "Selecciona una opció: ";
    }

    /**
     * prints second menu
     */
    public String   showMenu2() {

        return "a) Les meves localitzacions\n" +
                "b) Historial de localitzacions\n" +
                "c) Les meves rutes\n" +
                "d) Parades i estacions preferides\n" +
                "e) Estacions inaugurades el meu any de naixement\n" +
                "f) Tornar al menú principal\n" +
                "Selecciona opció: ";
    }

    /**
     * makes sure the user entered a valid value
     * @param question question asked to the user
     * @return proper answer
     */
    public String   isValid (String question, String[] conditions, String error) {
        Scanner sc = new Scanner(System.in);

        do {
            System.out.println(question);
            String ans = sc.nextLine();
            for (String condition : conditions) {
                if (ans.equalsIgnoreCase(condition)) {
                    return condition;
                }
            }
            System.out.println(error);
        } while (true);
    }

    /**
     * main loop of the program, the moment is exited, the program finishes
     * @param user Usuari
     * @param dm Data model
     */
    public void     mainLoop(Usuari user, DataModel dm) throws IOException{

        // when true -> exit of the app
        boolean ok = false;

        do {
            String option = isValid(showMenu1(), condition, Menu.ERROR_MENU_1);

            // checks if the answer is valid and shows menu
            switch (Integer.parseInt(option)) {

                // 1 - Gestió d'usuari
                case 1:
                    manageUsers(user, dm);
                    break;

                // 2 - Buscar localitzacions
                case 2:
                    searchLocation(user, dm);
                    break;

                // 3 - Planejar ruta
                case 3:
                    Planner planner = new Planner();
                    boolean next = false;

                    do {

                        try {

                            // ask for the parameters of the url
                            Location origin         = planner.validLocation(dm, "Origen? (lat,lon/nom localització)");
                            Location destination    = planner.validLocation(dm, "Destí? (lat,lon/nom localització)");
                            String ans              = planner.validAnswer();
                            String date             = planner.validDate();
                            String hour             = planner.validHour();
                            String walk             = planner.validWalk();

                            /* once we have all the parameters prints the route and saves it in the history */
                            user.getHistorial().insertRoute(planner.searchFastestRoute(origin, destination, ans, date, hour, walk));
                            next = true;
                        } catch (IOException e) {
                            System.out.println(e.getMessage() +  "\n");
                        } catch (NoSuchFieldException b) {
                            System.out.println(b.getMessage() + "\n");
                            next = true;
                        }
                    } while (!next);
                    break;

                //4 - Temps d'espera del bus
                case 4:
                    Bus bus = new Bus();
                    String stopCode = bus.askStopCode();
                    user.isInFavoriteCode(stopCode);
                    bus.iBus(stopCode);
                    break;

                //5 - Sortir
                default:
                    ok = true;
                    break;
            }
        } while (!ok);
    }

    /**
     * first option of the program
     */
    public static void manageUsers(Usuari user, DataModel dm) {
        Menu menu = new Menu();
        System.out.println();

        boolean next = false;

        do {
            // checks if the ans is correct
            String[] conditions = {"a", "b", "c", "d", "e", "f"};
            String valid = menu.isValid(menu.showMenu2(), conditions, Menu.ERROR_MENU_2);
            int option = valid.equals("a") ? 1
                    : valid.equals("b") ? 2
                    : valid.equals("c") ? 3
                    : valid.equals("d") ? 4
                    : valid.equals("e") ? 5
                    : 6;

            switch (option) {

                // (a) - Les meves localitzacions
                case 1:
                    while (user.wantNewLocation()) {
                        user.addNewLocation(dm);
                    }
                    next = true;
                    System.out.println();
                    break;

                // (b) - Historial de localitzacions
                case 2:
                    user.getHistorial().showSearchHistorial();
                    break;

                // (c) - Les meves rutes
                case 3:
                    user.getHistorial().showRouteHistorial();
                    break;

                // (d) - Parades i estacions preferides
                case 4:
                    try {
                        user.favoriteStations();
                    } catch (NullPointerException | IOException e) {
                        System.out.println("\n" + e.getMessage() + "\n");
                    }
                    break;

                // (e) - Estacions inaugurades el meu any de naixement
                case 5:
                    try {
                        user.yearBornStations();
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                    break;

                default:
                    System.out.println();
                    next = true;
                    break;
            }
        } while (!next);
    }

    /**
     * second option of the program, searches for an existing location.
     * other functions:
     *      - adds to favorite
     *      - adds to history
     * @param user info of the user
     * @param dm   info of the json
     */
    public void searchLocation(Usuari user, DataModel dm) {
        Scanner sc = new Scanner(System.in);
        String name;

        System.out.println("Introdueix el nom d'una localització:");
        name = sc.nextLine();

        Location seekLocationUser     = user.nameIsPresent(name);
        Location seekLocationDataModel  = dm.nameIsPresent(name);

        // if it's not in the user locations
        if (seekLocationUser == null) {

            // if it's not in the data model either shows an error
            if (seekLocationDataModel == null) {
                System.out.println("\nHo sentim, no hi ha cap localització amb aquest nom.\n");
            } else {
                // if its in the data model shows information
                printInformationLocation(seekLocationDataModel);
                // asks if the user want to add to favorites
                if (wantNewFavorite()) {
                    // if yes, adds a new favorite
                    user.addNewFavorite(seekLocationDataModel);
                }
                // we add the found location to the history
                user.addToHistory(seekLocationDataModel);
            }
        } else {
            // if its in the user List shows information
            seekLocationUser.printInformationLocation();
            // asks if the user want to add to favorites
            if (wantNewFavorite()) {
                // if yes, adds a new favorite
                user.addNewFavorite(seekLocationUser);
            }
            // we add the found location to the history
            user.addToHistory(seekLocationUser);
        }
    }

    /**
     * asks the user if he wants to add to favorites a location
     * and controls the answer
     * @return true if the answer is yes
     */
    public boolean      wantNewFavorite() {
        Scanner sc = new Scanner(System.in);
        boolean ok = false;
        String ans = "";

        // check if the ans is correct
        do {
            System.out.println("Vols guardar la localització trobada com a preferida? (sí/no)");
            ans = sc.nextLine().toLowerCase();
            if(ans.equals("si") || ans.equals("no") || ans.equals("sí")) {
                ok = true;
            } else {
                System.out.println("Error! S'ha d'introduir \"sí\" o \"no\".");
            }
        }while(!ok);

        // if the ans is "si" we add to favorites
        return ans.equals("si") || ans.equals("sí");
    }

    /**
     * prints all the information of the location passed
     */
    public void     printInformationLocation(Location location) {
        // info we always print
        System.out.println("\nPosició: " + location.getCoordenadesLatitud() + ", " + location.getCoordenadesLongitud());
        System.out.println("Descripció: \n" + location.getDescription());

        // info we only print depending on the type of location
        if (location.getId() == Location.HOTEL) {

            // print the info in the terminal
            System.out.println("Estrelles: " + location.getStars());

        } else if (location.getId() == Location.MONUMENT) {

            // print the info in the terminal
            System.out.println("Arquitecte: " + location.getArchitect());
            System.out.println("Inauguració: " + location.getInauguration());

        } else if (location.getId() == Location.RESTAURANT) {

            // characteristics is saved as an array of strings
            String[] characteristics = location.getCharacteristics();

            // print the info on the terminal in a list structure
            System.out.println("Caracteristiques: ");
            for (String characteristic : characteristics) {
                System.out.println("\t- " + characteristic);
            }
        }
        System.out.println();
    }
}
