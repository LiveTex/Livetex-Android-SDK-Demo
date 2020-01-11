package nit.livetex.livetexsdktestapp.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import livetex.queue_service.Destination;
import nit.livetex.livetexsdktestapp.MainApplication;
import nit.livetex.livetexsdktestapp.R;
import nit.livetex.livetexsdktestapp.fragments.callbacks.ClientFormCallback;
import nit.livetex.livetexsdktestapp.fragments.presenters.ClientFormPresenter;
import nit.livetex.livetexsdktestapp.utils.DataKeeper;
import sdk.handler.AHandler;
import sdk.models.LTDepartment;
import sdk.models.LTEmployee;

/**
 * Created by user on 28.07.15.
 */
public class ChooseModeFragment extends BaseFragment implements ClientFormCallback, View.OnClickListener {

    private static final String TAG = "ChooseModeFragment";
    private Button btnOnlineMode;

    private ArrayList<Destination> destinations;

    @Override
    protected int getLayoutId() {
        return R.layout.choose_mode;
    }

    private void init(View v) {
        btnOnlineMode = v.findViewById(R.id.btnOnlineMode);
        btnOnlineMode.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnOnlineMode:
                if (TextUtils.isEmpty(DataKeeper.getClientName(getContext()))) {
                    showFragment(new ClientFormFragment(), true);
                } else {
                    showProgress();
                    if (destinations != null && !destinations.isEmpty()) {
                        Destination destination = destinations.get(0);
                        ClientFormPresenter.sendToDestination(getContext(), destination, DataKeeper.getClientName(getContext()), this);
                    }
                }
                break;
        }
    }

    @Override
    protected View onCreateView(View v) {
        init(v);
        btnOnlineMode.setBackground(getContext().getResources().getDrawable(R.drawable.gray_btn));

        MainApplication.getDestinations(new AHandler<ArrayList<Destination>>() {
            @Override
            public void onError(String errMsg) {
                Log.e(TAG, "Destinations: err " + errMsg);
            }

            @Override
            public void onResultRecieved(ArrayList<Destination> destinations) {
                if (destinations != null && destinations.size() != 0) {
                    ChooseModeFragment.this.destinations = destinations;
                    btnOnlineMode.setEnabled(true);
                    btnOnlineMode.setBackground(getContext().getResources().getDrawable(R.drawable.blue_btn));
                } else
                    Log.e(TAG, "Empty destinations!");
            }
        });

        return super.onCreateView(v);
    }

    @Override
    public void onEmployeesReceived(List<LTEmployee> operators) {

    }

    @Override
    public void onDepartmentsReceived(List<LTDepartment> departments) {

    }

    @Override
    public void onDestinationsReceived(List<Destination> destinations) {

    }

    @Override
    public void createChat(String conversationId, String avatar, String firstName) {

    }

    @Override
    public void createChat() {
        dismissProgress();
        Bundle bundle = new Bundle();
        showFragment(new OnlineChatFragment1(), bundle);
    }

    @Override
    public void onEmployeesEmpty() {

    }

    @Override
    public void onDepartmentsEmpty() {

    }
}