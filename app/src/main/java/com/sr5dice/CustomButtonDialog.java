package com.sr5dice;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

/**
 * Created by nbp184 on 2016/04/12.
 */
public class CustomButtonDialog extends DialogFragment {

    public static final String ARG_VALUE = "custom button value";

    private OnSaveListener listener = null;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof OnSaveListener) {
            listener = (OnSaveListener)activity;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_custom_button, null);
        EditText et = (EditText)view.findViewById(R.id.edit_custom);
        et.setText(""+getArguments().getInt(ARG_VALUE));
        builder.setView(view)
                .setTitle(R.string.custom_roll)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onSaveClick();
                    }
                })
                .setNegativeButton("Cancel", null);

        return builder.create();
    }

    private void onSaveClick() {
        if(listener != null) {
            EditText et = (EditText)getDialog().findViewById(R.id.edit_custom);
            Bundle data = new Bundle();
            data.putInt(ARG_VALUE, Integer.parseInt(et.getText().toString()));
            listener.onDialogClick(getTag(), data);
        }
    }

    public interface OnSaveListener {
        void onDialogClick(String tag, Bundle data);
    }

}
