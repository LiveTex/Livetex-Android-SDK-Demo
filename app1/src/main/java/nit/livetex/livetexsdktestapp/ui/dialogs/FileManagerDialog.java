package nit.livetex.livetexsdktestapp.ui.dialogs;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nit.livetex.livetexsdktestapp.utils.CommonUtils;

/**
 * Created by sergey.so on 08.05.2014.
 *
 */
public class FileManagerDialog extends android.support.v4.app.DialogFragment {

    public static final int TAKE_FILE_URI = 115;
    private String mCurrentPath = null;
    private List<File> mFiles = new ArrayList<File>();
    private List<File> mStartFiles = new ArrayList<File>();
    private LinearLayout mMainLayout;
    private boolean isRoot = true;

    public static void show(FragmentManager fragmentManager) {
        FileManagerDialog dialog = new FileManagerDialog();
        dialog.show(fragmentManager, "FileManagerDialog");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mStartFiles.addAll(getFiles(Environment.getExternalStorageDirectory()));
        mFiles.addAll(mStartFiles);
        mMainLayout = createMainLayout();
        ListView listView = createListView(getActivity());
        listView.setAdapter(new FileAdapter(getActivity(), mFiles));
        mMainLayout.addView(createBackItem(listView));
        mMainLayout.addView(listView);
        return new AlertDialog.Builder(getActivity())
                .setTitle(Environment.getExternalStorageDirectory().getPath())
                .setView(mMainLayout)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    private LinearLayout createMainLayout() {
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setMinimumHeight(getLinearLayoutMinHeight(getActivity()));
        return linearLayout;
    }

    private static int getLinearLayoutMinHeight(Activity activity) {
        return CommonUtils.getScreenSize(activity).y;
    }

    private TextView createTextView(Context context, int style) {
        TextView textView = new TextView(context);
        textView.setTextAppearance(context, style);
        int itemHeight = getItemHeight();
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight));
        textView.setMinHeight(itemHeight);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setPadding(15, 0, 0, 0);
        textView.setVisibility(View.GONE);
        return textView;
    }

    private int getItemHeight() {
        TypedValue value = new TypedValue();
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getTheme().resolveAttribute(android.R.attr.rowHeight, value, true);
        CommonUtils.getDefaultDisplay(getActivity()).getMetrics(metrics);
        return (int) TypedValue.complexToDimension(value.data, metrics);
    }

    private TextView createBackItem(final ListView listView) {
        TextView textView = createTextView(getActivity(), android.R.style.TextAppearance_Small);
        Drawable drawable = getResources().getDrawable(android.R.drawable.ic_menu_directions);
        drawable.setBounds(0, 0, 60, 60);
        textView.setCompoundDrawables(drawable, null, null, null);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (new File(mCurrentPath).getParent().equals(Environment.getExternalStorageDirectory().getPath())) {
                    isRoot = true;
                } else {
                    File file = new File(mCurrentPath);
                    File parentDirectory = file.getParentFile();
                    if (parentDirectory != null) {
                        isRoot = false;
                        mCurrentPath = parentDirectory.getPath();
                    }
                }
                refreshFiles(((FileAdapter) listView.getAdapter()));
            }
        });
        return textView;
    }

    private ListView createListView(Context context) {
        final ListView listView = new ListView(context);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                final ArrayAdapter<File> adapter = (FileAdapter) adapterView.getAdapter();
                File file = adapter.getItem(index);
                if (file.isDirectory()) {
                    mCurrentPath = file.getPath();
                    isRoot = false;
                    refreshFiles(adapter);
                } else {
                    Intent intent = new Intent();
                    intent.setData(Uri.fromFile(file));
                    getTargetFragment().onActivityResult(getTargetRequestCode(), TAKE_FILE_URI, intent);
                   // ((MessagesActivity) getActivity()).sendFile(Uri.fromFile(file));
                    dismiss();
                }
            }
        });
        return listView;
    }

    private void refreshFiles(ArrayAdapter<File> adapter) {
        try {
            if (isRoot){
                getDialog().setTitle(Environment.getExternalStorageDirectory().getPath());
                mFiles.clear();
                mFiles.addAll(mStartFiles);
                adapter.notifyDataSetChanged();
                mMainLayout.getChildAt(0).setVisibility(View.GONE);
            } else {
                getDialog().setTitle(mCurrentPath);
                mFiles.clear();
                mFiles.addAll(getFiles(mCurrentPath));
                adapter.notifyDataSetChanged();
                mMainLayout.getChildAt(0).setVisibility(View.VISIBLE);
            }
        } catch (NullPointerException e) {
            Toast.makeText(getActivity(), android.R.string.unknownName, Toast.LENGTH_SHORT).show();
        }
    }

    private List<File> getFiles(String directoryPath) {
        File directory = new File(directoryPath);
        return getFiles(directory);
    }

    private List<File> getFiles(File directory) {
        List<File> fileList = Arrays.asList(directory.listFiles());
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File file, File file2) {
                if (file.isDirectory() && file2.isFile())
                    return -1;
                else if (file.isFile() && file2.isDirectory())
                    return 1;
                else
                    return file.getPath().compareTo(file2.getPath());
            }
        });
        return fileList;
    }

    private static class FileAdapter extends ArrayAdapter<File> {

        public FileAdapter(Context context, List<File> files) {
            super(context, android.R.layout.simple_list_item_1, files);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            File file = getItem(position);
            view.setText(file.getName());
            return view;
        }
    }


}
