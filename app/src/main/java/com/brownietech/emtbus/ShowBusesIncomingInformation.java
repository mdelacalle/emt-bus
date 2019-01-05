package com.brownietech.emtbus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ShowBusesIncomingInformation extends DialogFragment {


    public void set_nombreParada(String _nombreParada) {
        this._nombreParada = _nombreParada;
    }

    String _nombreParada;
    ArrayList<Arrive> _arrives;

    public void set_codParada(String _codParada) {
        this._codParada = _codParada;
    }

    String _codParada;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup containerVg, @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, containerVg, savedInstanceState);

        ConstraintLayout container = (ConstraintLayout) inflater.inflate(R.layout.show_bus_info_dialog, null);
        TextView tw = container.findViewById(R.id.nombre_parada);
        RecyclerView arrivesRV = container.findViewById(R.id.arrives);
        tw.setText(_nombreParada);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        arrivesRV.setHasFixedSize(true);
        arrivesRV.setLayoutManager(mLayoutManager);
        arrivesRV.setAdapter(new ArrivesAdapter(_arrives,getContext()));

        return container;
    }

    public void setArrives(ArrayList<Arrive> arrives) {
        _arrives = arrives;
    }
}
