package nit.livetex.livetexsdktestapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import livetex.queue_service.Destination;
import nit.livetex.livetexsdktestapp.R;

public class DestinationsAdapter extends ArrayAdapter<DestinationsAdapter.DestinationWrapper> {
    public DestinationsAdapter(Context context) {
        super(context, R.layout.spinner_view, new ArrayList<DestinationsAdapter.DestinationWrapper>());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.spinner_view, parent, false);
        TextView tvDepartmentName = (TextView) v.findViewById(android.R.id.text1);

        tvDepartmentName.setText("Отдел " + getItem(position).getD().getTouchPoint().getTouchPointId());

        return v;
    }

    public void setData(List<DestinationsAdapter.DestinationWrapper> destinations) {
        addAll(destinations);
        notifyDataSetChanged();
    }


    public static class DestinationWrapper {
        private Destination d;

        public DestinationWrapper(Destination d) {
            this.d = d;
        }

        public Destination getD() {
            return d;
        }

        @Override
        public String toString() {

            if (d.isSetTouchPoint()) {
                return "Отдел " + d.getTouchPoint().getTouchPointId();
            } else {
                return "Department ";
            }

        }
    }


}
