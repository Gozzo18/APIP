package com.example.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.AnimateBuilder;
import com.aldebaran.qi.sdk.builder.AnimationBuilder;
import com.aldebaran.qi.sdk.builder.ListenBuilder;
import com.aldebaran.qi.sdk.builder.PhraseSetBuilder;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayPosition;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy;
import com.aldebaran.qi.sdk.object.actuation.Animate;
import com.aldebaran.qi.sdk.object.actuation.Animation;
import com.aldebaran.qi.sdk.object.conversation.Listen;
import com.aldebaran.qi.sdk.object.conversation.ListenResult;
import com.aldebaran.qi.sdk.object.conversation.PhraseSet;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.aldebaran.qi.sdk.util.PhraseSetUtil;

public class Indications  extends RobotActivity implements RobotLifecycleCallbacks {

    private static final String TAG = "Disability";
    private GlobalVariables globalVariables;

    private QiContext qiContext;
    public Future<ListenResult> listen_result_future;

    public Animate goodbyeAnimation;
    public Future<Void> goodbyeAnimationFuture;

    public Button marketButtonA;
    public Button marketButtonB;
    public Button marketButtonC;
    public Button marketButtonD;
    public Button marketButtonE;
    public Button marketButtonF;
    public Button marketButtonG;
    public Button terminate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        globalVariables = (GlobalVariables) getIntent().getSerializableExtra("globalVariables");
        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.OVERLAY);
        setSpeechBarDisplayPosition(SpeechBarDisplayPosition.BOTTOM);
        setContentView(R.layout.activity_indications);
        QiSDK.register(this, this);

        marketButtonA = findViewById(R.id.marketA);
        marketButtonB = findViewById(R.id.marketB);
        marketButtonC = findViewById(R.id.marketC);
        marketButtonD = findViewById(R.id.marketD);
        marketButtonE = findViewById(R.id.marketE);
        marketButtonF = findViewById(R.id.marketF);
        marketButtonG = findViewById(R.id.marketG);
        terminate = findViewById(R.id.terminate);
        initUiElements();
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        this.qiContext = qiContext;
        initTerminateInteraction();
        if (!globalVariables.getMute() & !globalVariables.getBlind() & !globalVariables.getColorBlind() & !globalVariables.getDeaf() & !globalVariables.getVisuallyImpaired()){
            //If the user has no disability, use touch interactions and make Pepper also talk and listen
            manyIndications();
        }
    }

    private void manyIndications(){
        PhraseSet marketA = PhraseSetBuilder.with(qiContext).withTexts("market a", "shop a", "gap").build();
        PhraseSet marketB = PhraseSetBuilder.with(qiContext).withTexts("market b", "shop b", "zara").build();
        PhraseSet marketC = PhraseSetBuilder.with(qiContext).withTexts("market c", "shop c", "burger king").build();
        PhraseSet marketD = PhraseSetBuilder.with(qiContext).withTexts("market d", "shop d", "bata").build();
        PhraseSet marketE = PhraseSetBuilder.with(qiContext).withTexts("market e", "shop e", "old wild west").build();
        PhraseSet marketF = PhraseSetBuilder.with(qiContext).withTexts("market f", "shop f", "nike").build();
        PhraseSet marketG = PhraseSetBuilder.with(qiContext).withTexts("market g", "shop g", "levi's", "levi").build();
        PhraseSet endInteraction = PhraseSetBuilder.with(qiContext).withTexts("Goodbye", "Bye", "Bye bye", "See you", "Don't need help anymore", "Terminate", "Finish", "Stop").build();
        PhraseSet goBack = PhraseSetBuilder.with(qiContext).withTexts("Go back", "Previous", "Something else").build();

        Listen listen = ListenBuilder.with(qiContext).withPhraseSets(marketA, marketB, marketC, marketD, marketE, marketF, marketG, endInteraction, goBack).build();
        listen_result_future = listen.async().run();
        listen_result_future.andThenConsume(result->{
            if (PhraseSetUtil.equals(result.getMatchedPhraseSet(), marketA)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() { marketButtonA.performClick(); }
                });
            } else if (PhraseSetUtil.equals(result.getMatchedPhraseSet(), marketB)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        marketButtonB.performClick();
                    }
                });
            } else if (PhraseSetUtil.equals(result.getMatchedPhraseSet(), marketC)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        marketButtonC.performClick();
                    }
                });
            } else if (PhraseSetUtil.equals(result.getMatchedPhraseSet(), marketD)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        marketButtonD.performClick();
                    }
                });
            } else if (PhraseSetUtil.equals(result.getMatchedPhraseSet(), marketE)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        marketButtonE.performClick();
                    }
                });
            } else if (PhraseSetUtil.equals(result.getMatchedPhraseSet(), marketF)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        marketButtonF.performClick();
                    }
                });
            } else if (PhraseSetUtil.equals(result.getMatchedPhraseSet(), marketG)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        marketButtonG.performClick();
                    }
                });
            } else if (PhraseSetUtil.equals(result.getMatchedPhraseSet(), endInteraction)) {
                listen_result_future.requestCancellation();
                goodbyeAnimationFuture = goodbyeAnimation.async().run();
                goodbyeAnimationFuture.andThenConsume(finished->{
                    Intent changeActivity = new Intent(this, MainActivity.class);
                    startActivity(changeActivity);
                });
            } else if (PhraseSetUtil.equals(result.getMatchedPhraseSet(), goBack)) {
                listen_result_future.requestCancellation();
                Intent changeActivity = new Intent(this, Information.class);
                changeActivity.putExtra("globalVariables", globalVariables);
                startActivity(changeActivity);
            }
        });
    }

    @Override
    public void onRobotFocusLost() {

    }

    @Override
    public void onRobotFocusRefused(String reason) {

    }

    private void initTerminateInteraction(){
        Animation goodbyeAnimationObject = AnimationBuilder.with(qiContext).withResources(R.raw.goodbye).build();
        goodbyeAnimation = AnimateBuilder.with(qiContext).withAnimation(goodbyeAnimationObject).build();
        // As soon as the animation starts, Pepper says goodbye to the user
        goodbyeAnimation.addOnStartedListener(() -> {
            Say goodbye = SayBuilder.with(qiContext).withText("Have a great day, bye!").build();
            goodbye.async().run();
        });
    }

    private void initUiElements() {
        terminate.setOnClickListener( v-> {
            Log.i(TAG, "Map indication finished");
            //Restart to the previous activity
            Intent changeActivity = new Intent(this, Information.class);
            changeActivity.putExtra("globalVariables", globalVariables);
            startActivity(changeActivity);
        });

        marketButtonA.setOnClickListener(v -> {
            Log.i(TAG, "Market A selected");
            if (!globalVariables.getDeaf()){
                //First check if the listen action is running
                if (listen_result_future != null){
                    listen_result_future.requestCancellation();
                }
                Future<Say> direction = SayBuilder.with(qiContext).withText("GAP is very close to where we are! Proceed straight ahead for 100 meters and you will find the entrance on your right.").buildAsync();
                //Double call is required otherwise the Say action is not displayed and the Listen can not be re-launched
                direction.andThenConsume(directionGiven -> {
                    directionGiven.async().run().andThenConsume(finished -> {
                        if (!globalVariables.getMute() & !globalVariables.getBlind() & !globalVariables.getColorBlind() & !globalVariables.getDeaf() & !globalVariables.getVisuallyImpaired()) {
                            manyIndications();
                        }
                    });
                });
            }
            fadeInImage((findViewById((R.id.marketFBGA_direction1))));
            fadeInImage((findViewById((R.id.marketGA_direction1))));
            fadeInImage((findViewById((R.id.marketGA_direction2))));
            fadeInImage((findViewById((R.id.marketA_direction1))));
        });

        marketButtonB.setOnClickListener(v->{
            Log.i(TAG, "Market B selected");
            if (!globalVariables.getDeaf()){
                //First check if the listen action is running
                if (listen_result_future != null){
                    listen_result_future.requestCancellation();
                }
                Future<Say> direction = SayBuilder.with(qiContext).withText("ZARA is exactly behind me! Proceed straight ahead for 25 meters and you will find the entrance on your right.").buildAsync();
                direction.andThenConsume(directionGiven->{
                    directionGiven.async().run().andThenConsume(finished->{
                        if (!globalVariables.getMute() & !globalVariables.getBlind() & !globalVariables.getColorBlind() & !globalVariables.getDeaf() & !globalVariables.getVisuallyImpaired()) {
                            manyIndications();
                        }
                    });
                });
            }
            fadeInImage((findViewById((R.id.marketFBGA_direction1))));
            fadeInImage((findViewById((R.id.marketB_direction1))));
        });


        marketButtonC.setOnClickListener(v->{
            Log.i(TAG, "Market C selected");
            if (!globalVariables.getDeaf()){
                //First check if the listen action is running
                if (listen_result_future != null){
                    listen_result_future.requestCancellation();
                }
                Future<Say> direction = SayBuilder.with(qiContext).withText("Burger king is on our right! Proceed in that direction ahead for 70 meters and you will find the entrance on your right.").buildAsync();
                direction.andThenConsume(directionGiven->{
                    directionGiven.async().run().andThenConsume(finished->{
                        if (!globalVariables.getMute() & !globalVariables.getBlind() & !globalVariables.getColorBlind() & !globalVariables.getDeaf() & !globalVariables.getVisuallyImpaired()) {
                            manyIndications();
                        }
                    });
                });
            }
            fadeInImage((findViewById((R.id.marketC_direction1))));
            fadeInImage((findViewById((R.id.marketC_direction2))));
            fadeInImage((findViewById((R.id.marketC_direction3))));
            fadeInImage((findViewById((R.id.marketC_direction4))));
        });

        marketButtonD.setOnClickListener(v -> {
            Log.i(TAG, "Market D selected");
            if (!globalVariables.getDeaf()){
                //First check if the listen action is running
                if (listen_result_future != null){
                    listen_result_future.requestCancellation();
                }
                Future<Say> direction = SayBuilder.with(qiContext).withText("BATA is on our left! Proceed in that direction ahead for 200 meters and you will find the entrance on your left.").buildAsync();
                direction.andThenConsume(directionGiven->{
                    directionGiven.async().run().andThenConsume(finished->{
                        if (!globalVariables.getMute() & !globalVariables.getBlind() & !globalVariables.getColorBlind() & !globalVariables.getDeaf() & !globalVariables.getVisuallyImpaired()) {
                            manyIndications();
                        }
                    });
                });
            }
            fadeInImage((findViewById((R.id.marketD_direction1))));
            fadeInImage((findViewById((R.id.marketDE_direction1))));
            fadeInImage((findViewById((R.id.marketDE_direction2))));
            fadeInImage((findViewById((R.id.marketDE_direction3))));
            fadeInImage((findViewById((R.id.marketDE_direction4))));
        });

        marketButtonE.setOnClickListener(v -> {
            Log.i(TAG, "Market E selected");
            if (!globalVariables.getDeaf()){
                //First check if the listen action is running
                if (listen_result_future != null){
                    listen_result_future.requestCancellation();
                }
                Future<Say> direction = SayBuilder.with(qiContext).withText("Old Wild West is on our left! Proceed in that direction ahead for 200 meters and you will find the entrance on your right.").buildAsync();
                direction.andThenConsume(directionGiven->{
                    directionGiven.async().run().andThenConsume(finished->{
                        if (!globalVariables.getMute() & !globalVariables.getBlind() & !globalVariables.getColorBlind() & !globalVariables.getDeaf() & !globalVariables.getVisuallyImpaired()) {
                            manyIndications();
                        }
                    });
                });
            }
            fadeInImage((findViewById((R.id.marketDE_direction1))));
            fadeInImage((findViewById((R.id.marketDE_direction2))));
            fadeInImage((findViewById((R.id.marketDE_direction3))));
            fadeInImage((findViewById((R.id.marketDE_direction4))));
            fadeInImage((findViewById((R.id.marketE_direction1))));
        });

        marketButtonF.setOnClickListener(v->{
            Log.i(TAG, "Market F selected");
            if (!globalVariables.getDeaf()){
                //First check if the listen action is running
                if (listen_result_future != null){
                    listen_result_future.requestCancellation();
                }
                Future<Say> direction = SayBuilder.with(qiContext).withText("NIKE is exactly behind me! Proceed straight ahead for 25 meters and you will find the entrance on your left.").buildAsync();
                direction.andThenConsume(directionGiven->{
                    directionGiven.async().run().andThenConsume(finished->{
                        if (!globalVariables.getMute() & !globalVariables.getBlind() & !globalVariables.getColorBlind() & !globalVariables.getDeaf() & !globalVariables.getVisuallyImpaired()) {
                            manyIndications();
                        }
                    });
                });
            }
            fadeInImage((findViewById((R.id.marketFBGA_direction1))));
            fadeInImage((findViewById((R.id.marketF_direction1))));
        });

        marketButtonG.setOnClickListener(v->{
            Log.i(TAG, "Market G selected");
            if (!globalVariables.getDeaf()){
                //First check if the listen action is running
                if (listen_result_future != null){
                    listen_result_future.requestCancellation();
                }
                Future<Say> direction = SayBuilder.with(qiContext).withText("LEVI's is very close to where we are! Proceed straight ahead for 100 meters and you will find the entrance on your left.").buildAsync();
                direction.andThenConsume(directionGiven->{
                    directionGiven.async().run().andThenConsume(finished->{
                        if (!globalVariables.getMute() & !globalVariables.getBlind() & !globalVariables.getColorBlind() & !globalVariables.getDeaf() & !globalVariables.getVisuallyImpaired()) {
                            manyIndications();
                        }
                    });
                });
            }
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
