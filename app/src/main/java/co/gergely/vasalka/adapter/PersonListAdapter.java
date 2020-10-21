package co.gergely.vasalka.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import co.gergely.vasalka.R;
import co.gergely.vasalka.activity.SearchPersonActivity;
import co.gergely.vasalka.model.Person;
import com.bumptech.glide.Glide;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.List;


public class PersonListAdapter extends RecyclerView.Adapter<PersonListAdapter.MyViewHolder> {


    List<Person> lista;
    Activity context;


    public PersonListAdapter(List<Person> lista, Activity context) {
        this.lista = lista;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_person_list, viewGroup, false);
        MyViewHolder vh = new MyViewHolder(mView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        final Person person = lista.get(i);
        myViewHolder.city.setText(person.getCity().getName());
        myViewHolder.name.setText(person.getName());
        if (person.getPhoto() != null
                && !person.getPhoto().isEmpty()
                && person.getPhoto().contains("http")) {
            Glide.with(context).load(person.getPhoto()).into(myViewHolder.photo);
        }
        myViewHolder.personItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SearchPersonActivity) context).onPersonClicked(person);
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


        TextView name;
        TextView city;
        CircleImageView photo;
        LinearLayout personItemLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            city = itemView.findViewById(R.id.city);
            photo = itemView.findViewById(R.id.photo);
            personItemLayout = itemView.findViewById(R.id.personItemLayout);
        }
    }

}
