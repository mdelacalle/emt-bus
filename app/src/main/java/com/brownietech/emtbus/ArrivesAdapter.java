package com.brownietech.emtbus;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ArrivesAdapter extends RecyclerView.Adapter<ArrivesAdapter.ViewHolder> {


    private static  ArrayList<Arrive> _arrives = new ArrayList<>();
    private final Context _context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
         public ViewHolder(View view) {
             super(view);
         }

        public void bind(int position) {

             Arrive arrive = _arrives.get(position);
            Log.e("***" , "position:"+position+ ", arrive:"+arrive.toString());
            ((TextView)itemView.findViewById(R.id.line)).setText(""+arrive.getLineId());
            ((TextView)itemView.findViewById(R.id.destination)).setText(arrive.getDestination());
            ((TextView)itemView.findViewById(R.id.time)).setText(arrive.getFormattedTime() + " mins.");
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ArrivesAdapter(ArrayList<Arrive> arrives, Context context) {
        _arrives = arrives;
        _context =  context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ArrivesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(_context)
                .inflate(R.layout.arrive_item, parent, false);
        ViewHolder vh = new ViewHolder(layout);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.e("***", "BIND:" + position);
        holder.bind(position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return _arrives.size();
    }
}
