package co.gergely.vasalka.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import co.gergely.vasalka.R;
import co.gergely.vasalka.activity.MainActivity;
import co.gergely.vasalka.common.Constants;

import java.io.File;
import java.io.IOException;

public class PickImageDialog extends Dialog implements View.OnClickListener {

    private Activity activity = null;
    private LinearLayout camera;
    private LinearLayout gallery;
    private TextView cancel;
    private int action;

    public PickImageDialog(Activity activity, int action) {
        super(activity);
        this.activity = activity;
        this.action = action;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_pick_image);
        camera = findViewById(R.id.layout_camera);
        gallery = findViewById(R.id.layout_gallery);
        cancel = findViewById(R.id.pick_image_dialog_cancel);
        camera.setOnClickListener(this);
        gallery.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pick_image_dialog_cancel:
                dismiss();
                break;
            case R.id.layout_camera:
                File photoFile = null;
                try {
                    MainActivity activity = ((MainActivity) this.activity);
                    photoFile = activity.createImageFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(getContext(), "co.gergely.vasalka.provider", photoFile);
                    takePicture.putExtra(MediaStore.EXTRA_OUTPUT,
                            photoURI);
                }
                if (action == Constants.PHOTO_FOR_PERSON) {
                    activity.startActivityForResult(takePicture, Constants.PHOTO_FOR_PERSON_CAMERA);
                } else if (action == Constants.PHOTO_FOR_TENDER) {
                    activity.startActivityForResult(takePicture, Constants.PHOTO_FOR_TENDER_CAMERA);
                } else if (action == Constants.PHOTO_FOR_CHAT) {
                    activity.startActivityForResult(takePicture, Constants.PHOTO_FOR_CHAT_CAMERA);
                }
                this.dismiss();
                break;
            case R.id.layout_gallery:
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (action == Constants.PHOTO_FOR_PERSON) {
                    activity.startActivityForResult(pickPhoto, Constants.PHOTO_FOR_PERSON_GALERY);
                } else if (action == Constants.PHOTO_FOR_TENDER) {
                    activity.startActivityForResult(pickPhoto, Constants.PHOTO_FOR_TENDER_GALERY);
                } else if (action == Constants.PHOTO_FOR_CHAT) {
                    activity.startActivityForResult(pickPhoto, Constants.PHOTO_FOR_CHAT_GALERY);
                }
                this.dismiss();
                break;
            default:
                break;
        }
    }


}
