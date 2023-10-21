package hu.okrim.trucksimulatortimer;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DatabaseController extends SQLiteOpenHelper {

    public static final String COLUMN_ID = "COLUMN_ID";
    public static final String COLUMN_ESTIMATED_TIME_SECONDS = "COLUMN_ESTIMATED_TIME_SECONDS";
    public static final String COLUMN_ACTUAL_TIME_SECONDS = "COLUMN_ACTUAL_TIME_SECONDS";
    public static final String COLUMN_ESTIMATED_ACTUAL_DIFFERENCE = "COLUMN_ESTIMATED_ACTUAL_DIFFERENCE";
    public static final String COLUMN_DIFFERENCE_PERCENTAGE_OF_TOTAL_TIME = "COLUMN_DIFFERENCE_PERCENTAGE_OF_TOTAL_TIME";
    public static final String COLUMN_FERRY_TIME = "COLUMN_FERRY_TIME";
    public static final String COLUMN_DATE = "COLUMN_DATE";
    public static final String DELIVERIES_TABLE = "DELIVERIES_TABLE";
    Context context;

    public DatabaseController(@Nullable Context context) {
        super(context, "deliveries.db", null, 1);
        this.context = context;
    }
    //This is called the first time a database is accessed.
    //Here we have code to create a new database.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + DELIVERIES_TABLE + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_ESTIMATED_TIME_SECONDS + " INTEGER," +
                COLUMN_ACTUAL_TIME_SECONDS + " INTEGER," +
                COLUMN_ESTIMATED_ACTUAL_DIFFERENCE + " INTEGER," +
                COLUMN_DIFFERENCE_PERCENTAGE_OF_TOTAL_TIME + " REAL," +
                COLUMN_FERRY_TIME + " INTEGER," +
                COLUMN_DATE + " TEXT" +
                ")";
        db.execSQL(createTableStatement);
    }
    //This is called if the database version is changed.
    //It prevents user apps from breaking when you change the database design.
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //Since we only run a local database this function is emitted...
    }

    public void wipeDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        String wipeStatement = "DELETE FROM " + DELIVERIES_TABLE;
        db.execSQL(wipeStatement);
    }

    public void addDelivery(DataEntryModel dataEntryModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ESTIMATED_TIME_SECONDS, dataEntryModel.getEstimatedTimeSeconds());
        cv.put(COLUMN_ACTUAL_TIME_SECONDS, dataEntryModel.getActualTimeSeconds());
        cv.put(COLUMN_ESTIMATED_ACTUAL_DIFFERENCE, dataEntryModel.getEstimatedActualDifference());
        cv.put(COLUMN_DATE, dataEntryModel.getDateString());
        cv.put(COLUMN_DIFFERENCE_PERCENTAGE_OF_TOTAL_TIME, dataEntryModel.getDifferencePercentageOfTotalTime());
        cv.put(COLUMN_FERRY_TIME, dataEntryModel.getFerryTimeSeconds());

        db.insert(DELIVERIES_TABLE, null, cv);
    }

    public void deleteDelivery(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteStatement = "DELETE FROM " + DELIVERIES_TABLE + " WHERE " + COLUMN_ID + " = " + id;
        db.execSQL(deleteStatement);
    }


    public List<DataFetchModel> getAllRecords(){
        List<DataFetchModel> returnList= new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectStatement = "SELECT * FROM " + DELIVERIES_TABLE;
        Cursor cursor = db.rawQuery(selectStatement, null);

        if(cursor.moveToFirst()){
            //loop through cursor (result set) and create new customer objects, put them in returnList
            do{
                DataFetchModel dataFetchModel = new DataFetchModel(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getInt(2),
                        cursor.getInt(3),
                        cursor.getDouble(4),
                       cursor.getString(6)
                );
                returnList.add(dataFetchModel);
            }while(cursor.moveToNext());
        }
        //Close both cursor and connection.
        cursor.close();
        db.close();

        return returnList;
    }

    public double calculateExtraSecondsByPastDeliveryTimes(int seconds, int ferrySeconds){
        double result = 0;
        double boundRadius = getSampleBound();
        double lowerBound = 1 - boundRadius;
        double upperBound = 1 + boundRadius;
        //Get data from the Database.
        String queryString = String.format(Locale.US, "SELECT AVG(COLUMN_DIFFERENCE_PERCENTAGE_OF_TOTAL_TIME) FROM %s " +
                                           "WHERE COLUMN_ESTIMATED_TIME_SECONDS >= %f AND " +
                                           "COLUMN_ESTIMATED_TIME_SECONDS <= %f AND COLUMN_FERRY_TIME >= %f AND " +
                                           "COLUMN_FERRY_TIME <= %f",
                                      DELIVERIES_TABLE, seconds * lowerBound, seconds * upperBound,
                                            ferrySeconds * lowerBound, ferrySeconds * upperBound);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        //If first element was found, meaning there were results...
        if(cursor.moveToFirst()){
            do{
                result = cursor.getDouble(0);
            }while(cursor.moveToNext());
        }
        //Close both cursor and connection.
        cursor.close();
        db.close();
        return result;
    }

    public double calculateOverallEstimationPrecision(){
        double result = 1.00;
        String queryString = "SELECT AVG(COLUMN_DIFFERENCE_PERCENTAGE_OF_TOTAL_TIME) FROM " + DELIVERIES_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        if(cursor.moveToFirst()){
            do{
                result = cursor.getDouble(0);
            }while(cursor.moveToNext());
        }
        //Close both cursor and connection.
        cursor.close();
        db.close();
        return result;
    }

    public double calculateOverallEstimationPrecisionByMedian(){
        double result = 0.00;
        int numberOfRecords = 0;
        String queryStringCountRows = "SELECT COUNT(COLUMN_ID) FROM " + DELIVERIES_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryStringCountRows, null);
        if(cursor.moveToFirst()){
            do{
                numberOfRecords = cursor.getInt(0);
            }while(cursor.moveToNext());
        }
        String queryStringMedian;
        if(numberOfRecords % 2 == 1){
            queryStringMedian = "SELECT " + COLUMN_DIFFERENCE_PERCENTAGE_OF_TOTAL_TIME + "\n" +
                    "FROM (SELECT " + COLUMN_DIFFERENCE_PERCENTAGE_OF_TOTAL_TIME + "\n" +
                    "FROM " + DELIVERIES_TABLE + "\n" +
                    "ORDER BY " + COLUMN_DIFFERENCE_PERCENTAGE_OF_TOTAL_TIME + "\n" +
                    "LIMIT 1\n" +
                    "OFFSET (SELECT (COUNT(*) - 1) / 2\n" +
                    "FROM " + DELIVERIES_TABLE + "))";
        }
        else {
            queryStringMedian = "SELECT " + COLUMN_DIFFERENCE_PERCENTAGE_OF_TOTAL_TIME + "\n" +
                    "FROM " + DELIVERIES_TABLE + "\n" +
                    "ORDER BY " + COLUMN_DIFFERENCE_PERCENTAGE_OF_TOTAL_TIME + "\n" +
                    "LIMIT 1 OFFSET (SELECT COUNT(*) / 2 FROM " + DELIVERIES_TABLE + ")";
        }
        cursor = db.rawQuery(queryStringMedian, null);
        if(cursor.moveToFirst()){
            do{
                result = cursor.getFloat(0);
            }while(cursor.moveToNext());
        }
        //Close both cursor and connection.
        cursor.close();
        db.close();
        return result;
    }

    public int countRecords(){
        int result = 0;
        String queryString = "SELECT COUNT(COLUMN_ID) FROM " + DELIVERIES_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        if(cursor.moveToFirst()){
            do{
                result = cursor.getInt(0);
            }while(cursor.moveToNext());
        }
        //Close both cursor and connection.
        cursor.close();
        db.close();
        return result;
    }

    public int countDaysOfUse(){
        int result = 0;
        String queryString = "SELECT COUNT(DISTINCT SUBSTR(COLUMN_DATE, 1, 10)) FROM " + DELIVERIES_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        if(cursor.moveToFirst()){
            do{
                result = cursor.getInt(0);
            }while(cursor.moveToNext());
        }
        //Close both cursor and connection.
        cursor.close();
        db.close();
        return result;
    }

    public double getSampleBound(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        return sharedPreferences.getFloat("samplingStrategyValue", 0.0f);
    }

    public int getAverageDriveTimePerDayInSeconds(){
        int result = 0;
        String queryString =
            "WITH X AS(" +
                "SELECT SUM(COLUMN_ACTUAL_TIME_SECONDS) AS total_seconds, " +
                "SUBSTR(COLUMN_DATE, 1, 10) as date " +
                "FROM " + DELIVERIES_TABLE + " " +
                "GROUP BY SUBSTR(COLUMN_DATE, 1, 10)) " +
            "SELECT AVG(total_seconds) FROM X";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        if(cursor.moveToFirst()){
            do{
                result = cursor.getInt(0);
            }while(cursor.moveToNext());
        }
        //Close both cursor and connection.
        cursor.close();
        db.close();
        return result;
    }

    public String getMedianOfDriveDate(){
        String result = "";
        int numberOfRecords = 0;
        String queryStringCountRows = "SELECT COUNT(COLUMN_ID) FROM " + DELIVERIES_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryStringCountRows, null);
        if(cursor.moveToFirst()){
            do{
                numberOfRecords = cursor.getInt(0);
            }while(cursor.moveToNext());
        }
        String queryStringMedian;
        if(numberOfRecords % 2 == 0){
            queryStringMedian = "SELECT SUBSTR(" + COLUMN_DATE + " , 12, 19)\n" +
                                "FROM (SELECT " + COLUMN_DATE + "\n" +
                                "FROM " + DELIVERIES_TABLE + "\n" +
                                "ORDER BY SUBSTR(" + COLUMN_DATE + " , 12, 19)\n" +
                                "LIMIT 2\n" +
                                "OFFSET (SELECT (COUNT(*) - 1) / 2\n" +
                                "FROM " + DELIVERIES_TABLE + "))";
        }
        else {
            queryStringMedian = "SELECT SUBSTR(" + COLUMN_DATE + " , 12, 19)\n" +
                                "FROM " + DELIVERIES_TABLE + "\n" +
                                "ORDER BY " + COLUMN_DATE + "\n" +
                                "LIMIT 1 OFFSET (SELECT COUNT(*) / 2 FROM " + DELIVERIES_TABLE + ")";
        }
        cursor = db.rawQuery(queryStringMedian, null);
        if(cursor.moveToFirst()){
            do{
                result = cursor.getString(0);
            }while(cursor.moveToNext());
        }
        //Close both cursor and connection.
        cursor.close();
        db.close();
        return result;
    }

    public int getTotalDriveTime(){
        int result = 0;
        String queryString = "SELECT SUM(COLUMN_ACTUAL_TIME_SECONDS) FROM " + DELIVERIES_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        if(cursor.moveToFirst()){
            do{
                result = cursor.getInt(0);
            }while(cursor.moveToNext());
        }
        //Close both cursor and connection.
        cursor.close();
        db.close();
        return result;
    }
}
