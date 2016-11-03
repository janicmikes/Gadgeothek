package ch.hsr.mge.gadgeothek;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.hsr.mge.gadgeothek.domain.Gadget;
import ch.hsr.mge.gadgeothek.domain.Loan;
import ch.hsr.mge.gadgeothek.domain.Reservation;
import ch.hsr.mge.gadgeothek.service.IDecoratorService;
import ch.hsr.mge.gadgeothek.service.LocalDecoratorService;

public class GadgetsAdapter extends RecyclerView.Adapter<GadgetsAdapter.MyViewHolder> {
    private List<Gadget> gadgetList;
    private List<Loan> loansList;
    private List<Reservation> reservationList;

    private IDecoratorService decorator;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, manufacturer;
        public ImageView manufacturerLogo;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.gadget_list_name);
            manufacturer = (TextView) view.findViewById(R.id.gadget_list_manufacturer);
            manufacturerLogo = (ImageView) view.findViewById(R.id.gadget_list_manufacturer_logo);
        }
    }


    public GadgetsAdapter() {
        this.decorator = LocalDecoratorService.getDecoratorService();
        this.gadgetList = new ArrayList<>();
        this.loansList = new ArrayList<>();
        this.reservationList = new ArrayList<>();
    }

    public void setGadgetList(List<Gadget> gadgetList){
        this.reservationList.clear();
        this.loansList.clear();

        this.gadgetList = gadgetList;

        this.notifyDataSetChanged();
    }

    public void setLoanList(List<Loan> loanList){
        gadgetList.clear();
        this.reservationList.clear();

        this.loansList = loanList;

        for (Loan loan : loanList) {
            gadgetList.add(loan.getGadget());
        }

        this.notifyDataSetChanged();
    }

    public void setReservationList(List<Reservation> reservationList){
        gadgetList.clear();
        this.loansList.clear();

        this.reservationList = reservationList;

        for (Reservation reservation : reservationList) {
            gadgetList.add(reservation.getGadget());
        }
        this.notifyDataSetChanged();
    }

    public Gadget getGadgetByPosition(int position){
        return gadgetList.get(position);
    }

    public Reservation getReservationByPosition(int position) {
        return reservationList.get(position);
    }

    public Loan getLoanByPosition(int position){
        return loansList.get(position);
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
        int imageID = decorator.getDrawableIdForManufacturerName(gadget.getManufacturer());
        holder.manufacturerLogo.setImageResource(imageID);
    }

    @Override
    public int getItemCount() {
        return gadgetList.size();
    }

}
