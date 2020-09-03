package com.example.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.AnimateBuilder;
import com.aldebaran.qi.sdk.builder.AnimationBuilder;
import com.aldebaran.qi.sdk.builder.ChatBuilder;
import com.aldebaran.qi.sdk.builder.QiChatbotBuilder;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.builder.TopicBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayPosition;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy;
import com.aldebaran.qi.sdk.object.actuation.Animate;
import com.aldebaran.qi.sdk.object.actuation.Animation;
import com.aldebaran.qi.sdk.object.conversation.Chat;
import com.aldebaran.qi.sdk.object.conversation.QiChatVariable;
import com.aldebaran.qi.sdk.object.conversation.QiChatbot;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.aldebaran.qi.sdk.object.conversation.Topic;

public class Disability extends RobotActivity implements RobotLifecycleCallbacks {

    private static final String TAG = "Disability";
    private QiContext qiContext;

    private GlobalVariables globalVariables;

    private QiChatbot disability_chatBot;
    public QiChatVariable disability_type;
    private Chat disability_chat;
    public Future<Void> future_chat;


    private Animate affirmationAnimation;
    private Animate goodbye_animation;
    private Future<Void> affirmationAnimationF;

    private Animate gotItAnimation;
    private Future<Void> gotItAnimationF;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the global variables
        globalVariables = (GlobalVariables) getIntent().getSerializableExtra("globalVariables");

        // Set the type of the speechBar
        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.OVERLAY);

        //Set the current layout view to activity_main.xml
        setContentView(R.layout.activity_disability);

        QiSDK.register(this, this);
    }

    @Override
    protected void onDestroy() {
        // Unregister the RobotLifecycleCallbacks for this Activity.
        QiSDK.unregister(this, this);
        super.onDestroy();
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        this.qiContext = qiContext;

        initChat();

        initAnimation();

        affirmationAnimationF = affirmationAnimation.async().run();
        affirmationAnimationF.andThenConsume(ask->{
            initUiElements();
            //Catch the disability type
            disability_type = disability_chatBot.variable("disability_type");
            //Catch the type of disability
            disability_type.addOnValueChangedListener(currentValue -> {
                Log.i(TAG, "Disability type: " + currentValue);
                switch (currentValue) {
                    case "blind":
                        globalVariables.setBlind(true);
                        break;
                    case "visuallyImpaired":
                        globalVariables.setVisuallyImpaired(true);
                        break;
                    case "colorBlind":
                        globalVariables.setColorBlind(true);
                        break;
                    case "deaf":
                        globalVariables.setDeaf(true);
                        break;
                    case "no":
                        break;
                }
                future_chat.requestCancellation();
                gotItAnimationF = gotItAnimation.async().run();
                gotItAnimationF.andThenConsume(change->{
                    Intent changeActivity = new Intent(this, Information.class);
                    changeActivity.putExtra("globalVariables", globalVariables);
                    startActivity(changeActivity);
                });
            });
        });
    }

    @Override
    public void onRobotFocusLost() {
        if (disability_chat != null) {
            disability_chat.removeAllOnStartedListeners();
        }

        if (disability_type != null) {
            disability_type.removeAllOnValueChangedListeners();
        }
        if (affirmationAnimation != null) {
            affirmationAnimation.removeAllOnStartedListeners();
        }
    }

    @Override
    public void onRobotFocusRefused(String reason) {

    }

    private void initAnimation() {
        // Affirmation animation
        Animation affirmationAnimationObject = AnimationBuilder.with(qiContext).withResources(R.raw.affirmation_a007).build();
        affirmationAnimation = AnimateBuilder.with(qiContext).withAnimation(affirmationAnimationObject).build();
        affirmationAnimation.addOnStartedListener(()->{
            //Pepper asks if the user has any disabilities
            Say askDisability = SayBuilder.with(qiContext).withText("Do you have any form of disability? I might adapt accordingly to it.").build();
            askDisability.run();
            //Then starts chatting with it
            future_chat = disability_chat.async().run();
        });

        // Build the goodbye animation
        Animation goodbyeAnimationObject = AnimationBuilder.with(qiContext).withResources(R.raw.goodbye).build();
        goodbye_animation = AnimateBuilder.with(qiContext).withAnimation(goodbyeAnimationObject).build();
        goodbye_animation.addOnStartedListener(() -> {
            Say goodbye = SayBuilder.with(qiContext).withText("Bye bye!").build();
            goodbye.async().run();
        });

        Animation gotItAnimationObject =  AnimationBuilder.with(qiContext).withResources(R.raw.left_hand_low_b001).build();
        gotItAnimation = AnimateBuilder.with(qiContext).withAnimation(gotItAnimationObject).build();
        gotItAnimation.addOnStartedListener(()->{
            if (globalVariables.getMute()) {
                Say gotIt = SayBuilder.with(qiContext).withText("Got it! Then pay attention to my movements and the tablet.").build();
                gotIt.run();
            } else if (globalVariables.getColorBlind()) {
                Say gotIt = SayBuilder.with(qiContext).withText("Colors will not be a problem! Every UI element will be easily distinguishable.").build();
                gotIt.run();
            } else if (globalVariables.getVisuallyImpaired()) {
                Say gotIt = SayBuilder.with(qiContext).withText("Ok, I'll make the text and buttons bigger.").build();
                gotIt.run();
            } else if(globalVariables.getBlind()) {
                Say gotIt = SayBuilder.with(qiContext).withText("Ok, then pay attention to my voice.").build();
                gotIt.run();
            } else if (!globalVariables.getDeaf()) {
                Say gotIt = SayBuilder.with(qiContext).withText("Fine, both touch and voice inputs are enabled.").build();
                gotIt.run();
            }
        });
    }

    private void initChat() {
        Topic disability_topic = TopicBuilder.with(qiContext).withResource(R.raw.disability).build();

        disability_chatBot = QiChatbotBuilder.with(qiContext).withTopic(disability_topic).build();

        disability_chat = ChatBuilder.with(qiContext).withChatbot(disability_chatBot).build();
        disability_chat.addOnStartedListener(() -> Log.i(TAG, "Chat started."));

        disability_chatBot.addOnEndedListener(endReason -> {
            future_chat.requestCancellation();
            if (endReason.equals("bye")) {
                goodbye_animation.async().run().andThenConsume(restart -> {
                    startActivity(new Intent(this, MainActivity.class));
                });
            } else {
                Intent changeActivity = new Intent(this, Information.class);
                changeActivity.putExtra("globalVariables", globalVariables);
                startActivity(changeActivity);
            }
        });
    }

    private void initUiElements() {
        final TextView textView = findViewById(R.id.textView2);
        final Button button_mute = findViewById(R.id.button_mute);
        final Button button_blind = findViewById(R.id.button_blind);
        final Button button_deaf = findViewById(R.id.button_deaf);
        final Button button_no = findViewById(R.id.button_none);
        final Button button_visually_impaired = findViewById(R.id.button_visually_impaired);
        final Button button_color_blind = findViewById(R.id.button_color_blind);

        button_mute.setOnClickListener(v -> {
            globalVariables.setMute(true);
            if (future_chat != null) {
                future_chat.requestCancellation();
            }
            gotItAnimationF = gotItAnimation.async().run();
            gotItAnimationF.andThenConsume(change->{
                Intent changeActivity = new Intent(this, Information.class);
                changeActivity.putExtra("globalVariables", globalVariables);
                startActivity(changeActivity);
            });
        });

        button_blind.setOnClickListener(v -> {
            //Need to be more precise
            fadeOutButton(button_mute);
            fadeOutButton(button_blind);
            fadeOutButton(button_deaf);
            fadeOutButton(button_no);
            textView.setText("Can you be more specific?");

            fadeInButton(button_visually_impaired);
            button_visually_impaired.setOnClickListener(w -> {
                globalVariables.setVisuallyImpaired(true);
                if (future_chat != null) {
                    future_chat.requestCancellation();
                }
                gotItAnimationF = gotItAnimation.async().run();
                gotItAnimationF.andThenConsume(change->{
                    Intent changeActivity = new Intent(this, Information.class);
                    changeActivity.putExtra("globalVariables", globalVariables);
                    startActivity(changeActivity);
                });
            });
            fadeInButton(button_color_blind);
            button_color_blind.setOnClickListener(w -> {
                globalVariables.setColorBlind(true);
                if (future_chat != null) {
                    future_chat.requestCancellation();
                }
                gotItAnimationF = gotItAnimation.async().run();
                gotItAnimationF.andThenConsume(change->{
                    Intent changeActivity = new Intent(this, Information.class);
                    changeActivity.putExtra("globalVariables", globalVariables);
                    startActivity(changeActivity);
                });
            });
        });

        button_deaf.setOnClickListener(v -> {
            if (future_chat != null) {
                future_chat.requestCancellation();
            }
            globalVariables.setDeaf(true);
            textView.setText("All information will be displayed on the tablet!");
            gotItAnimationF = gotItAnimation.async().run();
            gotItAnimationF.andThenConsume(change-> {
                Intent changeActivity = new Intent(this, Information.class);
                changeActivity.putExtra("globalVariables", globalVariables);
                startActivity(changeActivity);
            });
        });

        button_no.setOnClickListener(v -> {
            if (future_chat != null) {
                future_chat.requestCancellation();
            }
            gotItAnimationF = gotItAnimation.async().run();
            gotItAnimationF.andThenConsume(change->{
                Intent changeActivity = new Intent(this, Information.class);
                changeActivity.putExtra("globalVariables", globalVariables);
                startActivity(changeActivity);
            });
        });
    }

    //region Animation methods
    private void fadeOutButton(Button b) {
        b.animate()
                .alpha(0f) //Button becomes transparent
                .setDuration(200) //Set the length of the animation
                .setListener(new AnimatorListenerAdapter() {
                    //When the animation ends, make the button disappear
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        b.setVisibility(View.GONE);
                    }
                });
    }

    private void fadeInButton(Button b) {
        b.animate()
                .alpha(1f) //Button becomes transparent
                .setDuration(200) //Set the length of the animation
                .setListener(new AnimatorListenerAdapter() {
                    //When the animation ends, make the button disappear
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        b.setVisibility(View.VISIBLE);
                    }
                });
    }
    //endregion
}
