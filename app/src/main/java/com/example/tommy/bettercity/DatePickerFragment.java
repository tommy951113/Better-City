package com.example.tommy.bettercity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;


/**
 * Created by tommy on 2015/12/5.
 */
public class DatePickerFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_date,null);
        return new AlertDialog.Builder(getActivity()).setView(v).setTitle(R.string.birthday_choice).setPositiveButton(android.R.string.ok,null).create();
    }
}
