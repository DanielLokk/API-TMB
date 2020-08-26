public class Restaurant extends Location {
    protected String[] characteristics;

    // constructor
    public Restaurant(String nom, double latitud, double longitud, String description, String[] characteristics, int id) {
        super(nom,latitud, longitud, description, RESTAURANT);
        this.characteristics = characteristics;
    }

    // getter of characteristics array
    public String[] getCharacteristics() {
        return characteristics;
    }
}
