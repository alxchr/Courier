package ru.aldi_service.courier;

/**
 * Created by alx on 06.12.15.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by alx on 06.12.15.
 */
public class WaybillAdapter extends BaseAdapter {
    Context c;
    LayoutInflater lInflater;
    ArrayList<Waybill> objects;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss");
    WaybillAdapter(Context context, ArrayList<Waybill> waybills) {
        c = context;
        objects = waybills;
        lInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.waybill_in_list, parent, false);
        }
        Waybill w = getWaybill(position);
        ((TextView) view.findViewById(R.id.number)).setText(w.getWaybill());
        ((TextView) view.findViewById(R.id.addressee)).setText(w.getAddressee());
        ((TextView) view.findViewById(R.id.address)).setText(w.getAddress());
        ((TextView) view.findViewById(R.id.urgency)).setText(c.getResources().getStringArray(R.array.urgencies)[w.getUrgency()] + " ");
        ((TextView) view.findViewById(R.id.deliveryDate)).setText(dateFormat.format(w.getDeliveryDate() * 1000L));
        view.setId(w.getId());
        view.setOnClickListener(Waybill.ocl);
        return view;
    }

    Waybill getWaybill(int p) {
        return ((Waybill) getItem(p));
    }
}
