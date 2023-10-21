package hu.okrim.trucksimulatortimer;

public class DataEntryModel {
    private int estimatedTimeSeconds;
    private int actualTimeSeconds;
    private final int estimatedActualDifference;
    private double differencePercentageOfTotalTime;
    private int ferryTimeSeconds;
    private String dateString;

    public DataEntryModel(int estimatedTimeSeconds, int actualTimeSeconds, String dateString, int ferryTimeSeconds) {
        this.estimatedTimeSeconds = estimatedTimeSeconds;
        this.actualTimeSeconds = actualTimeSeconds;
        this.estimatedActualDifference = estimatedTimeSeconds - actualTimeSeconds;
        this.differencePercentageOfTotalTime = calculateDifferencePercentage();
        this.dateString = dateString;
        this.ferryTimeSeconds = ferryTimeSeconds;
    }

    private double calculateDifferencePercentage() {
        if(actualTimeSeconds != 0 && estimatedTimeSeconds != 0){
            return (double)estimatedActualDifference / estimatedTimeSeconds;
        }
        else{
            return 0.0;
        }

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

    public int getFerryTimeSeconds() {
        return ferryTimeSeconds;
    }

    public void setFerryTimeSeconds(int ferryTimeSeconds) {
        this.ferryTimeSeconds = ferryTimeSeconds;
    }
}
