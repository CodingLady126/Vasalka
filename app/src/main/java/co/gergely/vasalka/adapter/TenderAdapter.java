package co.gergely.vasalka.adapter;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import co.gergely.vasalka.R;
import co.gergely.vasalka.common.Constants;
import co.gergely.vasalka.inerfaces.OnHandleTender;
import co.gergely.vasalka.model.Service;
import co.gergely.vasalka.model.Tender;
import com.bumptech.glide.Glide;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.List;

public class TenderAdapter extends RecyclerView.Adapter<TenderAdapter.MyViewHolder> {

    private List<Tender> lista;
    private OnHandleTender listener;


    public TenderAdapter(OnHandleTender listener, List<Tender> lista) {
        this.listener = listener;
        this.lista = lista;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_tender, viewGroup, false);
        MyViewHolder vh = new MyViewHolder(mView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        final Tender tender = lista.get(i);
        myViewHolder.dateTime.setText(tender.getCreatedAt());
        myViewHolder.city.setText(tender.getCity().getName() + " + " + tender.getServiceArea() + "km");

        Log.d("TADAPTER", "SERVICE LIST SIZE: " + tender.getServiceList().size());

        for (Service service : tender.getServiceList()) {
            Log.d("TADAPTER", "Service id:" + service.getId());
            if (service.getId().equals(Constants.SERVICE_IRONING_ID)) {
                myViewHolder.ironingImage.setVisibility(View.VISIBLE);
                Log.d("TADAPTER", "Service: " + service.getName());
            }
            if (service.getId().equals(Constants.SERVICE_WASHING_ID)) {
                myViewHolder.washingImage.setVisibility(View.VISIBLE);
                Log.d("TADAPTER", "Service: " + service.getName());
            }
            if (service.getId().equals(Constants.SERVICE_CLEANING_ID)) {
                myViewHolder.cleaningImage.setVisibility(View.VISIBLE);
                Log.d("TADAPTER", "Service: " + service.getName());
            }
            if (service.getId().equals(Constants.SERVICE_GARDENING_ID)) {
                myViewHolder.gardeningImage.setVisibility(View.VISIBLE);
                Log.d("TADAPTER", "Service: " + service.getName());
            }
        }

        Glide.with(myViewHolder.profileImage).load(tender.getPerson().getPhoto()).into(myViewHolder.profileImage);

        myViewHolder.parentItem
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onTenderClicked(tender);
                    }
                });
    }


    @Override
    public int getItemCount() {
        if (lista == null || lista.isEmpty()) {
            return 0;
        } else {
            return lista.size();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        protected ConstraintLayout parentItem;
        protected TextView dateTime;
        protected ImageView ironingImage;
        protected ImageView washingImage;
        protected ImageView cleaningImage;
        protected ImageView gardeningImage;
        protected TextView city;
        protected CircleImageView profileImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            parentItem = itemView.findViewById(R.id.parentItem);
            dateTime = itemView.findViewById(R.id.time);
            ironingImage = itemView.findViewById(R.id.ironingImage);
            washingImage = itemView.findViewById(R.id.washingImage);
            cleaningImage = itemView.findViewById(R.id.cleaningImage);
            gardeningImage = itemView.findViewById(R.id.gardeningImage);
            profileImage = itemView.findViewById(R.id.profileImage);
            city = itemView.findViewById(R.id.city);
        }
    }
}
