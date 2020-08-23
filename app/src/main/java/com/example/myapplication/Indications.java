package com.example.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayPosition;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy;

public class Indications  extends RobotActivity implements RobotLifecycleCallbacks {

    private static final String TAG = "Disability";
    private QiContext qiContext;

    public AlertDialog.Builder builder;
    public AlertDialog confirmLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set type and position of speechbar https://android.aldebaran.com/sdk/doc/pepper-sdk/ch4_api/conversation/conversation_feedbacks.html
        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.OVERLAY);
        setSpeechBarDisplayPosition(SpeechBarDisplayPosition.TOP);
        setContentView(R.layout.activity_indications);
        QiSDK.register(this, this);
        //Build the AlertBox here, otherwise "context" is null
        builder = new AlertDialog.Builder(this);
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
            //Set title and message that should appear in the dialog box
            builder.setTitle("Conferma");
            builder.setMessage("Vuoi andare al negozio A?");
            builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    fadeInImage((findViewById((R.id.marketFBGA_direction1))));
                    fadeInImage((findViewById((R.id.marketGA_direction1))));
                    fadeInImage((findViewById((R.id.marketGA_direction2))));
                    fadeInImage((findViewById((R.id.marketA_direction1))));
                    //Close the alert box
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Simply close the alert box
                    dialog.dismiss();
                }
            });
            confirmLocation = builder.create();
            confirmLocation.show();
        });

        marketB.setOnClickListener(v->{
            Log.i(TAG, "Market B selected");
            builder.setTitle("Conferma");
            builder.setMessage("Vuoi andare al negozio B?");
            builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    fadeInImage((findViewById((R.id.marketFBGA_direction1))));
                    fadeInImage((findViewById((R.id.marketB_direction1))));
                    //Close the alert box
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            confirmLocation = builder.create();
            confirmLocation.show();
        });

        marketC.setOnClickListener(v->{
            Log.i(TAG, "Market C selected");
            builder.setTitle("Conferma");
            builder.setMessage("Vuoi andare al negozio C?");
            builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    fadeInImage((findViewById((R.id.marketC_direction1))));
                    fadeInImage((findViewById((R.id.marketC_direction2))));
                    fadeInImage((findViewById((R.id.marketC_direction3))));
                    fadeInImage((findViewById((R.id.marketC_direction4))));
                    //Close the alert box
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            confirmLocation = builder.create();
            confirmLocation.show();
        });

        marketD.setOnClickListener(v->{
            Log.i(TAG, "Market D selected");
            builder.setTitle("Conferma");
            builder.setMessage("Vuoi andare al negozio D?");
            builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    fadeInImage((findViewById((R.id.marketD_direction1))));
                    fadeInImage((findViewById((R.id.marketDE_direction1))));
                    fadeInImage((findViewById((R.id.marketDE_direction2))));
                    fadeInImage((findViewById((R.id.marketDE_direction3))));
                    fadeInImage((findViewById((R.id.marketDE_direction4))));


                    //Close the alert box
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            confirmLocation = builder.create();
            confirmLocation.show();
        });

        marketE.setOnClickListener(v->{

            Log.i(TAG, "Market E selected");
            builder.setTitle("Conferma");
            builder.setMessage("Vuoi andare al negozio E?");
            builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    fadeInImage((findViewById((R.id.marketDE_direction1))));
                    fadeInImage((findViewById((R.id.marketDE_direction2))));
                    fadeInImage((findViewById((R.id.marketDE_direction3))));
                    fadeInImage((findViewById((R.id.marketDE_direction4))));
                    fadeInImage((findViewById((R.id.marketE_direction1))));

                    //Close the alert box
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            confirmLocation = builder.create();
            confirmLocation.show();
        });

        marketF.setOnClickListener(v->{
            Log.i(TAG, "Market F selected");
            builder.setTitle("Conferma");
            builder.setMessage("Vuoi andare al negozio F?");
            builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {

                    fadeInImage((findViewById((R.id.marketFBGA_direction1))));
                    fadeInImage((findViewById((R.id.marketF_direction1))));


                    //Close the alert box
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            confirmLocation = builder.create();
            confirmLocation.show();
        });

        marketG.setOnClickListener(v->{
            Log.i(TAG, "Market G selected");
            builder.setTitle("Conferma");
            builder.setMessage("Vuoi andare al negozio G?");
            builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    fadeInImage((findViewById((R.id.marketFBGA_direction1))));
                    fadeInImage((findViewById((R.id.marketGA_direction2))));
                    fadeInImage((findViewById((R.id.marketGA_direction1))));
                    fadeInImage((findViewById((R.id.marketG_direction1))));



                    //Close the alert box
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            confirmLocation = builder.create();
            confirmLocation.show();
        });



    }
    private void fadeInImage (ImageView im) {
        im.animate()
                .alpha(1f) //image becomes transparent
                .setDuration(200) //Set the length of the animation
                .setListener(new AnimatorListenerAdapter() {
                    //When the animation ends, make the button disappear
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        im.setVisibility(View.VISIBLE);
                    }
                });
    }
}