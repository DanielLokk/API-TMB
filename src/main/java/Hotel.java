public class Hotel extends Location {
    protected int stars;

    // constructor
    public      Hotel(String nom, double latitud, double longitud, String description, int stars, int id) {
        super(nom,latitud, longitud, description, HOTEL);
        this.stars = stars;
    }

    // getter
    public int  getStars() {
        return stars;
    }
}
