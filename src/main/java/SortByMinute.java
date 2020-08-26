import java.util.Comparator;

public class SortByMinute implements Comparator<MinuteBus> {

    /**
     * compares two minutes and sorts them
     * @param n1 minute 1
     * @param n2 minute 2
     * @return order
     */
    public int compare(MinuteBus n1, MinuteBus n2) {
        if (n1.getMin() >= n2.getMin()) {
            return 1;
        } else {
            return -1;
        }
    }
}