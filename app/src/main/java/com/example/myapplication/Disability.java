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
    private Future<Void> affirmationAnimationF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        globalVariables = (GlobalVariables) getIntent().getSerializableExtra("globalVariables");
        //Set type and position of speechbar https://android.aldebaran.com/sdk/doc/pepper-sdk/ch4_api/conversation/conversation_feedbacks.html
        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.OVERLAY);
        setSpeechBarDisplayPosition(SpeechBarDisplayPosition.TOP);
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
                Intent changeActivity = new Intent(this, Information.class);
                changeActivity.putExtra("globalVariables", globalVariables);
                startActivity(changeActivity);
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
        if(affirmationAnimation != null){
            affirmationAnimation.removeAllOnStartedListeners();
        }
    }

    @Override
    public void onRobotFocusRefused(String reason) {
    }

    private void initAnimation(){

        Animation affirmationAnimationObject = AnimationBuilder.with(qiContext).withResources(R.raw.affirmation_a007).build();
        affirmationAnimation = AnimateBuilder.with(qiContext).withAnimation(affirmationAnimationObject).build();
        affirmationAnimation.addOnStartedListener(()->{
            //Pepper asks if the user has any disabilities
            Say askDisability = SayBuilder.with(qiContext).withText("Do you have any form of disability? I might adapt accordingly to it.").build();
            askDisability.run();
            //Then starts chatting with it
            future_chat = disability_chat.async().run();
        });
    }

    private void initChat() {

        Topic disability_topic = TopicBuilder.with(qiContext).withResource(R.raw.disability).build();

        disability_chatBot = QiChatbotBuilder.with(qiContext).withTopic(disability_topic).build();

        disability_chat = ChatBuilder.with(qiContext).withChatbot(disability_chatBot).build();
        disability_chat.addOnStartedListener(() -> Log.i(TAG, "Chat started."));

        disability_chatBot.addOnEndedListener(endReason -> {
            future_chat.requestCancellation();
            Intent changeActivity = new Intent(this, Information.class);
            changeActivity.putExtra("globalVariables", globalVariables);
            startActivity(changeActivity);
        });
    }

    private void initUiElements() {

        final TextView textView = (TextView) findViewById(R.id.textView2);
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
            Future<Say> memorized = SayBuilder.with(qiContext).withText("Got it! Then pay attention to my movements and the tablet.").buildAsync();
            memorized.andThenConsume(gotIt->{
                gotIt.async().run();
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
                Future<Say> memorized = SayBuilder.with(qiContext).withText("Ricevuto").buildAsync();
                memorized.andThenConsume(gotIt->{
                    gotIt.async().run();
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
                future_chat.requestCancellation();
                Future<Say> memorized = SayBuilder.with(qiContext).withText("Ricevuto").buildAsync();
                memorized.andThenConsume(gotIt->{
                    gotIt.async().run();
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
            textView.setText("Ricevuto!");
            Intent changeActivity = new Intent(this, Information.class);
            changeActivity.putExtra("globalVariables", globalVariables);
            startActivity(changeActivity);
        });

        button_no.setOnClickListener(v -> {
            if (future_chat != null) {
                future_chat.requestCancellation();
            }
            Future<Say> memorized = SayBuilder.with(qiContext).withText("Ok").buildAsync();
            memorized.andThenConsume(gotIt -> {
                gotIt.async().run();
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
