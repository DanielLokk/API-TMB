public class NearStop {
    private String symbol;
    private String stop_name;
    private double distance;
    private String mode;

    // constructor
    public NearStop(String symbol, String stop_name, double distance, String mode) {
        this.symbol = symbol;
        this.stop_name = stop_name;
        this.distance = distance;
        this.mode = mode;
    }

    // getters
    public double getDistance() {
        return distance;
    }
    public String getSymbol() {
        return symbol;
    }
    public String getStop_name() {
        return stop_name;
    }
    public String getMode() {
        return mode;
    }
}
