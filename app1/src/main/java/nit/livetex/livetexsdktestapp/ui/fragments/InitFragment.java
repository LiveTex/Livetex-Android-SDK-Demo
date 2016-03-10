package nit.livetex.livetexsdktestapp.ui.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import nit.livetex.livetexsdktestapp.R;
import nit.livetex.livetexsdktestapp.models.BaseMessage;
import nit.livetex.livetexsdktestapp.models.ErrorMessage1;
import nit.livetex.livetexsdktestapp.models.EventMessage;
import nit.livetex.livetexsdktestapp.presenters.InitPresenter;
import nit.livetex.livetexsdktestapp.ui.callbacks.InitCallback;
import nit.livetex.livetexsdktestapp.utils.CommonUtils;
import com.squareup.otto.Subscribe;

/**
 * Created by user on 28.07.15.
 */
public class InitFragment extends BaseFragment implements InitCallback {

    private InitPresenter presenter;

    @Override
    protected View onCreateView(View v) {
        showProgress();
        presenter = new InitPresenter(this);
        presenter.init("106217");
        return super.onCreateView(v);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_init;
    }

    @Subscribe
    public void onEventMessage(EventMessage eventMessage) {
        if(eventMessage.getMessageType() == BaseMessage.TYPE.INIT) {
            onInitComplete(eventMessage.getStringExtra());
        }
    }

    public void onErrorMessage(ErrorMessage1 errorMessage1) {
        if(errorMessage1.getMessageType() == BaseMessage.TYPE.INIT) {

        }
    }

    @Override
    public void onInitComplete(String token) {
        dismissProgress();
        showFragment(new ChooseModeFragment());
    }

    @Override
    public void onClear() {
        CommonUtils.showToast(getContext(), "Cache is clear");
    }


}


















