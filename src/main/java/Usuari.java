import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;



public class Usuari {

    private String nom_usuari;
    private String correu;
    private int any_n;

    // Arrays of objects
    private ArrayList<Location> locations;
    private Favorite            favorites;
    private Historial           historial;
    private ArrayList<String>   favoriteCode;


    // constructors
    public              Usuari() {

        Scanner sc = new Scanner(System.in);
        locations = new ArrayList<Location>();
        favoriteCode = new ArrayList<String>();

        System.out.println("Benvingut a l'aplicació de TMBJson! Si us plau, introdueix les dades que se't demanen.\n");
        System.out.println("Nom d'usuari:");
        this.nom_usuari = sc.nextLine();
        System.out.println();
        System.out.println("Correu electrònic:");
        this.correu = sc.nextLine();
        System.out.println();
        System.out.println("Any de naixement:");
        this.any_n = sc.nextInt();
        System.out.println();
        System.out.println("La informació s'ha registrat amb èxit!\n");
    }

    // getters
    public Historial    getHistorial () {
        if (historial != null) {
            return this.historial;
        } else {
            this.historial = new Historial();
            return this.historial;
        }
    }

    /**
     * prints all the locations of the List,
     * if it's empty throws an error
     */
    public void         printLocations() {
        if (locations == null) {
            System.out.println("No tens cap localitzacio creada.");
        }else{
            for (Location location : locations) {
                System.out.println("- " + location.getNom());
            }
        }
    }

    /**
     * asks if the user wants to add a new location
     * @return true if he says yes and false i he says no
     */
    public boolean      wantNewLocation() {
        Scanner sc = new Scanner(System.in);
        boolean ok = false;
        String ans = "";

        // check if the ans is correct
        do {
            System.out.println("Vols crear una nova localització? (sí/no)");
            ans = sc.nextLine().toLowerCase();
            if(ans.equals("si") || ans.equals("no") || ans.equals("sí")) {
                ok = true;
            } else {
                System.out.println("Error! S'ha d'introduir \"sí\" o \"no\".");
            }
        }while(!ok);

        // if the ans is "si" we add a new location
        return ans.equals("si") || ans.equals("sí");
        // if not return false
    }

    /**
     * adds a new location to the List of locations of the USER
     * @param dm has the info of the json
     */
    public void         addNewLocation(DataModel dm) {

        Location location = new Location();

        // we save the info in the List
        locations.add(location.addNewLocation(dm, this));
        System.out.println("\nLa informació s'ha registrat amb èxit!\n");
        printLocations();
        System.out.println();
    }

    /**
     * sees if the info is user List of locations
     * @param item  value to look for
     * @return true if the item is in the List
     */
    public Location     nameIsPresent(String item) {

        // search item in the locations list
        for (Location l : locations) {

            // if the item is found
            if (l.getNom().toLowerCase().equals(item.toLowerCase())) {
                return l;
            }
        }

        // if didn't find
        return null;
    }

    /**
     * prints the information of the location passed on parameter
     * @param location location to read
     */
    public void         printInformationLocation(Location location) {
        // info we always print
        System.out.println("\nPosició: " + location.getCoordenadesLatitud() + ", " + location.getCoordenadesLongitud());
        System.out.println("Descripció: \n" + location.getDescription());
        System.out.println();
    }

    /**
     * prints the stations that are at less than 500 meters
     * @throws NullPointerException in case historial wasn't initialized before
     * @throws IOException in case the URL is bad requested
     */
    public void         favoriteStations() throws NullPointerException, IOException {

        Api api = new Api();
        // this will store all the near locations to sort them
        String nullException = "Per tenir parades i estacions preferides es requereix" +
                " haver creat una localització preferida anteriorment.";

        if (favorites != null) {
            favorites.isSearchFavorites();
        } else {
            throw new NullPointerException(nullException);
        }

        favoriteCode = api.nearStops(favorites, favoriteCode);
    }


    /**
     * adds a new favorite to the favorite List
     * @param location favorite to add
     */
    public void         addNewFavorite (Location location) {
        Scanner sc = new Scanner(System.in);
        boolean next = false;
        String ans = "";

        do {
            System.out.println();
            System.out.println("Tipus(casa/feina/estudis/oci/cultura):");
            ans = sc.nextLine();
            System.out.println();

            // if the answer is one of these (ignoring case)
            if(ans.equalsIgnoreCase("casa") || ans.equalsIgnoreCase("feina")
                || ans.equalsIgnoreCase("estudis") || ans.equalsIgnoreCase("oci")
                || ans.equalsIgnoreCase("cultura")) {

                // we can get out of the loop
                next = true;

                // if favorites is not initialized
                if (favorites == null) {
                    favorites = new Favorite();
                }

                // add new favorite
                favorites.insertFavorite(location);
                System.out.println(location.getNom() + " s'ha assignat com a una nova localització preferida.");
                System.out.println();

            } else {
                System.out.println("Error! S'ha d'introduir \"casa\", \"feina\", \"estudis\", \"oci\" o \"cultura\".");
            }

        }while(!next);

    }

    /**
     * inserts new location in the List
     * @param location object to save
     */
    public void         addToHistory (Location location) {

        // in case is the first time we insert we create a new List
        if (historial == null) {
            this.historial = new Historial();
        }

        // insert a new location to the history
        historial.insertLocation(location);
    }

    /**
     * prints the subway stations created the year of birth of the user
     * @throws IOException in case the parameters are wrong
     */
    public void         yearBornStations() throws IOException {

        Api api = new Api();
        OkHttpClient client = new OkHttpClient();
        StringBuilder sb = new StringBuilder();
        boolean empty = false;
        sb.append("\nEstacions inaugurades el ").append(any_n).append(":\n");

        for (int j = 0; j < 8; j++) {

            j = j == 7 ? 94 : j;

            // we make a request for every existing line
            String subwayStops = "https://api.tmb.cat/v1/transit/linies/metro/" + j + "/estacions?" +
                    "app_id=" + Planner.APP_ID +
                    "&app_key=" + Planner.APP_KEY;

            Request request = new Request.Builder()
                    .url(subwayStops)
                    .build();

            Response response = client.newCall(request).execute();

            String subJson = null;
            if (response.message().equals("OK")) {
                subJson = response.body().string();
            } else {
                throw new IOException("Algún dels paràmetres estava malament :(");
            }

            // extract subway data
            JSONObject obj2 = new JSONObject(subJson);
            JSONArray features_subway = obj2.getJSONArray("features");

            // searches subway stations
            for (int i = 0; i < features_subway.length(); i++) {
                JSONObject properties = features_subway.getJSONObject(i).getJSONObject("properties");
                String any_n = String.valueOf(this.any_n);
                String[] any_json = properties.get("DATA_INAUGURACIO").toString().split("-");
                if (any_json[0].equals(any_n)) {
                    empty = true;
                    sb.append("\t- ").append(properties.get("NOM_ESTACIO")).append(" ").append(properties.get("PICTO")).append("\n");
                }
            }



        }

        // in case didn't find any
        if (!empty) {
            System.out.println("\nCap estació de metro es va inaugurar el teu any de naixement :(\n");
        } else {
            System.out.println(sb.toString());
        }
    }

    /**
     * prints message if the stop code is in the favorite list
     * @param stopCode code of the bus stop
     */
    public void         isInFavoriteCode(String stopCode) {
        if(favoriteCode.contains(stopCode)) {
            System.out.print("Parada preferida!");
        }
    }

}
