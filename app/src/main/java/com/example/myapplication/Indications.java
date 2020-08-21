package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayPosition;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy;

public class Indications  extends RobotActivity implements RobotLifecycleCallbacks {

    private static final String TAG = "Disability";
    private QiContext qiContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set type and position of speechbar https://android.aldebaran.com/sdk/doc/pepper-sdk/ch4_api/conversation/conversation_feedbacks.html
        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.OVERLAY);
        setSpeechBarDisplayPosition(SpeechBarDisplayPosition.TOP);
        setContentView(R.layout.activity_indications);
        QiSDK.register(this, this);

    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        this.qiContext = qiContext;
        initUiElements();
    }

    @Override
    public void onRobotFocusLost() {

    }

    @Override
    public void onRobotFocusRefused(String reason) {

    }

    private void initUiElements() {
        final Button marketA = findViewById(R.id.marketA);
        final Button marketB = findViewById(R.id.marketB);
        final Button marketC = findViewById(R.id.marketC);
        final Button marketD = findViewById(R.id.marketD);
        final Button marketE = findViewById(R.id.marketE);
        final Button marketF = findViewById(R.id.marketF);
        final Button marketG = findViewById(R.id.marketG);

        marketA.setOnClickListener(v->{
            Log.i(TAG, "Market A selected");
        });

        marketB.setOnClickListener(v->{
            Log.i(TAG, "Market B selected");
        });

        marketC.setOnClickListener(v->{
            Log.i(TAG, "Market C selected");
        });

        marketD.setOnClickListener(v->{
            Log.i(TAG, "Market D selected");
        });

        marketE.setOnClickListener(v->{

            Log.i(TAG, "Market E selected");
        });

        marketF.setOnClickListener(v->{
            Log.i(TAG, "Market F selected");
        });

        marketG.setOnClickListener(v->{
            Log.i(TAG, "Market G selected");
        });


    }
}