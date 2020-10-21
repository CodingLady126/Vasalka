package co.gergely.vasalka.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import co.gergely.vasalka.BuildConfig;
import co.gergely.vasalka.R;
import co.gergely.vasalka.VasalkaApp;
import co.gergely.vasalka.api.ApiSatisfaction;
import co.gergely.vasalka.api.NetworkClient;
import co.gergely.vasalka.model.Person;
import co.gergely.vasalka.model.Satisfaction;
import co.gergely.vasalka.model.Service;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RatePersonDialog extends Dialog implements View.OnClickListener {

    private final String TAG = "PERSON_REATE";
    private boolean debug = true;
    private EditText rateEditText;
    private RatingBar ratingBar;
    private Person person;
    Context context;
    private Spinner serviceSpinner;
    private ProgressBar progressBar;


    public RatePersonDialog(Context context, Person person) {
        super(context);
        this.context = context;
        this.person = person;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_rate_person);

        findViewById(R.id.cancelBtn).setOnClickListener(this);
        findViewById(R.id.rateBtn).setOnClickListener(this);

        ratingBar = findViewById(R.id.ratingBar);
        rateEditText = findViewById(R.id.rateDesc);
        progressBar = findViewById(R.id.progressBar);
        serviceSpinner = findViewById(R.id.serviceSpinner);

        ArrayAdapter<Service> adapter = new ArrayAdapter<Service>(context, android.R.layout.simple_spinner_item, person.getServiceList());
        serviceSpinner.setAdapter(adapter);

        ratingBar.setMax(5);
        ratingBar.setRating(0);
        ratingBar.setStepSize(1f);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancelBtn:
                dismiss();
                break;
            case R.id.rateBtn:

                Satisfaction satisfaction = new Satisfaction();
                satisfaction.setDescription(rateEditText.getText().toString());
                satisfaction.setRate((int) ratingBar.getRating());
                satisfaction.setSrcPerson(VasalkaApp.getSession().getMyPerson());
                satisfaction.setDstPerson(person);
                satisfaction.setServiceId(((Service) serviceSpinner.getSelectedItem()).getId());

                ApiSatisfaction satisfactionService = NetworkClient.getRetrofit(null).create(ApiSatisfaction.class);
                satisfactionService
                        .addSatisfaction(satisfaction.getDstPerson().getId(), satisfaction)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Satisfaction>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                progressBar.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onNext(Satisfaction satisfaction) {
                                progressBar.setVisibility(View.GONE);

                            }

                            @Override
                            public void onError(Throwable e) {
                                progressBar.setVisibility(View.GONE);

                                if (BuildConfig.DEBUG && debug) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onComplete() {
                                progressBar.setVisibility(View.GONE);

                                dismiss();
                            }
                        });
                break;
        }
    }
}
