import java.util.Scanner;

public class Location {
    private String name;
    private double[] coordinates;
    private String description;
    private int id;

    // constants
    public static final int HOTEL = 0;
    public static final int RESTAURANT = 1;
    public static final int MONUMENT = 2;
    public static final int ANOTHER = 3;

    // constructors
    public          Location() {
        this.coordinates = new double[2];
    }
    public          Location(String nom, double latitud, double longitud, String description, int id) {
        this.coordinates = new double[2];
        this.coordinates[1] = latitud;
        this.coordinates[0] = longitud;
        this.name = nom;
        this.description = description;
        this.id = id;
    }

    // getters heritages
    public int      getStars() {
        return getStars();
    }
    public String   getArchitect() {
        return getArchitect();
    }
    public int      getInauguration() {
        return getInauguration();
    }
    public String[] getCharacteristics () {
        return getCharacteristics();
    }

    // getters location
    public int      getId() {
        return id;
    }
    public String   getNom() {
        return name;
    }
    public double   getCoordenadesLongitud() {
        return coordinates[0];
    }
    public double   getCoordenadesLatitud() {
        return coordinates[1];
    }
    public String   getDescription() {
        return description;
    }

    // setters location
    public void     setCoordenadesLongitud(double lon) {
        this.coordinates[0] = lon;
    }
    public void     setCoordenadesLatitud(double lat) {
        this.coordinates[1] = lat;
    }
    public void     setDescription(String description) {
        this.description = description;
    }
    public void     setNom(String nom) {
        this.name = nom;
    }

    /**
     * adds a new location to the List of locations of the USER
     * @param dm has the info of the json
     */
    public Location   addNewLocation(DataModel dm, Usuari user) {
        Scanner sc = new Scanner(System.in);
        Location location = new Location();

        // boolean to continue when the answer is correct
        boolean ok = false;

        // asks name
        do {
            System.out.println("Nom de la localització:");
            name = sc.nextLine();

            // sees if the name is present in user and datamodel locations
            if (user.nameIsPresent(name) == null && dm.nameIsPresent(name) == null) {
                ok = true;
                // save the info
                location.setNom(name);
            } else {
                System.out.println("Error! Aquesta localització ja existeix.");
            }
        } while (!ok);

        // reset the value
        ok = false;

        // asks the "longitud"
        do {
            System.out.println("Longitud:");

            String lon = sc.nextLine();
            // checks if the ans is  only numbers
            if (lon.matches("\\d+")) {
                if (Double.parseDouble(lon) >= -180.00 && Double.parseDouble(lon) <= 180.00) {
                    ok = true;
                    coordinates[0] = Long.parseLong(lon);
                } else {
                    System.out.println("Error! Les coordenades son incorrectes.");
                }
            } else {
                System.out.println("Error! Les coordenades son incorrectes.");
            }
        } while (!ok);

        ok = false;

        // asks the "latitud"
        do {
            System.out.println("Latitud:");
            String lat = sc.nextLine();
            // checks if the ans is  only numbers
            if (lat.matches("\\d+")) {
                if (Double.parseDouble(lat) >= -180 && Double.parseDouble(lat) <= 180) {
                    ok = true;
                    coordinates[1] = Long.parseLong(lat);
                } else {
                    System.out.println("Error! Les coordenades son incorrectes.");
                }
            } else {
                System.out.println("Error! Les coordenades son incorrectes.");
            }
        } while (!ok);

        // asks the description (no need to check anything)
        System.out.println("Descripció:");
        description = sc.nextLine();
        location.setDescription(description);

        return this;
    }

    /**
     * prints the information of the location
     */
    public void printInformationLocation() {
        // info we always print
        System.out.println("\nPosició: " + coordinates[1] + ", " + coordinates[0]);
        System.out.println("Descripció: \n" + description);
        System.out.println();
    }
}
