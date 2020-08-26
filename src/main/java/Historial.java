import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Historial {
    private List<Location> searchHistorial;
    private List<StringBuilder> routeHistorial;

    // constructor
    public      Historial() {
        this.searchHistorial = new LinkedList<Location>();
        this.routeHistorial  = new LinkedList<StringBuilder>();
    }

    /**
     * shortcut to add a new route
     * @param sb stringbuilder containing info to add
     */
    public void insertRoute(StringBuilder sb) {
        if (this.routeHistorial == null) {
            this.routeHistorial = new LinkedList<StringBuilder>();
            this.routeHistorial.add(sb);
        } else {
            this.routeHistorial.add(sb);
        }
    }

    /**
     * prints the history of searched routes in the proper format
     */
    public void showRouteHistorial () {
        if (!routeHistorial.isEmpty()) {
            for (int i = 1;i < routeHistorial.size()+1; i++) {
                String[] query = routeHistorial.get(i-1).toString().split("¿");
                System.out.println("\n->Ruta "+ i +": ");
                System.out.print(query[1]);
                System.out.println(query[0]);
                System.out.println("-------------------------------------------------");
            }
            System.out.println();
        } else {
            System.out.println("\nEncara no has buscat cap ruta :(");
            System.out.println("Per buscar-ne una, accedeix a l'opció 3 del menú principal.\n");
        }
    }

    /**
     * shortcut to insert a location into the List
     * @param location object to add
     */
    public void insertLocation(Location location) {
        searchHistorial.add(location);
    }

    /**
     * prints the list backwards
     */
    public void showSearchHistorial () {

        if (!searchHistorial.isEmpty()) {
            System.out.println("\nLocalitzacions buscades: ");
            for (int i = searchHistorial.size() - 1; i >= 0; i--) {
                System.out.println("\t- " + searchHistorial.get(i).getNom());
            }
            System.out.println();
        } else {
            System.out.println("\nEncara no has buscat cap localització!");
            System.out.println("Per buscar-ne una, accedeix a l'opció 2 del menú principal.\n");
        }
    }
}
