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

/**
 * Created by user on 14.08.15.
 */
public class DepartmentsAdapter extends ArrayAdapter<LTDepartment> {
    public DepartmentsAdapter(Context context) {
        super(context, R.layout.spinner_view, new ArrayList<LTDepartment>());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.spinner_view, parent, false);
        TextView tvDepartmentName = (TextView) v.findViewById(android.R.id.text1);
        tvDepartmentName.setText(getItem(position).getName());
        return v;
    }

    public void setData(List<LTDepartment> departments) {
        addAll(departments);
        notifyDataSetChanged();
    }
}
