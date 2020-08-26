import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Api {

    public static final String PAR_BUS_STOPS = "parades";
    public static final String PAR_SUB_STOPS = "estacions";
    public static final String PAR_BUS_LINE = "linies/bus";

    private static final int MAX_DISTANCE = 500;

    public Api() {
    }

    /**
     * returns the json for the easiest requests
     * @param parameters url
     * @return json from the api
     * @throws IOException case execute fails
     */
    public String getJsonApi (String parameters) throws IOException {

        OkHttpClient client = new OkHttpClient();

        String busStops = "https://api.tmb.cat/v1/transit/" + parameters + "?" +
                "app_id=" + Planner.APP_ID +
                "&app_key=" + Planner.APP_KEY;
        Request request = new Request.Builder()
                .url(busStops)
                .build();

        Response response = client.newCall(request).execute();

        String json = null;
        if (response.message().equals("OK")) {
            json = response.body().string();
        } else {
            throw new IOException("Algún dels parametres estava incorrecte :(");
        }
        return json;
    }


    public ArrayList<String> nearStops(Favorite favorites, ArrayList<String> favoriteCode) throws IOException {

        List<NearStop> list_ns = new LinkedList<>();

        // json info
        String jsonBus = getJsonApi(Api.PAR_BUS_STOPS);
        String jsonSub = getJsonApi(Api.PAR_SUB_STOPS);

        // extract bus data
        JSONObject obj = new JSONObject(jsonBus);
        JSONArray features_bus = obj.getJSONArray("features");

        // extract subway data
        JSONObject obj2 = new JSONObject(jsonSub);
        JSONArray  features_subway = obj2.getJSONArray("features");

        // loop to read all favorites
        for (int j = 0; j < favorites.getLength(); j++) {
            int index = 0;
            boolean empty = false;

            // prints the name of the current favorite
            System.out.println("- " + favorites.getLocations().get(j).getNom());

            // searches bus stops
            for (int i = 0; i < features_bus.length(); i++) {

                double distance = getDistance(features_bus,i,j, favorites);

                if (distance <= MAX_DISTANCE) {
                    empty = true;
                    index++;
                    JSONObject properties = features_bus.getJSONObject(i).getJSONObject("properties");
                    // we add the code to an array to check on the option 4 (iBus)
                    favoriteCode.add(properties.get("CODI_PARADA").toString());
                    String code_parada = properties.get("CODI_PARADA").toString();
                    String name_parada = properties.get("NOM_PARADA").toString();

                    list_ns.add(new NearStop(code_parada,name_parada,distance,"BUS"));
                }
            }

            // searches subway stations
            for (int i = 0; i < features_subway.length(); i++) {

                double distance = getDistance(features_subway,i,j, favorites);

                if (distance <= MAX_DISTANCE) {
                    empty = true;
                    index++;
                    JSONObject properties = features_subway.getJSONObject(i).getJSONObject("properties");
                    String code_parada = properties.get("PICTO").toString();
                    String name_parada = properties.get("NOM_ESTACIO").toString();

                    list_ns.add(new NearStop(code_parada,name_parada,distance,"SUB"));
                }
            }

            // in case there is no bus stops or stations
            if (!empty) {
                System.out.println("\tTMB està fent tot el possible perquè el bus i el metro arribin fins aquí.");
            } else {
                list_ns.sort(new SortByDistance());
                for (int i = 1; i < list_ns.size()+1; i++) {
                    System.out.println("\t"
                            + i
                            + ") "
                            + list_ns.get(i-1).getStop_name()
                            + " ("
                            + list_ns.get(i-1).getSymbol()
                            + ") "
                            + list_ns.get(i-1).getMode());
                }

                // Clean the list to not repeat in the next favorite
                list_ns.clear();
            }
            System.out.println();
        }
        return favoriteCode;
    }

    /**
     * gets the distance from two coordinates
     * @param array where the coordinates are stored
     * @param index_i from the array of the json
     * @param index_j from the array of the favorites
     * @return the distance in meters
     */
    private double      getDistance(JSONArray array, int index_i, int index_j, Favorite favorites) {
        JSONObject geometry = array.getJSONObject(index_i).getJSONObject("geometry");
        JSONArray coordinates = geometry.getJSONArray("coordinates");
        double lon = (double) coordinates.get(0);
        double lat = (double) coordinates.get(1);

        // does the harvesine formula to calculate the distance between two points
        return harvesineFormula(lon, favorites.getLatSearch(index_j), lat, favorites.getLonSearch(index_j));
    }

    /**
     * does the harvasine formula to calculate the distance between 2 coordinates
     * @param lat1 latitud 1
     * @param lat2 latitud 2
     * @param lon1 longitud 1
     * @param lon2 longitud 2
     * @return distance in meters
     */
    private double      harvesineFormula(double lat1, double lat2, double lon1, double lon2) {

        // radius of earth
        int R = 6371;

        double latDistance = Math.toRadians(lat2-lat1);
        double lonDistance = Math.toRadians(lon2-lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        // distance in meters
        return (R * c)*1000;
    }

}
