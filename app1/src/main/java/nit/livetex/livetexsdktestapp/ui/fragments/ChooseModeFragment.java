package nit.livetex.livetexsdktestapp.ui.fragments;

import android.view.View;
import android.widget.Button;

import nit.livetex.livetexsdktestapp.MainApplication;
import nit.livetex.livetexsdktestapp.R;
import nit.livetex.livetexsdktestapp.ui.fragments.offline.OfflineConversationListFragment;
import nit.livetex.livetexsdktestapp.ui.fragments.online.ClientFormFragment;

import java.util.ArrayList;

import sdk.handler.AHandler;
import sdk.models.LTDepartment;

/**
 * Created by user on 28.07.15.
 */
public class ChooseModeFragment extends BaseFragment implements View.OnClickListener {

    Button btnOfflineMode;
    Button btnOnlineMode;

    @Override
    protected int getLayoutId() {
        return R.layout.choose_mode;
    }

    private void init(View v) {
        btnOfflineMode = (Button) v.findViewById(R.id.btnOfflineMode);
        btnOnlineMode = (Button) v.findViewById(R.id.btnOnlineMode);
        btnOnlineMode.setOnClickListener(this);
        btnOfflineMode.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnOfflineMode) {
            showFragment(new OfflineConversationListFragment(), true);
        } else if(view.getId() == R.id.btnOnlineMode) {
            showFragment(new ClientFormFragment(), true);
        }
    }

    @Override
    protected View onCreateView(View v) {
        init(v);
        btnOnlineMode.setBackground(getContext().getResources().getDrawable(R.drawable.gray_btn));

        MainApplication.getDepartments("online", new AHandler<ArrayList<LTDepartment>>() {
            @Override
            public void onError(String errMsg) {

            }

            @Override
            public void onResultRecieved(ArrayList<LTDepartment> departments) {

                if(departments != null && departments.size() != 0) {
                    btnOnlineMode.setEnabled(true);
                    btnOnlineMode.setBackground(getContext().getResources().getDrawable(R.drawable.blue_btn));
                }
            }
        });

        return super.onCreateView(v);
    }

}





















