package co.gergely.vasalka.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import co.gergely.vasalka.BuildConfig;
import co.gergely.vasalka.R;
import co.gergely.vasalka.api.ApiTender;
import co.gergely.vasalka.api.NetworkClient;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.File;

public class FileUploadProgressDialog extends Dialog {

    private static final String TAG = "FileUploadProgressDlg";
    private final boolean debug = true;
    private String path1 = null;
    private String path2 = null;
    private String path3 = null;
    private File file1 = null;
    private File file2 = null;
    private File file3 = null;
    private MultipartBody.Part part1 = null;
    private MultipartBody.Part part2 = null;
    private MultipartBody.Part part3 = null;
    private Long personId;
    private Long tenderId;
    private int maxValue = 0;
    private ProgressBar progressBar;
    private TextView countLabel;
    private ApiTender tenderService = NetworkClient.getRetrofit(null).create(ApiTender.class);

    public FileUploadProgressDialog(Context context, Long tenderId, Long personId, String path1, String path2, String path3) {
        super(context);
        this.path1 = path1;
        this.path2 = path2;
        this.path3 = path3;
        this.personId = personId;
        this.tenderId = tenderId;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_file_upload_progress);

        progressBar = findViewById(R.id.progressBar);
        countLabel = findViewById(R.id.countLabel);

        if (path1 != null && !path1.isEmpty() && path2 != null && !path2.isEmpty() && path3 != null && !path3.isEmpty()) {
            maxValue = 3;
        } else if (path1 != null && !path1.isEmpty() && path2 != null && !path2.isEmpty() && (path3 == null || path3.isEmpty())) {
            maxValue = 2;
        } else if (path1 != null && !path1.isEmpty() && ((path2 == null || path2.isEmpty()) && (path3 == null || path3.isEmpty()))) {
            maxValue = 1;
        }
        progressBar.setMax(maxValue);
        countLabel.setText("0/" + maxValue);
    }

    @Override
    public void show() {
        super.show();


        if (path1 != null && !path1.isEmpty()) {
            file1 = new File(path1);
            RequestBody fileReqBody1 = RequestBody.create(MediaType.parse("multipart/form-data"), file1);
            part1 = MultipartBody.Part.createFormData("image", file1.getName(), fileReqBody1);
        }


        if (path2 != null && !path2.isEmpty()) {
            file2 = new File(path2);
            RequestBody fileReqBody2 = RequestBody.create(MediaType.parse("multipart/form-data"), file2);
            part2 = MultipartBody.Part.createFormData("image", file2.getName(), fileReqBody2);
        }


        if (path3 != null && !path3.isEmpty()) {
            file3 = new File(path3);
            RequestBody fileReqBody3 = RequestBody.create(MediaType.parse("multipart/form-data"), file3);
            part3 = MultipartBody.Part.createFormData("image", file3.getName(), fileReqBody3);
        }

        uploadFile1();

    }


    private void uploadFile1() {

        tenderService.uploadTenderImages(part1, tenderId, personId, 1)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        progressBar.setProgress(1);
                        countLabel.setText("1/" + maxValue);
                        if (path2 != null && !path2.isEmpty()) {
                            uploadFile2();
                        } else {
                            dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        if (BuildConfig.DEBUG && debug) {
                            Log.d(TAG, "Fileupload1 error: " + t.getLocalizedMessage());
                            t.printStackTrace();
                        }
                        dismiss();
                    }
                });

    }

    private void uploadFile2() {

        tenderService.uploadTenderImages(part2, tenderId, personId, 2)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        progressBar.setProgress(2);
                        countLabel.setText("2/" + maxValue);
                        if (path3 != null && !path3.isEmpty()) {
                            uploadFile3();
                        } else {
                            dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        if (BuildConfig.DEBUG && debug) {
                            Log.d(TAG, "Fileupload2 error: " + t.getLocalizedMessage());
                            t.printStackTrace();
                        }
                        dismiss();
                    }
                });

    }

    private void uploadFile3() {

        tenderService.uploadTenderImages(part3, tenderId, personId, 3)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        progressBar.setProgress(3);
                        countLabel.setText("3/" + maxValue);
                        dismiss();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        if (BuildConfig.DEBUG && debug) {
                            Log.d(TAG, "Fileupload2 error: " + t.getLocalizedMessage());
                            t.printStackTrace();
                        }
                        dismiss();
                    }
                });

    }

}
