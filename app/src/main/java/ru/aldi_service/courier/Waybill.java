package ru.aldi_service.courier;

import android.app.Activity;
import android.content.Intent;
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
    private String waybill, address, addressee, contactPerson, geography, phone, acceptedBy, info, comment, deliveryDate;
    private int id, deliveryListId, nItems, urgency, status;
    private float weight, costOfDelivery, addresseePayment, additionalPayment;

    public Waybill(Activity a, int i, String wn, String addrs, String addr, int urg, String dd) {
        waybill = wn;
        id = i;
        addressee = addrs;
        address = addr;
        urgency = urg;
        deliveryDate = dd;
        act = a;
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

    String getDeliveryDate() {
        return deliveryDate;
    }

    void setDeliveryDate(String dd) {
        deliveryDate = dd;
    }
}
