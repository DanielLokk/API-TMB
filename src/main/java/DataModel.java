import java.util.*;

public class DataModel {
    private List<Location> locations;

    // Constructor
    public          DataModel() {
        List<Location> locations = new LinkedList<Location>();
    }

    // Setter
    public void     setLocations(List<Location> locations) {
        this.locations = locations;
    }

    /**
     * insert new location to the list
     * @param loc object added to the list
     */
    public void     insertLocation(Location loc) {
        this.locations.add(loc);
    }

    /**
     *  checks if the name passed is in the List, and
     *  if print is true, prints the info of that item
     * @param  item  value to find in the List
     * @return null if the item is not found, and if the location is found it returns the Location
     */
    public Location nameIsPresent(String item) {

        // search through the user locations array
        for (Location location : locations) {

            // if the item is found
            if (location.getNom().toLowerCase().equals(item.toLowerCase())) {
                // once the item is found we don't need to look more
                return location;
            }
        }

        // item not found returns null
        return null;
    }



    /**
     * gets coordinates ans sees if they are in the list, if the format
     * isn't correct or the coordinates aren't on the list returns null
     * @param coordinates coordinates with format "<lat>,<lon>"
     * @return null or the location found
     */
    public Location coordinatesArePresent (String coordinates) {

        String[] latAndLong;

        if (coordinates.contains(",")) {
            latAndLong = coordinates.split(",");
        } else {
            return null;
        }
        Location locationToReturn = null;

        // convert to double because for some reason it deletes the 0 in the end
        double latitud = Double.parseDouble(latAndLong[0]);
        double longitud = Double.parseDouble(latAndLong[1]);

        // search through the user locations array
        for (Location location : locations) {
            String lat = Double.toString(location.getCoordenadesLatitud());
            String lon = Double.toString(location.getCoordenadesLongitud());
            // if the item is found
            if (lat.equals(Double.toString(latitud))) {

                if (lon.equals(Double.toString(longitud))) {
                    // once the item is found we don't need to look more
                    locationToReturn = location;
                }
            }
        }

        // search through the user locations array
        for (Location location : locations) {
            String lon = Double.toString(location.getCoordenadesLongitud());
            // if the item is found
            if (lon.equals(latAndLong[1])) {
                // once the item is found we don't need to look more
                locationToReturn = location;
            }
        }

        // item not found returns null
        return locationToReturn;
    }
}
