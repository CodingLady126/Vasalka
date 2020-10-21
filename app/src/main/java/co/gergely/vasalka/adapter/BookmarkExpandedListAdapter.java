package co.gergely.vasalka.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import co.gergely.vasalka.BuildConfig;
import co.gergely.vasalka.R;
import co.gergely.vasalka.VasalkaApp;
import co.gergely.vasalka.activity.MainActivity;
import co.gergely.vasalka.api.ApiBookmark;
import co.gergely.vasalka.api.NetworkClient;
import co.gergely.vasalka.common.Constants;
import co.gergely.vasalka.model.Bookmark;
import co.gergely.vasalka.model.Person;
import co.gergely.vasalka.model.Service;
import com.bumptech.glide.Glide;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.HashMap;
import java.util.List;

public class BookmarkExpandedListAdapter extends BaseExpandableListAdapter {

    private Activity context;
    private List<String> expandableListTitle;
    private HashMap<String, List<Bookmark>> expandableListDetail;


    public BookmarkExpandedListAdapter(Activity context,
                                       List<String> expandableListTitle,
                                       HashMap<String, List<Bookmark>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.expandableListTitle.get(groupPosition);
    }

    @Override
    public Bookmark getChild(int groupPosition, int childPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String listTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_bookmark_header, null);
        }

        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.listTitle);

        listTitleTextView.setText(listTitle);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {


        final Bookmark bookmark = (Bookmark) getChild(groupPosition, childPosition);
        if (BuildConfig.DEBUG) {
            Log.d("BOOKMARKADAPTER", "BOOKRMARK:" + bookmark.toString());
        }


        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_bookmark_item, null);
        }
        CircleImageView expandedListImageView = (CircleImageView) convertView
                .findViewById(R.id.expandedListItemImage);


        TextView expandedListTextView = (TextView) convertView
                .findViewById(R.id.expandedListItemLabel);


        if (bookmark.getObjectType().getId() == Constants.BOOKMARK_OBJECT_TYPE_PERSON_ID) {
            expandedListImageView.setVisibility(View.VISIBLE);

            Glide.with(convertView).load(bookmark.getPerson().getPhoto()).into(expandedListImageView);
            expandedListTextView.setText(bookmark.getPerson().getName());

        } else if (bookmark.getObjectType().getId() == Constants.BOOKMARK_OBJECT_TYPE_TENDER_ID) {
            expandedListImageView.setVisibility(View.GONE);
            String services = "";
            for (Service service : bookmark.getTender().getServiceList()) {
                if (service.getId().equals(Constants.SERVICE_IRONING_ID)) {
                    services += context.getString(R.string.service_ironing) + " ";
                } else if (service.getId().equals(Constants.SERVICE_CLEANING_ID)) {
                    services += context.getString(R.string.service_cleaning) + " ";
                } else if (service.getId().equals(Constants.SERVICE_WASHING_ID)) {
                    services += context.getString(R.string.service_washing) + " ";
                } else if (service.getId().equals(Constants.SERVICE_GARDENING_ID)) {
                    services += context.getString(R.string.service_gardening) + " ";
                }
            }
            expandedListTextView.setText(bookmark.getTender().getPerson().getName() + "- " + services);

        } else if (bookmark.getObjectType().getId() == Constants.BOOKMARK_OBJECT_TYPE_CHAT_ID) {
            expandedListImageView.setVisibility(View.VISIBLE);

            Person partner = null;

            if (bookmark.getChatRoom().getPersonOne().getId().equals(VasalkaApp.getSession().getMyPerson().getId())) {
                partner = bookmark.getChatRoom().getPersonTwo();
            } else if (bookmark.getChatRoom().getPersonTwo().getId().equals(VasalkaApp.getSession().getMyPerson().getId())) {
                partner = bookmark.getChatRoom().getPersonOne();
            }

            if (partner != null) {
                Glide.with(convertView).load(partner.getPhoto()).into(expandedListImageView);
                expandedListTextView.setText(partner.getName());
            }
        }


        ImageView deleteBtn = convertView.findViewById(R.id.deleteImageView);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle(R.string.alertTitleAttention)
                        .setMessage(R.string.areYouSureDelete)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {
                                ApiBookmark bookmarkService = NetworkClient.getRetrofit(null).create(ApiBookmark.class);
                                bookmarkService.deleteBookmark(bookmark.getId())
                                        .enqueue(new Callback<Void>() {
                                            @Override
                                            public void onResponse(Call<Void> call, Response<Void> response) {
                                                ((MainActivity) context).goToBookmarkList();
                                                dialog.dismiss();
                                            }
                                            @Override
                                            public void onFailure(Call<Void> call, Throwable t) {
                                                dialog.dismiss();
                                            }
                                        });
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.show();
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
