package co.gergely.vasalka.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import co.gergely.vasalka.R;
import co.gergely.vasalka.common.Constants;
import co.gergely.vasalka.inerfaces.OnHandlePerson;
import co.gergely.vasalka.model.SatisfactionSummary;
import co.gergely.vasalka.model.SearchPerson;
import co.gergely.vasalka.tools.Utils;
import com.bumptech.glide.Glide;

import java.util.List;

public class PersonServiceAdapter extends RecyclerView.Adapter<PersonServiceAdapter.MyViewHolder> {

    List<SearchPerson> lista;
    Context context;
    OnHandlePerson listener;

    public PersonServiceAdapter(Context context, List<SearchPerson> lista, OnHandlePerson listener) {
        this.lista = lista;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_service_profile, viewGroup, false);
        MyViewHolder vh = new MyViewHolder(mView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.update(lista.get(i));
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

        private CardView personCard;

        private ImageView profileImage;
        private TextView name;
        private TextView city;
        private TextView distance;

        private ImageView star1;
        private ImageView star2;
        private ImageView star3;
        private ImageView star4;
        private ImageView star5;

        private ImageView serviceIronig;
        private ImageView serviceWashing;
        private ImageView serviceCleaning;
        private ImageView serviceGardening;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            this.personCard = itemView.findViewById(R.id.personServiceCard);

            this.profileImage = itemView.findViewById(R.id.profileImage);
            this.name = itemView.findViewById(R.id.profileName);
            this.city = itemView.findViewById(R.id.cityName);
            this.distance = itemView.findViewById(R.id.distanceLabel);

            this.star1 = itemView.findViewById(R.id.star1);
            this.star2 = itemView.findViewById(R.id.star2);
            this.star3 = itemView.findViewById(R.id.star3);
            this.star4 = itemView.findViewById(R.id.star4);
            this.star5 = itemView.findViewById(R.id.star5);

            this.serviceIronig = itemView.findViewById(R.id.serviceIroning);
            this.serviceWashing = itemView.findViewById(R.id.serviceWashing);
            this.serviceCleaning = itemView.findViewById(R.id.serviceCleaning);
            this.serviceGardening = itemView.findViewById(R.id.serviceGardening);
        }

        public void update(final SearchPerson person) {
            Glide.with(this.profileImage).load(person.getPhoto()).into(this.profileImage);
            this.name.setText(person.getName());
            this.city.setText(person.getCity().getName());
            tintStars(person.getSatisfactionSummary());

            if (person.getDistance() == 0) {
                this.distance.setVisibility(View.GONE);
            } else {
                this.distance.setText((int) Math.round(person.getDistance()) + context.getString(R.string.distanceEndLabel));
            }

            if (!Utils.Companion.hasService(person, Constants.SERVICE_IRONING_ID)) {
                this.serviceIronig.setVisibility(View.GONE);
            }
            if (!Utils.Companion.hasService(person, Constants.SERVICE_WASHING_ID)) {
                this.serviceWashing.setVisibility(View.GONE);
            }
            if (!Utils.Companion.hasService(person, Constants.SERVICE_CLEANING_ID)) {
                this.serviceCleaning.setVisibility(View.GONE);
            }
            if (!Utils.Companion.hasService(person, Constants.SERVICE_GARDENING_ID)) {
                this.serviceGardening.setVisibility(View.GONE);
            }

            this.personCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onPersonClicked(person);
                }
            });
        }

        private void tintStars(SatisfactionSummary summary) {
            if (summary.getAvg() == null) {
                return;
            } else if (summary.getAvg() < 2) {
                this.star1.setColorFilter(
                        ContextCompat.getColor(context, R.color.smsBtn),
                        android.graphics.PorterDuff.Mode.SRC_IN);
            } else if (summary.getAvg() >= 2 && summary.getAvg() < 3) {
                this.star1.setColorFilter(
                        ContextCompat.getColor(context, R.color.smsBtn),
                        android.graphics.PorterDuff.Mode.SRC_IN);
                this.star2.setColorFilter(
                        ContextCompat.getColor(context, R.color.smsBtn),
                        android.graphics.PorterDuff.Mode.SRC_IN);
            } else if (summary.getAvg() >= 3 && summary.getAvg() < 4) {
                this.star1.setColorFilter(
                        ContextCompat.getColor(context, R.color.smsBtn),
                        android.graphics.PorterDuff.Mode.SRC_IN);
                this.star2.setColorFilter(
                        ContextCompat.getColor(context, R.color.smsBtn),
                        android.graphics.PorterDuff.Mode.SRC_IN);
                this.star3.setColorFilter(
                        ContextCompat.getColor(context, R.color.smsBtn),
                        android.graphics.PorterDuff.Mode.SRC_IN);
            } else if (summary.getAvg() >= 4 && summary.getAvg() < 5) {
                this.star1.setColorFilter(
                        ContextCompat.getColor(context, R.color.smsBtn),
                        android.graphics.PorterDuff.Mode.SRC_IN);
                this.star2.setColorFilter(
                        ContextCompat.getColor(context, R.color.smsBtn),
                        android.graphics.PorterDuff.Mode.SRC_IN);
                this.star3.setColorFilter(
                        ContextCompat.getColor(context, R.color.smsBtn),
                        android.graphics.PorterDuff.Mode.SRC_IN);
                this.star4.setColorFilter(
                        ContextCompat.getColor(context, R.color.smsBtn),
                        android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
                this.star1.setColorFilter(
                        ContextCompat.getColor(context, R.color.smsBtn),
                        android.graphics.PorterDuff.Mode.SRC_IN);
                this.star2.setColorFilter(
                        ContextCompat.getColor(context, R.color.smsBtn),
                        android.graphics.PorterDuff.Mode.SRC_IN);
                this.star3.setColorFilter(
                        ContextCompat.getColor(context, R.color.smsBtn),
                        android.graphics.PorterDuff.Mode.SRC_IN);
                this.star4.setColorFilter(
                        ContextCompat.getColor(context, R.color.smsBtn),
                        android.graphics.PorterDuff.Mode.SRC_IN);
                this.star5.setColorFilter(
                        ContextCompat.getColor(context, R.color.smsBtn),
                        android.graphics.PorterDuff.Mode.SRC_IN);

            }


        }


    }

}
