package co.gergely.vasalka.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import co.gergely.vasalka.R;
import co.gergely.vasalka.common.Constants;
import co.gergely.vasalka.model.Person;
import co.gergely.vasalka.model.Satisfaction;

import java.util.List;

public class SatisfactionAdapter extends RecyclerView.Adapter<SatisfactionAdapter.MyViewHolder> {

    String TAG = "SatisfactionAdapter";
    List<Satisfaction> satisfactionList;
    Person currentPerson;

    public SatisfactionAdapter(List<Satisfaction> list, Person person) {
        this.satisfactionList = list;
        this.currentPerson = person;
    }

    @NonNull
    @Override
    public SatisfactionAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_satisfaction_profile, viewGroup, false);
        MyViewHolder vh = new MyViewHolder(mView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i) {
        viewHolder.update(satisfactionList.get(i));
        Log.d(TAG, "--" + satisfactionList.get(i).getDstPerson().getName());
    }

    @Override
    public int getItemCount() {
        if (satisfactionList == null || satisfactionList.isEmpty()) {
            return 0;
        } else {
            return satisfactionList.size();
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView personNameLabel;
        private RatingBar rateBar;
        private TextView descTextView;
        private TextView serviceLabel;
        private TextView dateTimeLabel;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.personNameLabel = itemView.findViewById(R.id.satisfactionPerson);
            this.rateBar = itemView.findViewById(R.id.ratingBar2);
            this.serviceLabel = itemView.findViewById(R.id.serviceLabel);
            this.descTextView = itemView.findViewById(R.id.satisfactionDesc);
            this.dateTimeLabel = itemView.findViewById(R.id.dateTimeLabel);
        }

        public void update(Satisfaction satisfaction) {

            if (currentPerson.getId().equals(satisfaction.getDstPerson().getId())) {
                personNameLabel.setText(satisfaction.getSrcPerson().getName());
            } else {
                personNameLabel.setText(satisfaction.getDstPerson().getName());
            }
            rateBar.setRating(satisfaction.getRate());


            if (satisfaction.getServiceId().equals(Constants.SERVICE_IRONING_ID)) {
                serviceLabel.setText(R.string.service_ironing);
            } else if (satisfaction.getServiceId().equals(Constants.SERVICE_WASHING_ID)) {
                serviceLabel.setText(R.string.service_washing);
            } else if (satisfaction.getServiceId().equals(Constants.SERVICE_CLEANING_ID)) {
                serviceLabel.setText(R.string.service_cleaning);
            } else if (satisfaction.getServiceId().equals(Constants.SERVICE_GARDENING_ID)) {
                serviceLabel.setText(R.string.service_gardening);
            }
            descTextView.setText(satisfaction.getDescription());
            dateTimeLabel.setText(satisfaction.getDateTime());
        }


    }


}
