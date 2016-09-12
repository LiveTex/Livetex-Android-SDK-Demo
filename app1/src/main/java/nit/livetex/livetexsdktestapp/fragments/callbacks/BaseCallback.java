package nit.livetex.livetexsdktestapp.fragments.callbacks;

import android.content.Context;

import nit.livetex.livetexsdktestapp.FragmentEnvironment;

/**
 * Created by user on 28.07.15.
 */
public interface BaseCallback {
    public Context getContext();
    public FragmentEnvironment getFragmentEnvironment();
}
