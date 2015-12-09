package ru.aldi_service.courier;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by alx on 02.11.15.
 * 03.11.15 add employee name
 * 10.11.15 add tables
 */
public class DBHelper extends SQLiteOpenHelper {
    final String LOG_TAG = "SQLite";

    public DBHelper(Context context) {
        // конструктор суперкласса
        super(context, "cargo_mobile", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, "--- onCreate database ---");

        db.execSQL("create table devices ("
                        + "employee_id integer primary key,"
                        + "login text,"
                        + "password text,"
                        + "employee text"
                        + ");"
        );
        db.execSQL("create table delivery_lists("
                        + "id integer primary key,"
                        + "employee_id integer,"
                        + "user_id integer,"
                        + "list_number text,"
                        + "datetime text"
                        + ");"
        );
        db.execSQL("create table deliveries("
                        + "id integer primary key,"
                        + "delivery_list_id integer,"
                        + "waybill text,"
                        + "weight real,"
                        + "n_items integer,"
                        + "addressee text,"
                        + "contact_person text,"
                        + "geography text,"
                        + "address text,"
                        + "phone text,"
                        + "cost_of_delivery real,"
                        + "addressee_payment real,"
                        + "additional_payment real,"
                        + "accepted_by text,"
                        + "sign blob,"
                        + "info text,"
                        + "urgency integer,"
                        + "comment text,"
                        + "delivery_date text,"
                        + "status integer"
                        + ");"
        );
        db.execSQL("create table delivery_items("
                        + "id integer primary key,"
                        + "delivery_id integer,"
                        + "item_number text"
                        + ");"
        );
        db.execSQL("create table users("
                        + "id integer primary key,"
                        + "username text,"
                        + "code text"
                        + ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
