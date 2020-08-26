import java.util.Comparator;

public class SortByDistance implements Comparator<NearStop> {
    public int compare(NearStop n1, NearStop n2) {
        if (n1.getDistance() >= n2.getDistance()) {
            return 1;
        } else {
            return -1;
        }
    }
}
