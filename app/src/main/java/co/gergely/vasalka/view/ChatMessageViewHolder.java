package co.gergely.vasalka.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import co.gergely.vasalka.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatMessageViewHolder extends RecyclerView.ViewHolder {

    public LinearLayout myMessageLayout;
    public CircleImageView myMessageProfileImage;
    public TextView myMessageDate;
    public TextView myMessageText;
    public ImageView myMessageImage;
    public CardView myCardView;

    public LinearLayout partnerMessageLayout;
    public CircleImageView partnerMessageProfileImage;
    public TextView partnerMessageDate;
    public TextView partnerMessageText;
    public ImageView partnerMessageImage;
    public CardView partnerCardView;

    public ChatMessageViewHolder(@NonNull View itemView) {
        super(itemView);

        myMessageLayout = itemView.findViewById(R.id.myMessageLayout);
        myMessageProfileImage = itemView.findViewById(R.id.myMessageProfileImage);
        myMessageText = itemView.findViewById(R.id.myMessageText);
        myMessageDate = itemView.findViewById(R.id.myMessageDate);
        myMessageImage = itemView.findViewById(R.id.myMessageImage);
        myCardView = itemView.findViewById(R.id.myCardView);

        partnerMessageLayout = itemView.findViewById(R.id.partnerMessageLayout);
        partnerMessageProfileImage = itemView.findViewById(R.id.partnerMessageProfileImage);
        partnerMessageDate = itemView.findViewById(R.id.partnerMessageDate);
        partnerMessageText = itemView.findViewById(R.id.partnerMessageText);
        partnerMessageImage = itemView.findViewById(R.id.partnerMessageImage);
        partnerCardView = itemView.findViewById(R.id.partnerCardView);

    }


}
