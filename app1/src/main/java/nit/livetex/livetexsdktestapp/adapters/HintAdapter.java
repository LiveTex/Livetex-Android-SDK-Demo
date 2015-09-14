package nit.livetex.livetexsdktestapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import nit.livetex.livetexsdktestapp.R;
import sdk.models.LTDepartment;
import sdk.models.LTEmployee;

/**
 * Created by user on 27.07.15.
 */
public class HintAdapter<T extends Object> extends ArrayAdapter<T> {

    private List<T> models = new ArrayList<>();

    public HintAdapter(Context context, List<T> models) {
        super(context, android.R.layout.simple_spinner_dropdown_item);
        this.models = models;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = LayoutInflater.from(getContext()).inflate(R.layout.spinner_view, parent, false);
        ((TextView) v.findViewById(android.R.id.text1)).setText(getText(position));
        return v;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View v = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        ((TextView) v.findViewById(android.R.id.text1)).setText(getText(position));
        return v;
    }

    public void setModels(List<T> models) {
        this.models = models;
        notifyDataSetChanged();
    }

    private String getText(int pos) {
        Object obj = models.get(pos);
        if (obj instanceof LTEmployee) {
            return getEmployeName((LTEmployee) obj);
        } else {
            return ((LTDepartment) obj).getName();
        }
    }

    private String getEmployeName(LTEmployee e) {
        return e.getFirstname() + " " + e.getLastname();
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public T getItem(int position) {
        return models.get(position);
    }
}
