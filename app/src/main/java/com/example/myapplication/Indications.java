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

    public Button marketButton_Burger_King;
    public Button marketButton_Zara;
    public Button marketButton_Conad;
    public Button marketButton_Brico;
    public Button marketButton_Mediaworld;
    public Button marketButton_KIKO;
    public Button marketButton_Globo;
    public Button terminate;

    public PhraseSet phrase_set_Burger_King;
    public PhraseSet phrase_set_Zara;
    public PhraseSet phrase_set_Conad;
    public PhraseSet phrase_set_Brico;
    public PhraseSet phrase_set_Mediaworld;
    public PhraseSet phrase_set_KIKO;
    public PhraseSet phrase_set_Globo;
    public PhraseSet endInteraction;
    public PhraseSet goBack;

    private ImageView brico_imageview;
    private ImageView burger_king_imageview;
    private ImageView conad_imageview;
    private ImageView globo_imageview;
    private ImageView kiko_imageview;
    private ImageView mediaworld_imageview;
    private ImageView zara_imageview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        globalVariables = (GlobalVariables) getIntent().getSerializableExtra("globalVariables");
        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.OVERLAY);
        setContentView(R.layout.activity_indications);
        QiSDK.register(this, this);

        marketButton_Burger_King = findViewById(R.id.market_burger_king);
        marketButton_Zara = findViewById(R.id.market_zara);
        marketButton_Conad = findViewById(R.id.market_conad);
        marketButton_Brico = findViewById(R.id.market_brico);
        marketButton_Mediaworld = findViewById(R.id.market_mediaworld);
        marketButton_KIKO = findViewById(R.id.market_kiko);
        marketButton_Globo = findViewById(R.id.market_globo);
        terminate = findViewById(R.id.terminate);

        brico_imageview = findViewById(R.id.market_brico_direction);
        burger_king_imageview = findViewById(R.id.market_burger_king_direction);
        conad_imageview = findViewById(R.id.market_conad_direction);
        globo_imageview = findViewById(R.id.market_globo_direction);
        kiko_imageview = findViewById(R.id.market_kiko_direction);
        mediaworld_imageview = findViewById(R.id.market_mediaworld_direction);
        zara_imageview = findViewById(R.id.market_zara_direction);

        initUiElements();
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        this.qiContext = qiContext;

        phrase_set_Burger_King = PhraseSetBuilder.with(qiContext).withTexts("hamburger", "Burger King", "fast food", "eat", "eating", "hungry", "starving").build();
        phrase_set_Zara = PhraseSetBuilder.with(qiContext).withTexts("Zara", "clothes").build();
        phrase_set_Conad = PhraseSetBuilder.with(qiContext).withTexts("Conad", "grocery", "groceries").build();
        phrase_set_Brico = PhraseSetBuilder.with(qiContext).withTexts("Brico", "bricolage", "DIY").build();
        phrase_set_Mediaworld = PhraseSetBuilder.with(qiContext).withTexts("Mediaworld", "technology", "smartphone").build();
        phrase_set_KIKO = PhraseSetBuilder.with(qiContext).withTexts("KIKO", "make up").build();
        phrase_set_Globo = PhraseSetBuilder.with(qiContext).withTexts("Globo", "sneakers", "shoes").build();
        endInteraction = PhraseSetBuilder.with(qiContext).withTexts("Goodbye", "Bye", "Bye bye", "See you", "Don't need help anymore", "Terminate", "Finish", "Stop").build();
        goBack = PhraseSetBuilder.with(qiContext).withTexts("Go back", "Previous", "Something else").build();

        initTerminateInteraction();

        if (!globalVariables.getMute()) {
            indications();
        }
    }

    private void indications() {
        Listen listen = ListenBuilder.with(qiContext).withPhraseSets(phrase_set_Burger_King, phrase_set_Zara, phrase_set_Conad, phrase_set_Brico, phrase_set_Mediaworld, phrase_set_KIKO, phrase_set_Globo, endInteraction, goBack).build();
        listen_result_future = listen.async().run();
        listen_result_future.andThenConsume(result -> {
            if (PhraseSetUtil.equals(result.getMatchedPhraseSet(), phrase_set_Burger_King)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() { marketButton_Burger_King.performClick(); }
                });
            } else if (PhraseSetUtil.equals(result.getMatchedPhraseSet(), phrase_set_Zara)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        marketButton_Zara.performClick();
                    }
                });
            } else if (PhraseSetUtil.equals(result.getMatchedPhraseSet(), phrase_set_Conad)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        marketButton_Conad.performClick();
                    }
                });
            } else if (PhraseSetUtil.equals(result.getMatchedPhraseSet(), phrase_set_Brico)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        marketButton_Brico.performClick();
                    }
                });
            } else if (PhraseSetUtil.equals(result.getMatchedPhraseSet(), phrase_set_Mediaworld)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        marketButton_Mediaworld.performClick();
                    }
                });
            } else if (PhraseSetUtil.equals(result.getMatchedPhraseSet(), phrase_set_KIKO)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        marketButton_KIKO.performClick();
                    }
                });
            } else if (PhraseSetUtil.equals(result.getMatchedPhraseSet(), phrase_set_Globo)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        marketButton_Globo.performClick();
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
        //
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        //
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

        marketButton_Burger_King.setOnClickListener(v -> {
            Log.i(TAG, "Burger King selected");
            hideAllArrows();
            burger_king_imageview.setVisibility(View.VISIBLE);

            if (listen_result_future != null) {
                listen_result_future.requestCancellation();
            }

            Future<Say> direction = SayBuilder.with(qiContext).withText("Burger King is right in front of you! Proceed 25 meters and you will find the entrance on your right.").buildAsync();
            direction.andThenConsume(directionGiven -> {
                directionGiven.async().run().andThenConsume(finished -> {
                    if (!globalVariables.getMute()) {
                        indications();
                    }
                });
            });
        });

        marketButton_Zara.setOnClickListener(v->{
            Log.i(TAG, "Zara selected");
            hideAllArrows();
            zara_imageview.setVisibility(View.VISIBLE);

            if (listen_result_future != null) {
                listen_result_future.requestCancellation();
            }

            Future<Say> direction = SayBuilder.with(qiContext).withText("Zara is on your right! Proceed for 50 meters and you will find the entrance on your right.").buildAsync();
            direction.andThenConsume(directionGiven -> {
                directionGiven.async().run().andThenConsume(finished -> {
                    if (!globalVariables.getMute()) {
                        indications();
                    }
                });
            });
        });


        marketButton_Conad.setOnClickListener(v->{
            Log.i(TAG, "Conad selected");
            hideAllArrows();
            conad_imageview.setVisibility(View.VISIBLE);

            if (listen_result_future != null) {
                listen_result_future.requestCancellation();
            }

            Future<Say> direction = SayBuilder.with(qiContext).withText("Conad is right behind you! The entrance is on your left, proceeding 40 meters.").buildAsync();
            direction.andThenConsume(directionGiven -> {
                directionGiven.async().run().andThenConsume(finished -> {
                    if (!globalVariables.getMute()) {
                        indications();
                    }
                });
            });
        });

        marketButton_Brico.setOnClickListener(v -> {
            Log.i(TAG, "Brico selected");
            hideAllArrows();
            brico_imageview.setVisibility(View.VISIBLE);

            if (listen_result_future != null) {
                listen_result_future.requestCancellation();
            }

            Future<Say> direction = SayBuilder.with(qiContext).withText("Brico is on your left! Continue for 75 meters and you will find the entrance on your left.").buildAsync();
            direction.andThenConsume(directionGiven -> {
                directionGiven.async().run().andThenConsume(finished -> {
                    if (!globalVariables.getMute()) {
                        indications();
                    }
                });
            });
        });

        marketButton_Mediaworld.setOnClickListener(v -> {
            Log.i(TAG, "Mediaworld selected");
            hideAllArrows();
            mediaworld_imageview.setVisibility(View.VISIBLE);

            if (listen_result_future != null) {
                listen_result_future.requestCancellation();
            }

            Future<Say> direction = SayBuilder.with(qiContext).withText("Mediaworld is on your left! Continue for 75 meters and you will find the entrance on your right.").buildAsync();
            direction.andThenConsume(directionGiven -> {
                directionGiven.async().run().andThenConsume(finished -> {
                    if (!globalVariables.getMute()) {
                        indications();
                    }
                });
            });
        });

        marketButton_KIKO.setOnClickListener(v->{
            Log.i(TAG, "KIKO selected");
            hideAllArrows();
            kiko_imageview.setVisibility(View.VISIBLE);

            if (listen_result_future != null) {
                listen_result_future.requestCancellation();
            }

            Future<Say> direction = SayBuilder.with(qiContext).withText("KIKO is right in front of you! Proceed 15 meters and you will find the entrance on your left.").buildAsync();
            direction.andThenConsume(directionGiven -> {
                directionGiven.async().run().andThenConsume(finished -> {
                    if (!globalVariables.getMute()) {
                        indications();
                    }
                });
            });
        });

        marketButton_Globo.setOnClickListener(v->{
            Log.i(TAG, "Globo selected");
            hideAllArrows();
            globo_imageview.setVisibility(View.VISIBLE);

            if (listen_result_future != null) {
                listen_result_future.requestCancellation();
            }

            Future<Say> direction = SayBuilder.with(qiContext).withText("Globo is right in front of you! Proceed 35 meters and you will find the entrance on your left.").buildAsync();
            direction.andThenConsume(directionGiven -> {
                directionGiven.async().run().andThenConsume(finished -> {
                    if (!globalVariables.getMute()) {
                        indications();
                    }
                });
            });
        });

    }

    public void hideAllArrows() {
        brico_imageview.setVisibility(View.GONE);
        burger_king_imageview.setVisibility(View.GONE);
        conad_imageview.setVisibility(View.GONE);
        globo_imageview.setVisibility(View.GONE);
        kiko_imageview.setVisibility(View.GONE);
        mediaworld_imageview.setVisibility(View.GONE);
        zara_imageview.setVisibility(View.GONE);
    }

}
