package ir.medxhub.doctor.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import ir.medxhub.doctor.Globals;
import ir.medxhub.doctor.about_us.MemberInfo;
import ir.medxhub.doctor.message.MessageItem;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static int DATABASE_VERSION = 1;
    private String DB_PATH;
    private SQLiteDatabase myDataBase;
    private final Context myContext;

    public DBHelper(Context context) {
        super(context, Globals.dbNAME, null, DATABASE_VERSION);
        this.myContext = context;
        this.DB_PATH = context.getFilesDir().getPath() + "/";
    }

    public void createDataBase() throws IOException {
        //By calling this method and empty database will be created into the default system path
        //of your application so we are gonna be able to overwrite that database with our database.
        this.getReadableDatabase();
        try {
            copyDataBase();
        } catch (IOException e) {
            Log.e("create_database", "----" + e.getMessage());
            throw new Error("Error copying database");
        }
    }

    public boolean checkDataBase() {
        SQLiteDatabase checkDB = null;

        try {
            String myPath = DB_PATH + Globals.dbNAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        } catch (SQLiteException e) {
            //database doesn't exist yet.
        }

        if (checkDB != null) {
            checkDB.close();
        }

        return checkDB != null;
    }

    private void copyDataBase() throws IOException {

        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(Globals.dbNAME);

        //create path if not exists and open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(DB_PATH + Globals.dbNAME);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDataBase() throws SQLException {
        //Open the database
        String myPath = DB_PATH + Globals.dbNAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        Log.i("database", "---- database opened");
    }

    public void closeDatabase() throws SQLException {
        this.close();
        Log.i("database", "---- database closed");
    }

    @Override
    public synchronized void close() {
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // This method is just implemented because we are extending abstract SQLiteOpenHelper
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This method is just implemented because we are extending abstract SQLiteOpenHelper
    }

    // rest of public helper methods

    public long saveMessage(MessageItem messageItem) {
        long id = 0;
        try {
            openDataBase();
            ContentValues values = new ContentValues();
            values.put("title", messageItem.getTitle());
            values.put("message", messageItem.getMessage());
            values.put("created_at", messageItem.getDate());

            id = myDataBase.insert("message", null, values);
            closeDatabase();
            Log.i("DatabaseInsertion: ", "---- Message saved to database");
        } catch (SQLException e) {
            Log.e("DatabaseInsertion: ", "----" + e.getMessage());
        }
        return id;
    }

    public MessageItem getMessage(int messageId) {
        MessageItem message = new MessageItem();
        try {
            openDataBase();
            String selectQuery = "SELECT  * FROM message WHERE id = " + messageId;

            Cursor cursor = myDataBase.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                message.setId(cursor.getInt(0));
                message.setTitle(cursor.getString(1));
                message.setMessage(cursor.getString(2));
                message.setDate(cursor.getString(3));
            }
            cursor.close();
            closeDatabase();
        } catch (Exception e) {
            Log.e("DatabaseInsertion", "----" + e.getMessage());
        }
        return message;
    }

    public List<MessageItem> getMessages() {
        List<MessageItem> messageItems = new ArrayList<MessageItem>();
        try {
            openDataBase();
            String selectQuery = "SELECT * FROM message";

            Cursor cursor = myDataBase.rawQuery(selectQuery, null);
            MessageItem message;
            if (cursor.moveToFirst()) {
                do {
                    message = new MessageItem();
                    message.setId(cursor.getInt(0));
                    message.setTitle(cursor.getString(1));
                    message.setMessage(cursor.getString(2));
                    message.setDate(cursor.getString(3));

                    messageItems.add(message);
                } while (cursor.moveToNext());
            }
            cursor.close();
            closeDatabase();
        } catch (Exception e) {
            Log.e("Get Messages", "----" + e.getMessage());
        }

        return messageItems;
    }

    public int deleteMessage(int messageId) {
        openDataBase();

        int result = myDataBase.delete("message", " id = ?", new String[]{String.valueOf(messageId)});
        Log.i("Message Delete", "---- Message delete_" + result);

        closeDatabase();
        return result;
    }

    public ArrayList<MemberInfo> getUserInfoes(boolean management) {
        ArrayList<MemberInfo> members = new ArrayList<MemberInfo>();
        MemberInfo member;
        openDataBase();
        try {
            String selectQuery = "SELECT * FROM members_info ORDER BY random()";

            Cursor cursor = myDataBase.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    member = new MemberInfo();
                    member.id = cursor.getInt(0);
                    member.fName = cursor.getString(1);
                    member.lName = cursor.getString(2);
                    member.about = cursor.getString(3);
                    member.mail = cursor.getString(4);
                    member.linkedIn = cursor.getString(5);
                    member.webLink = cursor.getString(6);
                    member.avatar = cursor.getString(7);
                    member.specialty = cursor.getString(8);

                    members.add(member);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("DatabaseFetch", "----" + e.getMessage());
        }
        closeDatabase();
        return members;
    }
}