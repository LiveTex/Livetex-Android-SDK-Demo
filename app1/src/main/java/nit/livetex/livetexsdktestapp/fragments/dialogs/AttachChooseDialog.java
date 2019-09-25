package nit.livetex.livetexsdktestapp.fragments.dialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import nit.livetex.livetexsdktestapp.R;

public class AttachChooseDialog extends DialogFragment implements DialogInterface.OnClickListener {

    public static int TAKE_PICTURE_BY_CAM = 110;
    public static int TAKE_GALLERY_PICTURE = 111;
    public static int TAKE_FILE = 112;

    public static void show(Fragment fragmnet, FragmentManager fragmentManager) {
        AttachChooseDialog dialog = new AttachChooseDialog();
        dialog.show(fragmentManager, "AttachChooseDialog");
    }

    public void showAllowingStateLoss(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String[] strings;
        if (isCameraAvailable()) {
            strings = new String[]{
                    getString(R.string.take_photo),
                    getString(R.string.choose_from_gallery),
                    getString(R.string.send_file)
            };
        } else {
            strings = new String[]{
                    getString(R.string.choose_from_gallery),
                    getString(R.string.send_file)
            };
        }
        ListAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.item_list_dialog, strings);
        return new AlertDialog.Builder(getActivity()).setTitle(R.string.choose_file)
                .setSingleChoiceItems(adapter, 0, this).create();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if (!isCameraAvailable()) i++;
        switch (i){
            case 0:
                getTargetFragment().onActivityResult(getTargetRequestCode(), TAKE_PICTURE_BY_CAM, null);
                break;
            case 1:
                getTargetFragment().onActivityResult(getTargetRequestCode(), TAKE_GALLERY_PICTURE, null);
                break;
            case 2:
                getTargetFragment().onActivityResult(getTargetRequestCode(), TAKE_FILE, null);
                break;
        }
        dismiss();
    }

    private boolean isCameraAvailable(){
        return getActivity().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }
}
