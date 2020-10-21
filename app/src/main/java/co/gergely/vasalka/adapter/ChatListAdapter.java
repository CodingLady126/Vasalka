package co.gergely.vasalka.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import co.gergely.vasalka.R;
import co.gergely.vasalka.VasalkaApp;
import co.gergely.vasalka.inerfaces.OnHandleChat;
import co.gergely.vasalka.model.ChatRoom;
import co.gergely.vasalka.model.Person;
import co.gergely.vasalka.tools.Utils;
import com.bumptech.glide.Glide;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.List;


public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyViewHolder> {

    OnHandleChat listener;
    Context context;
    List<ChatRoom> lista;
    Person myPerson;


    public ChatListAdapter(Context context, OnHandleChat listener, List<ChatRoom> lista) {
        this.context = context;
        this.lista = lista;
        this.myPerson = VasalkaApp.getSession().getMyPerson();
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat, viewGroup, false);
        MyViewHolder vh = new MyViewHolder(mView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        final ChatRoom item = lista.get(i);
        Person partner;
        if (item.getPersonOne().getId().equals(myPerson.getId())) {
            partner = item.getPersonTwo();
        } else {
            partner = item.getPersonOne();
        }

        myViewHolder.name.setText(partner.getName());
        Glide.with(context).load(partner.getPhoto()).into(myViewHolder.profileImage);
        myViewHolder.chatListCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onChatOpen(item);
            }
        });

        myViewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder aDialog = new AlertDialog.Builder(context)
                        .setTitle(R.string.alertTitleAttention)
                        .setMessage(R.string.areYouSureDelete)
                        .setCancelable(true)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (item.getPersonOne().getId().equals((VasalkaApp.getSession().getMyPerson().getId()))) {
                                    item.setPersonOneAlive(false);
                                } else {
                                    item.setPersonTwoAlive(false);
                                }
                                listener.onChatDelete(item);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog dialog = aDialog.create();
                dialog.show();
            }
        });

        myViewHolder.bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder aDialog = new AlertDialog.Builder(context)
                        .setTitle(R.string.areYouSure)
                        .setMessage(R.string.doYouWantToAddToBookmark)
                        .setCancelable(true)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Utils.Companion.addChatToMyBookmark(context, item);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog dialog = aDialog.create();
                dialog.show();
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

        CardView chatListCard;
        TextView name;
        CircleImageView profileImage;
        ImageView delete;
        ImageView bookmark;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            chatListCard = itemView.findViewById(R.id.chatListCard);
            name = itemView.findViewById(R.id.chatListPersonName);
            profileImage = itemView.findViewById(R.id.chatListPersonImage);
            delete = itemView.findViewById(R.id.chatListDelete);
            bookmark = itemView.findViewById(R.id.chatListBookmark);

        }
    }
}