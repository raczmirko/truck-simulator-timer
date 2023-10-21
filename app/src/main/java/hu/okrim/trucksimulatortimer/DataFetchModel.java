package hu.okrim.trucksimulatortimer;

public class DataFetchModel {

    private int id;
    private int estimatedTimeSeconds;
    private int actualTimeSeconds;
    private int estimatedActualDifference;
    private double differencePercentageOfTotalTime;
    private String dateString;

    public DataFetchModel(int id, int estimatedTimeSeconds, int actualTimeSeconds, int estimatedActualDifference, double differencePercentageOfTotalTime, String dateString) {
        this.id = id;
        this.estimatedTimeSeconds = estimatedTimeSeconds;
        this.actualTimeSeconds = actualTimeSeconds;
        this.estimatedActualDifference = estimatedActualDifference;
        this.differencePercentageOfTotalTime = differencePercentageOfTotalTime;
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

    public double getDifferencePercentageOfTotalTime() {
        return differencePercentageOfTotalTime;
    }

    public void setDifferencePercentageOfTotalTime(double differencePercentageOfTotalTime) {
        this.differencePercentageOfTotalTime = differencePercentageOfTotalTime;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEstimatedActualDifference(int estimatedActualDifference) {
        this.estimatedActualDifference = estimatedActualDifference;
    }
}
