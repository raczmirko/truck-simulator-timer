package hu.okrim.trucksimulatortimer;

public class DataEntryModel {
    private int estimatedTimeSeconds;
    private int actualTimeSeconds;
    private int estimatedActualDifference;
    private String dateString;

    public DataEntryModel(int estimatedTimeSeconds, int actualTimeSeconds, String dateString) {
        this.estimatedTimeSeconds = estimatedTimeSeconds;
        this.actualTimeSeconds = actualTimeSeconds;
        this.estimatedActualDifference = estimatedTimeSeconds - actualTimeSeconds;
        this.dateString = dateString;
    }

    public int getEstimatedTimeSeconds() {
        return estimatedTimeSeconds;
    }

    public void setEstimatedTimeSeconds(int estimatedTimeSeconds) {
        this.estimatedTimeSeconds = estimatedTimeSeconds;
    }

    public int getActualTimeSeconds() {
        return actualTimeSeconds;
    }

    public void setActualTimeSeconds(int actualTimeSeconds) {
        this.actualTimeSeconds = actualTimeSeconds;
    }

    public int getEstimatedActualDifference() {
        return estimatedActualDifference;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }
}
