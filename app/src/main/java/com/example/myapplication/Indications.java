package com.example.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy;

public class Indications  extends RobotActivity implements RobotLifecycleCallbacks {

    private static final String TAG = "Disability";
    private QiContext qiContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.OVERLAY);
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

    private void clearAllArrows() {
//        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout);
//        for (int i = 0; i < layout.getChildCount(); i++) {
//            View subView = layout .getChildAt(i);
//            if (subView instanceof ImageView) {
//                ImageView imageView = (ImageView) subView;
//                fadeOutImage(imageView);
//            }
//        }
    }

    private void initUiElements() {
        final Button marketA = findViewById(R.id.marketA);
        final Button marketB = findViewById(R.id.marketB);
        final Button marketC = findViewById(R.id.marketC);
        final Button marketD = findViewById(R.id.marketD);
        final Button marketE = findViewById(R.id.marketE);
        final Button marketF = findViewById(R.id.marketF);
        final Button marketG = findViewById(R.id.marketG);
        final Button terminate = findViewById(R.id.terminate);

        terminate.setOnClickListener( v-> {
            Log.i(TAG, "Map indication finished");
            //Restart to the previous activity
            startActivity(new Intent(this, Information.class));
        });

        marketA.setOnClickListener(v -> {
            Log.i(TAG, "Market A selected");
            clearAllArrows();
            fadeInImage((findViewById((R.id.marketFBGA_direction1))));
            fadeInImage((findViewById((R.id.marketGA_direction1))));
            fadeInImage((findViewById((R.id.marketGA_direction2))));
            fadeInImage((findViewById((R.id.marketA_direction1))));
        });

        marketB.setOnClickListener(v->{
            Log.i(TAG, "Market B selected");
            clearAllArrows();
            fadeInImage((findViewById((R.id.marketFBGA_direction1))));
            fadeInImage((findViewById((R.id.marketB_direction1))));
        });


        marketC.setOnClickListener(v->{
            Log.i(TAG, "Market C selected");
            clearAllArrows();
            fadeInImage((findViewById((R.id.marketC_direction1))));
            fadeInImage((findViewById((R.id.marketC_direction2))));
            fadeInImage((findViewById((R.id.marketC_direction3))));
            fadeInImage((findViewById((R.id.marketC_direction4))));
        });

        marketD.setOnClickListener(v -> {
            Log.i(TAG, "Market D selected");
            clearAllArrows();
            fadeInImage((findViewById((R.id.marketD_direction1))));
            fadeInImage((findViewById((R.id.marketDE_direction1))));
            fadeInImage((findViewById((R.id.marketDE_direction2))));
            fadeInImage((findViewById((R.id.marketDE_direction3))));
            fadeInImage((findViewById((R.id.marketDE_direction4))));
        });

        marketE.setOnClickListener(v -> {
            Log.i(TAG, "Market E selected");
            clearAllArrows();
            fadeInImage((findViewById((R.id.marketDE_direction1))));
            fadeInImage((findViewById((R.id.marketDE_direction2))));
            fadeInImage((findViewById((R.id.marketDE_direction3))));
            fadeInImage((findViewById((R.id.marketDE_direction4))));
            fadeInImage((findViewById((R.id.marketE_direction1))));
        });

        marketF.setOnClickListener(v->{
            Log.i(TAG, "Market F selected");
            clearAllArrows();
            fadeInImage((findViewById((R.id.marketFBGA_direction1))));
            fadeInImage((findViewById((R.id.marketF_direction1))));
        });

        marketG.setOnClickListener(v->{
            Log.i(TAG, "Market G selected");
            clearAllArrows();
            fadeInImage((findViewById((R.id.marketFBGA_direction1))));
            fadeInImage((findViewById((R.id.marketGA_direction2))));
            fadeInImage((findViewById((R.id.marketGA_direction1))));
            fadeInImage((findViewById((R.id.marketG_direction1))));
        });

    }

    private void fadeInImage(ImageView im) {
        im.animate().alpha(1f).setDuration(200).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        im.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void fadeOutImage(ImageView im) {
        im.animate().alpha(0f).setDuration(200).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        im.setVisibility(View.GONE);
                    }
                });
    }

}
