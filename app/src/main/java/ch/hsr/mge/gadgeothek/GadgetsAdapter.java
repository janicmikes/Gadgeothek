package ch.hsr.mge.gadgeothek;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.hsr.mge.gadgeothek.domain.Gadget;

public class GadgetsAdapter extends RecyclerView.Adapter<GadgetsAdapter.MyViewHolder> {
    private List<Gadget> gadgetList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, manufacturer;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.gadget_list_name);
            manufacturer = (TextView) view.findViewById(R.id.gadget_list_manufacturer);
        }
    }


    public GadgetsAdapter() {
        this.gadgetList = new ArrayList<>();
    }

    public void setGadgetList(List<Gadget> gadgetList){
        this.gadgetList = gadgetList;
        this.notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gadget_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Gadget gadget = gadgetList.get(position);
        holder.name.setText(gadget.getName());
        holder.manufacturer.setText(gadget.getManufacturer());
    }

    @Override
    public int getItemCount() {
        return gadgetList.size();
    }

}
