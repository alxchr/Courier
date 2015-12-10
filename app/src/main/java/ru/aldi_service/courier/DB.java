package ru.aldi_service.courier;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by alx on 30.11.15. New DB interface
 */
public class DB {
    public static final String COLUMN_EMPLOYEE_ID = "employee_id";
    public static final String COLUMN_LOGIN = "login";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_EMPLOYEE = "employee";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_LIST_NUMBER = "list_number";
    public static final String COLUMN_DATETIME = "datetime";
    public static final String COLUMN_DELIVERY_LIST_ID = "delivery_list_id";
    public static final String COLUMN_WAYBILL = "waybill";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_N_ITEMS = "n_items";
    public static final String COLUMN_ADDRESSEE = "addressee";
    public static final String COLUMN_CONTACT_PERSON = "contact_person";
    public static final String COLUMN_GEOGRAPHY = "geography";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_COD = "cost_of_delivery";
    public static final String COLUMN_ADDRESSEE_PAYMENT = "addressee_payment";
    public static final String COLUMN_ADDITIONAL_PAYMENT = "additional_payment";
    public static final String COLUMN_ACCEPTED_BY = "accepted_by";
    public static final String COLUMN_SIGN = "sign";
    public static final String COLUMN_INFO = "info";
    public static final String COLUMN_URGENCY = "urgency";
    public static final String COLUMN_COMMENT = "comment";
    public static final String COLUMN_DELIVERY_DATE = "delivery_date";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_DELIVERY_ID = "delivery_id";
    public static final String COLUMN_ITEM_NUMBER = "item_number";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_CODE = "code";
    private static final String DB_NAME = "cargo_mobile";
    private static final int DB_VERSION = 1;
    private static final String DB_DEVICES = "devices";
    private static final String DB_DELIVERY_LISTS = "delivery_lists";
    private static final String DB_DELIVERIES = "deliveries";
    private static final String DB_DELIVERY_ITEMS = "delivery_items";
    private static final String DB_USERS = "users";
    private static final String DB_CREATE_DEVICES =
            "create table " + DB_DEVICES + "(" +
                    COLUMN_EMPLOYEE_ID + " integer primary key, " +
                    COLUMN_LOGIN + " text," +
                    COLUMN_PASSWORD + " text," +
                    COLUMN_EMPLOYEE + " text" +
                    ");";
    private static final String DB_CREATE_DELIVERY_LISTS =
            "create table " + DB_DELIVERY_LISTS + "(" +
                    COLUMN_ID + " integer primary key, " +
                    COLUMN_EMPLOYEE_ID + " integer, " +
                    COLUMN_USER_ID + " integer, " +
                    COLUMN_LIST_NUMBER + " text," +
                    COLUMN_DATETIME + " text" +
                    ");";
    private static final String DB_CREATE_DELIVERIES =
            "create table " + DB_DELIVERIES + "(" +
                    COLUMN_ID + " integer primary key, " +
                    COLUMN_DELIVERY_LIST_ID + " integer, " +
                    COLUMN_WAYBILL + " text," +
                    COLUMN_WEIGHT + " real," +
                    COLUMN_N_ITEMS + " integer, " +
                    COLUMN_ADDRESSEE + " text," +
                    COLUMN_CONTACT_PERSON + " text," +
                    COLUMN_GEOGRAPHY + " text," +
                    COLUMN_ADDRESS + " text," +
                    COLUMN_PHONE + " text," +
                    COLUMN_COD + " text," +
                    COLUMN_ADDRESSEE_PAYMENT + " text," +
                    COLUMN_ADDITIONAL_PAYMENT + " text," +
                    COLUMN_ACCEPTED_BY + " text," +
                    COLUMN_SIGN + " blob," +
                    COLUMN_INFO + " text," +
                    COLUMN_URGENCY + " integer," +
                    COLUMN_COMMENT + " text," +
                    COLUMN_DELIVERY_DATE + " text," +
                    COLUMN_STATUS + " integer " +
                    ");";
    private static final String DB_CREATE_DELIVERY_ITEMS =
            "create table " + DB_DELIVERY_ITEMS + "(" +
                    COLUMN_ID + " integer primary key, " +
                    COLUMN_DELIVERY_ID + " integer, " +
                    COLUMN_ITEM_NUMBER + " text" +
                    ");";
    private static final String DB_CREATE_USERS =
            "create table " + DB_USERS + "(" +
                    COLUMN_ID + " integer primary key, " +
                    COLUMN_USERNAME + " text," +
                    COLUMN_CODE + " text" +
                    ");";
    private final Context mCtx;
    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    public DB(Context ctx) {
        mCtx = ctx;
    }

    // открыть подключение
    public void openDB() {
        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    // закрыть подключение
    public void closeDB() {
        if (mDBHelper != null) mDBHelper.close();
    }

    public boolean checkPasswordDB(String employeeName, String password) {
        openDB();
        String[] selectionArgs = new String[]{employeeName};
        String selection = "login = ?";
        Cursor c = mDB.query("devices", null, selection, selectionArgs, null, null, null);
        if (c != null && c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {
                    if (password.equals(c.getString(c.getColumnIndex(COLUMN_PASSWORD)))) {
                        closeDB();
                        return true;
                    }
                } while (c.moveToNext());
            }
        }
        closeDB();
        return false;
    }

    // получить все данные из таблицы DB_TABLE
    public Cursor getAllData() {
        return mDB.query(DB_DELIVERIES, null, null, null, null, null, null);
    }

    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                        int version) {
            super(context, name, factory, version);
        }

        // создаем БД
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE_DEVICES);
            db.execSQL(DB_CREATE_DELIVERY_LISTS);
            db.execSQL(DB_CREATE_DELIVERIES);
            db.execSQL(DB_CREATE_DELIVERY_ITEMS);
            db.execSQL(DB_CREATE_USERS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

    /**
     * A basic Camera preview class
     */

}
