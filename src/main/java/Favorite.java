import java.util.LinkedList;
import java.util.List;

public class Favorite {
    private List<Location> locations;

    // constructor
    public Favorite() {
        this.locations = new LinkedList<Location>();
    }

    // getter
    public List<Location> getLocations() {
        return locations;
    }

    /**
     * obtain length of locations
     * @return length of locations
     */
    public int getLength() {
        return locations.size();
    }

    /**
     * obtain longitud of a certain location
     * @param index index of the List
     * @return longitud
     */
    public double getLatSearch(int index) {
        return locations.get(index).getCoordenadesLatitud();
    }

    /**
     * obtain latitud of a certain location
     * @param index index of the List
     * @return latitud
     */
    public double getLonSearch(int index) {
        return locations.get(index).getCoordenadesLongitud();
    }

    /**
     * insert a new location into the List
     * @param loc Location to add
     */
    public void insertFavorite(Location loc) {
        locations.add(loc);
    }

    /**
     * throws exception if the search historial doesn't exist
     * @throws NullPointerException if doesn't exist
     */
    public void isSearchFavorites() throws NullPointerException {
        if (locations == null || locations.isEmpty()) {
            throw new NullPointerException("Per tenir parades i estacions preferides es requereix haver creat una localització preferida anteriorment.");
        }
    }



}
