public class Monument extends Location {
    protected String architect;
    protected int inauguration;

    // constructor
    public          Monument(String nom, double latitud, double longitud, String description, String arch, int inauguration, int id) {
        super(nom,latitud, longitud, description, MONUMENT);
        this.architect = arch;
        this.inauguration = inauguration;
    }

    // getters
    public String   getArchitect() {
        return architect;
    }
    public int      getInauguration() {
        return inauguration;
    }
}
