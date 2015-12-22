package ru.aldi_service.courier;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;


/**
 * Created by alx on 29.11.15.
 * New interface 06.12.15
 */
public class Waybill {
    static Activity act;
    static public View.OnClickListener ocl = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int deliveryID = v.getId();
            String sID = String.valueOf(deliveryID);
            Log.d("Clicked", "View = " + sID);
            Intent intent = new Intent(act, WaybillActivity.class);
            intent.putExtra("delivery_id", sID);
            act.startActivityForResult(intent, 1);
        }
    };
    DBHelper dbHelper;
    ContentValues cv;
    private SQLiteDatabase db;
    private Cursor c1, c2;
    private String selection;
    private byte[] sign = {0};
    private String waybill, address, addressee, contactPerson, geography, phone, acceptedBy, info, comment;
    private int id, deliveryListId, nItems, urgency, status;
    private float weight, costOfDelivery, addresseePayment, additionalPayment;
    private long ddts;

    public Waybill(Activity a, int i, String wn, String addrs, String addr, int urg, long dd) {
        waybill = wn;
        id = i;
        addressee = addrs;
        address = addr;
        urgency = urg;
        ddts = dd;
        act = a;
        /*
        dbHelper = new DBHelper(act);
        db = dbHelper.getWritableDatabase();
        selection = "id = '" + String.valueOf(id) + "'";
        c1 = db.query("deliveries", null, selection, null, null, null, null);
        c1.moveToFirst();
        waybill = c1.getString(c1.getColumnIndex("waybill"));
        addressee = c1.getString(c1.getColumnIndex("addressee"));
        geography = c1.getString(c1.getColumnIndex("geography"));
        address = c1.getString(c1.getColumnIndex("address"));
        phone = c1.getString(c1.getColumnIndex("phone"));
        info = c1.getString(c1.getColumnIndex("info"));
        comment = c1.getString(c1.getColumnIndex("comment"));
        costOfDelivery = c1.getFloat(c1.getColumnIndex("cost_of_delivery"));
        addresseePayment = c1.getFloat(c1.getColumnIndex("addressee_payment"));
        additionalPayment = c1.getFloat(c1.getColumnIndex("additional_payment"));
        weight = c1.getFloat(c1.getColumnIndex("weight"));
        acceptedBy = c1.getString(c1.getColumnIndex("accepted_by"));
        contactPerson = c1.getString(c1.getColumnIndex("contact_person"));
        ddts = c1.getLong(c1.getColumnIndex("delivery_date"));
        status = c1.getInt(c1.getColumnIndex("status"));
        nItems = c1.getInt(c1.getColumnIndex("n_items"));
        urgency = c1.getInt(c1.getColumnIndex("urgency"));
        deliveryListId = c1.getInt(c1.getColumnIndex("delivery_list_id"));
        sign = c1.getBlob(c1.getColumnIndex("sign"));
        */
    }

    void setContactPerson(String cp) {
        contactPerson = cp;
    }

    void setGeography(String g) {
        geography = g;
    }

    void setPhone(String ph) {
        phone = ph;
    }

    void setAcceptedBy(String ab) {
        acceptedBy = ab;
    }

    void setInfo(String i) {
        info = i;
    }

    void setComment(String c) {
        comment = c;
    }

    void setDeliveryListId(int dli) {
        deliveryListId = dli;
    }

    void setnItems(int ni) {
        nItems = ni;
    }

    void setStatus(int s) {
        status = s;
    }

    void setWeight(float w) {
        weight = w;
    }

    void setCostOfDelivery(float cod) {
        costOfDelivery = cod;
    }

    void setAddresseePayment(float ap) {
        addresseePayment = ap;
    }

    void setAdditionalPayment(float adp) {
        additionalPayment = adp;
    }

    String getWaybill() {
        return waybill;
    }

    void setWaybill(String w) {
        waybill = w;
    }

    String getAddressee() {
        return addressee;
    }

    void setAddressee(String ad) {
        addressee = ad;
    }

    String getAddress() {
        return address;
    }

    void setAddress(String a) {
        address = a;
    }

    int getId() {
        return id;
    }

    void setId(int i) {
        id = i;
    }

    int getUrgency() {
        return urgency;
    }

    void setUrgency(int u) {
        urgency = u;
    }

    long getDeliveryDate() {
        return ddts;
    }

    void setDeliveryDate(long dd) {
        ddts = dd;
    }

    void setWbSign(byte[] s) {
        sign = s;
    }
}
