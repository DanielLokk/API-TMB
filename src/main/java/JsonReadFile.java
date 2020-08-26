import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

public class JsonReadFile {

    /**
     * loads all the information from the .json to the dataList class
     * @return returns the info in the object DataModel
     */
    public DataModel load() throws IOException {

        //declaration of utilities to read
        StringBuilder sb = new StringBuilder();
        JsonReader reader;
        DataModel dm = new DataModel();


        // relative path to the json
        String PATH = "src/main/resources/localitzacions.json";
        reader = new JsonReader(new FileReader(PATH));              /* instance jsonReader */
        dm.setLocations(new LinkedList<>());
        String diferentiation = "";

        // start reading the json
        reader.beginObject();
        reader.nextName();                                          /* locations */
        reader.beginArray();                                        /* array of locations */

        do {

            // start a new Location
            reader.beginObject();

            // read the name
            reader.nextName();
            sb.append(reader.nextString());
            sb.append("#");

            // read the coordinates
            reader.nextName();
            reader.beginArray();

            // read the longitud
            sb.append(reader.nextDouble());
            sb.append("#");

            // read the latitud
            sb.append(reader.nextDouble());
            sb.append("#");

            // finish reading the coordinates
            reader.endArray();

            // read the description
            reader.nextName();
            sb.append(reader.nextString());
            sb.append("#");

            // enter if there is more info on the Location (hotel, monument or restaurant)
            if(reader.hasNext()) {

                // info of the next name saved
                diferentiation = reader.nextName();

                // MONUMENT
                if ("architect".equals(diferentiation)) {

                    // read the architect and the inauguration
                    sb.append(reader.nextString());
                    sb.append("#");
                    reader.nextName();
                    sb.append(reader.nextInt());

                    // fill the info for the new monument
                    String[] arrayMonument = sb.toString().split("#");
                    Location monument = new Monument(arrayMonument[0], Double.parseDouble(arrayMonument[1]),
                            Double.parseDouble(arrayMonument[2]), arrayMonument[3], arrayMonument[4],
                            Integer.parseInt(arrayMonument[5]), Location.MONUMENT);

                    // insert the monument to the List
                    dm.insertLocation(monument);

                // RESTAURANT
                } else if ("characteristics".equals(diferentiation)) {

                    // fill the info of the restaurant
                    String[] arrayRestaurant = sb.toString().split("#");
                    sb.setLength(0);

                    // read the array of characteristics
                    reader.beginArray();
                    do {
                        sb.append(reader.nextString());
                        sb.append(";");
                    } while (reader.hasNext());
                    reader.endArray();

                    // fill the array of characteristics
                    String[] arrayCharacteristics = sb.toString().split(";");
                    Location restaurant = new Restaurant(arrayRestaurant[0], Double.parseDouble(arrayRestaurant[1]),
                            Double.parseDouble(arrayRestaurant[2]), arrayRestaurant[3], arrayCharacteristics
                            , Location.RESTAURANT);

                    // insert the restaurant to the List
                    dm.insertLocation(restaurant);

                // HOTEL
                } else if ("stars".equals(diferentiation)) {

                    //read stars
                    sb.append(reader.nextInt());

                    //fill hotels information
                    String[] arrayHotels = sb.toString().split("#");
                    Location hotel = new Hotel(arrayHotels[0], Double.parseDouble(arrayHotels[1]),
                            Double.parseDouble(arrayHotels[2]), arrayHotels[3], Integer.parseInt(arrayHotels[4]),
                            Location.HOTEL);

                    // insert the hotel to the List
                    dm.insertLocation(hotel);
                }

                // NOT A HOTEL, RESTAURANT OR MONUMENT
            } else {

                String[] arrayLocation = sb.toString().split("#");
                Location location = new Location(arrayLocation[0], Double.parseDouble(arrayLocation[1]),
                        Double.parseDouble(arrayLocation[2]), arrayLocation[3], Location.ANOTHER);

                // insert the location to the List
                dm.insertLocation(location);

            }

            //end object and reset stringBuilder to get new data
            reader.endObject();
            sb.setLength(0);

        // until there is no next object
        }while(reader.hasNext());

        return dm;
    }
}
