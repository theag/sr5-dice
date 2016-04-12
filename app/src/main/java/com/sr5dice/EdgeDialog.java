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
import android.widget.NumberPicker;

/**
 * Created by nbp184 on 2016/04/12.
 */
public class EdgeDialog extends DialogFragment {

    public static final String ARG_EDGE = "edge";

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
        View view = inflater.inflate(R.layout.dialog_edge, null);
        NumberPicker np = (NumberPicker)view.findViewById(R.id.numberPicker_edge);
        np.setMinValue(1);
        np.setMaxValue(13);
        np.setValue(getArguments().getInt(ARG_EDGE));
        builder.setView(view)
                .setTitle(R.string.edge)
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
            NumberPicker np = (NumberPicker)getDialog().findViewById(R.id.numberPicker_edge);
            Bundle data = new Bundle();
            data.putInt(ARG_EDGE, np.getValue());
            listener.onDialogClick(getTag(), data);
        }
    }

    public interface OnSaveListener {
        void onDialogClick(String tag, Bundle data);
    }
}
