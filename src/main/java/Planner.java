import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

// api
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

public class Planner {

    public static final String APP_ID = "bbd8fee9";
    public static final String APP_KEY = "3a2ce125bfdbab6a42e30bce58c1e485";


    // constructor
    public              Planner() {

    }

    /**
     * checks if the location is correct and if it's on the data model
     * @param dm data model where the info is stored
     * @return returns the valid location
     */
    public Location     validLocation(DataModel dm, String msg) {
        String origin = "";
        Scanner sc = new Scanner(System.in);
        Location location = new Location();

        while (true) {
            System.out.println(msg);
            origin = sc.nextLine();
            location = dm.nameIsPresent(origin);
            // if its on the data model
            if (location != null) {
                return location;
            } else {

                // if it's not a name we look for coordinates
                location = dm.coordinatesArePresent(origin);
                if (location != null) {
                    return location;
                } else {
                    // if it's neither we throw an error message
                    System.out.println("\nHo sentim, aquesta localització no és vàlida :(");
                }
            }
        }
    }

    /**
     * searches the fastest route from origin to destination
     * @param origin location origin
     * @param destination location destiny
     * @param ans origin or destiny
     * @param date date to arriveBy
     * @param hour hour
     * @throws IOException error in case the parameters are wrong
     */
    public StringBuilder searchFastestRoute(Location origin, Location destination,
                                           String ans, String date, String hour,
                                           String walk) throws IOException, NoSuchFieldException {

        StringBuilder sb = new StringBuilder();
        OkHttpClient client = new OkHttpClient();
        String planner_url = "https://api.tmb.cat/v1/planner/plan?";

        sb.append(planner_url).append("app_id=").append(APP_ID).append("&app_key=").append(APP_KEY);
        sb.append("&fromPlace=").append(origin.getCoordenadesLongitud()).append(",").append(origin.getCoordenadesLatitud());
        sb.append("&toPlace=").append(destination.getCoordenadesLongitud()).append(",").append(destination.getCoordenadesLatitud());
        sb.append("&date=").append(date);
        sb.append("&time=").append(hour);
        if (ans.equals("s")) {
            sb.append("&arriveBy=false");
        } else {
            sb.append("&arriveBy=true");
        }
        sb.append("&mode=TRANSIT,WALK");
        sb.append("&maxWalkDistance=").append(walk);

        Request request = new Request.Builder()
                .url(sb.toString())
                .build();

        Response response = client.newCall(request).execute();

        // clean the string builder
        sb.setLength(0);

        // we search for problems
        String jsonData = null;
        if (response.message().equals("OK")) {
            jsonData = response.body().string();
        } else if (response.message().equals("Not Found")) {
            throw new NoSuchFieldException("\nTMB està fent tot el possible perquè el bus i el metro facin aquesta ruta en un futur.");
        } else {
            throw new IOException("\nError, hi ha algun paràmetre erroni :(");
        }

        // look for the right data
        JSONObject obj = new JSONObject(jsonData);

        JSONObject plan = obj.getJSONObject("plan");
        // array of itineraries
        JSONArray itineraries = plan.getJSONArray("itineraries");

        // start querying the route
        sb.append("Combinació més ràpida:\n");
        sb.append("\tTemps del trajecte: ").append(properMinuteFormat(convertToMinutes(itineraries.getJSONObject(0)
                .get("duration")
                .toString()))).append(" min\n");
        sb.append("\tOrigen\n");

        JSONArray legs = itineraries.getJSONObject(0).getJSONArray("legs");

        // loop to print the legs of the trip
        for (int i = 0; i < legs.length(); i++) {
            sb.append("\t|");
            String mode = legs.getJSONObject(i).get("mode").toString();
            // depending on the mode we query different things
            if (mode.equals("WALK")) {
                queryWalk(legs,i, sb);
            } else if (mode.equals("BUS")) {
                queryTransit(legs,i,sb, true);
            } else {
                queryTransit(legs,i,sb,false);
            }
        }
        sb.append("\t|\n\tDestí\n");
        System.out.println(sb.toString());

        // save info to keep later
        sb.append("¿");
        sb.append("\t- Origen: ").append(origin.getNom()).append("\n");
        sb.append("\t- Destí: ").append(destination.getNom()).append("\n");
        sb.append("\t- Dia de sortida: ").append(date).append(" a les ").append(hour).append("\n");
        sb.append("\t- Màxima distància caminant: ").append(walk).append("\n");

        return sb;
    }

    /**
     * prints the walking query
     * @param legs JSONArray where the data is stores
     * @param index index of the JSONArray
     */
    private void        queryWalk (JSONArray legs, int index, StringBuilder sb) {
        sb.append("\n\tcaminar " +
                properMinuteFormat(convertToMinutes(legs.getJSONObject(index)
                        .get("duration")
                        .toString())) + " min\n");
    }

    /**
     * prints the transit query
     * @param legs JSONArray where the data is stores
     * @param index index of the JSONArray
     */
    private void        queryTransit(JSONArray legs, int index, StringBuilder sb, boolean bus) {
        JSONObject from = legs.getJSONObject(index).getJSONObject("from");
        JSONObject to = legs.getJSONObject(index).getJSONObject("to");

        sb.append("\n\t" + legs.getJSONObject(index).get("route").toString()+ " ");

        // if its a bus we add the stop code
        if (bus) {
            sb.append(from.get("name").toString() + "(" + from.get("stopCode").toString() + ") -> ");
            sb.append(to.get("name").toString() + "(" +to.get("stopCode").toString() + ") ");
        } else {
            sb.append(from.get("name").toString() + " -> ");
            sb.append(to.get("name").toString() + " ");
        }

        sb.append(properMinuteFormat(convertToMinutes(legs.getJSONObject(index).get("duration").toString()))+ " min\n");
    }

    /**
     * rounds floats
     * @param value value to round
     * @return returns a string to be printed
     */
    private String      properMinuteFormat (float value) {
        return String.format("%.0f", value);
    }

    /**
     * all the time data in the json is in seconds, so we need to convert it
     * @param value value to convert
     * @return value
     */
    private float       convertToMinutes (String value) {
        return Float.parseFloat(value) / 60;
    }

    /**
     * checks if the answer is "s" or "a"
     * @return returns s or a
     */
    public String       validAnswer () {
        Scanner sc = new Scanner(System.in);
        String ans = "";

        while (true) {
            System.out.println("Dia/hora seran de sortida o d'arribada? (s/a)");
            ans = sc.nextLine();
            if (ans.equalsIgnoreCase("s") || ans.equalsIgnoreCase("a")) {
                return ans;
            } else {
                System.out.println("\nError! S'ha d'introduir \"s\" o \"a\"!");
            }
        }
    }

    /**
     * asks for the hour to the user
     * @return hour
     */
    public String       validHour() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Hora? (HH:MMam/HH:MMpm)");
        return sc.nextLine();
    }

    /**
     * checks if the date is valid
     * @return returns a valid date
     */
    public String       validDate() {
        Scanner sc = new Scanner(System.in);
        SimpleDateFormat sd = new SimpleDateFormat("MM-dd-yyyy");
        sd.setLenient(false);

        String dateUser = "";

        while (true) {
            System.out.println("Dia? (MM-DD-YYYY)");
            dateUser = sc.nextLine();
            try {
                Date javaDate = sd.parse(dateUser);
                return dateUser;
            } catch (ParseException e) {
                System.out.println(dateUser + " no és una data vàlida");
            }
        }
    }

    /**
     * asks the user for the max number of meters walking
     * @return returns the answer
     */
    public String       validWalk() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Màxima distància caminant en metres?");
        return sc.nextLine();
    }
}
