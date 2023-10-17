package hu.okrim.trucksimulatortimer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseController extends SQLiteOpenHelper {

    public static final String COLUMN_ID = "COLUMN_ID";
    public static final String COLUMN_ESTIMATED_TIME_SECONDS = "COLUMN_ESTIMATED_TIME_SECONDS";
    public static final String COLUMN_ACTUAL_TIME_SECONDS = "COLUMN_ACTUAL_TIME_SECONDS";
    public static final String COLUMN_ESTIMATED_ACTUAL_DIFFERENCE = "COLUMN_ESTIMATED_ACTUAL_DIFFERENCE";
    public static final String COLUMN_DATE = "COLUMN_DATE";
    public static final String DELIVERIES_TABLE = "DELIVERIES_TABLE";

    public DatabaseController(@Nullable Context context) {
        super(context, "deliveries.db", null, 1);
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

    public void addDelivery(DataEntryModel dataEntryModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ESTIMATED_TIME_SECONDS, dataEntryModel.getEstimatedTimeSeconds());
        cv.put(COLUMN_ACTUAL_TIME_SECONDS, dataEntryModel.getActualTimeSeconds());
        cv.put(COLUMN_ESTIMATED_ACTUAL_DIFFERENCE, dataEntryModel.getEstimatedActualDifference());
        cv.put(COLUMN_DATE, dataEntryModel.getDateString());

        long insert = db.insert(DELIVERIES_TABLE, null, cv);
    }

    /*public List<DataEntryModel> getAllRecords(){
        List<DataEntryModel> returnList = new ArrayList<>();
        //Get data from the Database.
        String queryString = "SELECT * FROM " + RECORDS_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        //If first element was found, meaning there were results...
        if(cursor.moveToFirst()){
            //loop through cursor (result set) and create new customer objects, put them in returnList
            do{
                String dateString = cursor.getString(1);
                String puzzle = cursor.getString(2);
                int time = cursor.getInt(3);

                DataEntryModel newRecord = new DataEntryModel(dateString, puzzle, time);
                returnList.add(newRecord);

            }while(cursor.moveToNext());
        }

        //Close both cursor and connection.
        cursor.close();
        db.close();
        return returnList;
    }

    public List<DataEntryModel> getRecordsForPuzzle(String puzzleToCheck){
        List<DataEntryModel> returnList = new ArrayList<>();
        //Get data from the Database.
        String queryString = "SELECT * FROM " + RECORDS_TABLE + " WHERE COLUMN_PUZZLE = '" + puzzleToCheck + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        //If first element was found, meaning there were results...
        if(cursor.moveToFirst()){
            //loop through cursor (result set) and create new customer objects, put them in returnList
            do{
                String dateString = cursor.getString(1);
                String puzzle = cursor.getString(2);
                int time = cursor.getInt(3);

                DataEntryModel newCustomer = new DataEntryModel(dateString,puzzle,time);
                returnList.add(newCustomer);

            }while(cursor.moveToNext());
        }

        //close both cursor and connection
        cursor.close();
        db.close();
        return returnList;
    }

    public List<DataEntryModel> getTop3Solves(String puzzleToCheck){
        List<DataEntryModel> returnList = new ArrayList<>();
        //Get data from the Database.
        String queryString = "SELECT COLUMN_TIME, COUNT(COLUMN_TIME) AS times, MAX(COLUMN_PUZZLE), MAX(COLUMN_DATE) FROM " + RECORDS_TABLE + " WHERE COLUMN_PUZZLE LIKE(" + "'" + puzzleToCheck + "'" + ") GROUP BY COLUMN_TIME ORDER BY COLUMN_TIME ASC LIMIT 3";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        //If first element was found, meaning there were results...
        if(cursor.moveToFirst()){
            //loop through cursor (result set) and create new customer objects, put them in returnList
            do{
                int time = cursor.getInt(0);
                String puzzle = cursor.getString(2);
                String dateString = cursor.getString(3);

                DataEntryModel newCustomer = new DataEntryModel(dateString, puzzle, time);
                returnList.add(newCustomer);

            }while(cursor.moveToNext());
        }

        //close both cursor and connection
        cursor.close();
        db.close();
        return returnList;
    }

    public String getTotalSolves(){
        //Get data from the Database.
        String queryString = "SELECT COUNT(*) FROM " + RECORDS_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        String returnString = "";
        //If first element was found, meaning there were results...
        if(cursor.moveToFirst()){
            //loop through cursor (result set) and create new customer objects, put them in returnList
            do{
                returnString = String.valueOf(cursor.getInt(0));
            }while(cursor.moveToNext());
        }
        //close both cursor and connection
        cursor.close();
        db.close();
        return returnString;
    }

    //Gets the solve that has the earliest date
    public List<DataEntryModel> getFirstSolve(){
        List<DataEntryModel> returnList = new ArrayList<>();
        //Get data from the Database.
        String queryString = "SELECT * FROM RECORDS_TABLE WHERE COLUMN_DATE = (SELECT MIN(COLUMN_DATE) FROM RECORDS_TABLE)";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        //If first element was found, meaning there were results...
        if(cursor.moveToFirst()){
            //loop through cursor (result set) and create new customer objects, put them in returnList
            do{
                String dateString = cursor.getString(1);
                String puzzle = cursor.getString(2);
                int time = cursor.getInt(3);

                DataEntryModel newDataEntryModel = new DataEntryModel(dateString, puzzle, time);
                returnList.add(newDataEntryModel);

            }while(cursor.moveToNext());
        }

        //close both cursor and connection
        cursor.close();
        db.close();
        return returnList;
    }

    public String getDaysSinceFirstSolve(){
        //Get data from the Database.
        String queryString = "WITH X AS (SELECT SUBSTR(MIN(COLUMN_DATE), 1, 10) AS minDate FROM RECORDS_TABLE) SELECT julianday('now') - julianday(date(minDate)) FROM X";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        String returnString = "";
        //If first element was found, meaning there were results...
        if(cursor.moveToFirst()){
            //loop through cursor (result set) and create new customer objects, put them in returnList
            do{
                returnString = String.valueOf(cursor.getInt(0));
            }while(cursor.moveToNext());
        }
        //close both cursor and connection
        cursor.close();
        db.close();
        return returnString;
    }

    public List<String> getFavouritePuzzle(){
        List<String> returnList = new ArrayList<>();
        //Get data from the Database.
        String queryString = "SELECT COLUMN_PUZZLE, COUNT(1) AS TIMES FROM RECORDS_TABLE GROUP BY COLUMN_PUZZLE ORDER BY TIMES DESC LIMIT 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        //If first element was found, meaning there were results...
        if(cursor.moveToFirst()){
            //loop through cursor (result set) and create new customer objects, put them in returnList
            do{
                String puzzle = cursor.getString(0);
                String times = cursor.getString(1);

                returnList.add(puzzle);
                returnList.add(times);
            }while(cursor.moveToNext());
        }
        //close both cursor and connection
        cursor.close();
        db.close();
        return returnList;
    }

    public String getDistinctDaysOfUse(){
        //Get data from the Database.
        String queryString = "SELECT COUNT(DISTINCT SUBSTR(COLUMN_DATE,1,10)) FROM RECORDS_TABLE";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        String returnString = "";
        //If first element was found, meaning there were results...
        if(cursor.moveToFirst()){
            //loop through cursor (result set) and create new customer objects, put them in returnList
            do{
                returnString = String.valueOf(cursor.getInt(0));
            }while(cursor.moveToNext());
        }
        //close both cursor and connection
        cursor.close();
        db.close();
        return returnString;
    }

    public List<String> getStatistics(String puzzle){
        *//*
        WITH X AS (
             SELECT * FROM RECORDS_TABLE WHERE COLUMN_PUZZLE LIKE ('%2x2x2%')),
             Y_PB AS (
             SELECT MIN(COLUMN_TIME) as pb FROM X),
             Y_TOTAL AS (
             SELECT COUNT(1) AS solves FROM X),
             Y_Ao5_HELP AS (
             SELECT COLUMN_TIME FROM X ORDER BY COLUMN_DATE DESC LIMIT 5),
             Y_Ao5 AS (
             SELECT CAST(AVG(COLUMN_TIME) AS INTEGER) AS ao5 FROM Y_Ao5_HELP WHERE COLUMN_TIME NOT IN (SELECT MIN(COLUMN_TIME) FROM Y_Ao5_HELP UNION SELECT MAX(COLUMN_TIME) FROM Y_Ao5_HELP)),
             Y_Ao12_HELP AS (
             SELECT COLUMN_TIME FROM X ORDER BY COLUMN_DATE DESC LIMIT 12),
             Y_Ao12 AS (
             SELECT CAST(AVG(COLUMN_TIME) AS INTEGER) AS ao12 FROM Y_Ao12_HELP WHERE COLUMN_TIME NOT IN (SELECT MIN(COLUMN_TIME) FROM Y_Ao12_HELP UNION SELECT MAX(COLUMN_TIME) FROM Y_Ao12_HELP)),
             Y_FIRST_Ao5_HELP AS(
             SELECT COLUMN_TIME FROM X ORDER BY COLUMN_DATE ASC LIMIT 5),
             Y_FIRST_Ao5 AS(
             SELECT CAST(AVG(COLUMN_TIME) AS INTEGER) AS first_ao5 FROM Y_FIRST_Ao5_HELP WHERE COLUMN_TIME NOT IN (SELECT MIN(COLUMN_TIME) FROM Y_FIRST_Ao5_HELP UNION SELECT MAX(COLUMN_TIME) FROM Y_FIRST_Ao5_HELP)),
             Y_Ao5_DIFF AS (
             SELECT first_ao5 - ao5 as diff FROM Y_Ao5, Y_FIRST_Ao5)
         SELECT pb, solves, ao5, ao12, diff
         FROM Y_PB, Y_TOTAL, Y_Ao5, Y_Ao12, Y_Ao5_DIFF
         *//*

        List<String> returnList = new ArrayList<>();
        //Get data from the Database.
        String queryString = "WITH X AS (\n" +
                "             SELECT * FROM RECORDS_TABLE WHERE COLUMN_PUZZLE LIKE ('%" + puzzle+ "%')),\n" +
                "             Y_PB AS (\n" +
                "             SELECT MIN(COLUMN_TIME) as pb FROM X),\n" +
                "             Y_TOTAL AS (\n" +
                "             SELECT COUNT(1) AS solves FROM X),\n" +
                "             Y_Ao5_HELP AS (\n" +
                "             SELECT COLUMN_TIME FROM X ORDER BY COLUMN_DATE DESC LIMIT 5),\n" +
                "             Y_Ao5 AS (\n" +
                "             SELECT CAST(AVG(COLUMN_TIME) AS INTEGER) AS ao5 FROM Y_Ao5_HELP WHERE COLUMN_TIME NOT IN (SELECT MIN(COLUMN_TIME) FROM Y_Ao5_HELP UNION SELECT MAX(COLUMN_TIME) FROM Y_Ao5_HELP)),\n" +
                "             Y_Ao12_HELP AS (\n" +
                "             SELECT COLUMN_TIME FROM X ORDER BY COLUMN_DATE DESC LIMIT 12),\n" +
                "             Y_Ao12 AS (\n" +
                "             SELECT CAST(AVG(COLUMN_TIME) AS INTEGER) AS ao12 FROM Y_Ao12_HELP WHERE COLUMN_TIME NOT IN (SELECT MIN(COLUMN_TIME) FROM Y_Ao12_HELP UNION SELECT MAX(COLUMN_TIME) FROM Y_Ao12_HELP)),\n" +
                "             Y_FIRST_Ao5_HELP AS(\n" +
                "             SELECT COLUMN_TIME FROM X ORDER BY COLUMN_DATE ASC LIMIT 5),\n" +
                "             Y_FIRST_Ao5 AS(\n" +
                "             SELECT CAST(AVG(COLUMN_TIME) AS INTEGER) AS first_ao5 FROM Y_FIRST_Ao5_HELP WHERE COLUMN_TIME NOT IN (SELECT MIN(COLUMN_TIME) FROM Y_FIRST_Ao5_HELP UNION SELECT MAX(COLUMN_TIME) FROM Y_FIRST_Ao5_HELP)),\n" +
                "             Y_Ao5_DIFF AS (\n" +
                "             SELECT first_ao5 - ao5 as diff FROM Y_Ao5, Y_FIRST_Ao5)\n" +
                "         SELECT pb, solves, ao5, ao12, diff\n" +
                "         FROM Y_PB, Y_TOTAL, Y_Ao5, Y_Ao12, Y_Ao5_DIFF";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        //If first element was found, meaning there were results...
        if(cursor.moveToFirst()){
            //loop through cursor (result set) and create new customer objects, put them in returnList
            do{
                String pb = cursor.getString(0);
                String totalSolves = cursor.getString(1);
                String ao5 = cursor.getString(2);
                String ao12 = cursor.getString(3);
                String ao5diff = cursor.getString(4);

                returnList.add(pb);
                returnList.add(totalSolves);
                returnList.add(ao5);
                returnList.add(ao12);
                returnList.add(ao5diff);

            }while(cursor.moveToNext());
        }

        //close both cursor and connection
        cursor.close();
        db.close();
        return returnList;
    }*/
}

