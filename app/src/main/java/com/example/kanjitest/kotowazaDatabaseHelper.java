package com.example.kanjitest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class kotowazaDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "kotowazaData.db";

    private final String DB_PATH;

    private SQLiteDatabase myDatabase;

    private final Context myContext;

    public kotowazaDatabaseHelper(Context context){
        super(context, DB_NAME, null, 1);
        this.myContext = context;
        this.DB_PATH = this.myContext.getDatabasePath(DB_NAME).getParent() + "/";
    }

    public void createDatabase() throws IOException{
        boolean dbExist = checkDatabase();
        if (!dbExist){
            try{
                copyDatabase();
            }catch (IOException e){
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDatabase(){
        SQLiteDatabase checkDB = null;
        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB =  SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){
            //database doesn't exist yet.
        }
        if (checkDB != null){
            checkDB.close();
        }
        return checkDB != null;
    }

    private void copyDatabase() throws IOException{
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0){
            myOutput.write(buffer,0,length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }
    public void openDatabase() throws SQLiteException{
        String myPath = DB_PATH + DB_NAME;
        myDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close(){
        if (myDatabase != null)
            myDatabase.close();
        super.close();
    }
    @Override
    public void onCreate(SQLiteDatabase db){
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){}


}
