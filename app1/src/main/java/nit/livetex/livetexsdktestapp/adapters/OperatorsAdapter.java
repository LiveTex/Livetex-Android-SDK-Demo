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
import sdk.models.LTEmployee;

/**
 * Created by user on 01.08.15.
 */
public class OperatorsAdapter extends ArrayAdapter<LTEmployee> {

    public OperatorsAdapter(Context context) {
        super(context, R.layout.spinner_view, new ArrayList<LTEmployee>());
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.spinner_view, parent, false);
        TextView tvOperatorName = (TextView) v.findViewById(android.R.id.text1);
        tvOperatorName.setText(getItem(position).firstname);
        return v;
    }

    public void setData(List<LTEmployee> employees) {
        addAll(employees);
        notifyDataSetChanged();
    }
}
