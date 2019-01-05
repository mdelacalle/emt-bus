package com.brownietech.emtbus;


import android.app.Activity;
import android.app.AlertDialog;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

import org.glob3.mobile.generated.Mark;
import org.glob3.mobile.generated.MarkTouchListener;

import java.util.ArrayList;

public class BusStopTouchListener extends MarkTouchListener implements EMTListener {
    private final String _busStop;
    private final String _denominacion;
    private final FragmentManager _fragmentManager;
    private final Activity _activity;
    private AlertDialog _dialog;

    public BusStopTouchListener(String busStop, String denominacion, FragmentManager fragmentManager, Activity activity) {
        super();
        this._busStop = busStop;
        this._denominacion = denominacion;
        this._fragmentManager = fragmentManager;
        this._activity = activity;
    }

    @Override
    public boolean touchedMark(Mark mark) {



        _activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                AlertDialog.Builder builder = new AlertDialog.Builder(_activity);
                builder.setMessage(R.string.get_info);
                _dialog = builder.create();
                _dialog.show();


                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        _dialog.dismiss();
                    }
                }, 1000);

                EMTAPI.getArrivesFromStop(_busStop,BusStopTouchListener.this);
            }
        });
        return false;
    }

    @Override
    public void onSuccess(ArrayList<Arrive> arrives) {
        ShowBusesIncomingInformation astp = new ShowBusesIncomingInformation();
        astp.setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_DayNight_NoActionBar);
        astp.set_codParada(_busStop);
        astp.set_nombreParada(_denominacion);
        astp.setArrives(arrives);
        astp.show(_fragmentManager, "BUS Activity");
    }

    @Override
    public void onError(String s) {
        _activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                AlertDialog.Builder builder = new AlertDialog.Builder(_activity);
                builder.setMessage(R.string.not_information)
                        .setTitle(R.string.error);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }
}
