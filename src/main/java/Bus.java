import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Bus {

    public static final String ERROR_STOP_CODE = "Error, codi de parada no vàlid!";

    // constructor
    public          Bus() {
    }

    /**
     * asks the user for the code of the bus stop
     * @return returns the stop code
     */
    public String   askStopCode() {
        Scanner sc = new Scanner(System.in);
        try {
            do {
                System.out.println("Introdueix el codi de parada:");
                String stopCode = sc.nextLine();
                System.out.println();
                if (stopCodeExists(stopCode)) {
                    return stopCode;
                } else {
                    System.out.println(ERROR_STOP_CODE);
                }
            } while (true);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * tells if the stopCode is a valid stop code
     * @param stopCode code of the bus stop
     * @return true if found, false if not found
     * @throws IOException in case the request fails
     */
    private boolean stopCodeExists(String stopCode) throws IOException {
        Api api = new Api();

        String jsonBus = api.getJsonApi(Api.PAR_BUS_STOPS);

        JSONObject obj = new JSONObject(jsonBus);
        JSONArray  features_bus = obj.getJSONArray("features");

        // searches bus stops
        for (int i = 0; i < features_bus.length(); i++) {
            JSONObject properties = features_bus.getJSONObject(i).getJSONObject("properties");
            if (stopCode.equals(properties.get("CODI_PARADA").toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * prints the time left of each bus to arrive to the stop
     * @param stopCode code of the stop to look in the iBUS
     * @throws IOException in case the request fails
     */
    public void     iBus(String stopCode) throws IOException {

        StringBuilder sb = new StringBuilder();
        OkHttpClient client = new OkHttpClient();
        List<MinuteBus> list_mb = new LinkedList<>();

        String busStops = "https://api.tmb.cat/v1/ibus/stops/" + stopCode + "?" +
                "app_id=" + Planner.APP_ID +
                "&app_key=" + Planner.APP_KEY;
        Request request = new Request.Builder()
                .url(busStops)
                .build();

        Response response = client.newCall(request).execute();

        String jsonBus = null;
        if (response.message().equals("OK")) {
            jsonBus = response.body().string();
        } else {
            throw new IOException("Algún dels paràmetres estava incorrecte :(");
        }

        JSONObject  obj  = new JSONObject(jsonBus);
        JSONObject  data = obj.getJSONObject("data");
        JSONArray   ibus = data.getJSONArray("ibus");

        // searches bus stops
        for (int i = 0; i < ibus.length(); i++) {
            JSONObject current_bus = ibus.getJSONObject(i);

            String line = current_bus.get("line").toString();
            int minutes_left = Integer.parseInt(current_bus.get("t-in-min").toString());

            // we need to sort them
            list_mb.add(new MinuteBus(line,minutes_left));
        }
        // we sort using sortByMinute
        list_mb.sort(new SortByMinute());
        // print the result
        for (MinuteBus minuteBus : list_mb) {
            System.out.println(minuteBus.getLine()
                    + " - "
                    + returnNameEndOfLine(minuteBus.getLine())
                    + " - "
                    + minuteBus.getMin()
                    + " min");
        }
        System.out.println();
    }

    /**
     * gets the end of the line name
     * @param line line to see the end
     * @return name of the end of line
     */
    public String   returnNameEndOfLine (String line) {
        Api api = new Api();

        try {

            String lineJson = api.getJsonApi(Api.PAR_BUS_LINE);

            JSONObject obj = new JSONObject(lineJson);
            JSONArray  line_features = obj.getJSONArray("features");

            // searches bus stops
            for (int i = 0; i < line_features.length(); i++) {
                JSONObject properties = line_features.getJSONObject(i).getJSONObject("properties");
                if (line.equals(properties.get("NOM_LINIA").toString())) {
                    return properties.get("DESTI_LINIA").toString();
                }
            }
            // in case the destination is not found
            throw new NoSuchFieldException("La linea no hi és???");
        } catch (IOException | NoSuchFieldException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }


}