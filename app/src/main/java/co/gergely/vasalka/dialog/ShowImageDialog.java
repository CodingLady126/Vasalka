package co.gergely.vasalka.dialog;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import co.gergely.vasalka.R;
import co.gergely.vasalka.common.GlideApp;

public class ShowImageDialog extends Dialog {

    private String path;
    private Context context;

    public ShowImageDialog(Context context, String path) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        this.context = context;
        this.path = path;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_show_image);
        ImageView image = findViewById(R.id.image);
        final Dialog dialog = this;
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        if (path.contains("http")) {
            GlideApp.with(context).load(path).into(image);
        } else {
            image.setImageURI(Uri.parse(path));
        }
    }
}
