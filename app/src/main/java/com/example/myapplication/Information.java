package com.example.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class Information extends RobotActivity implements RobotLifecycleCallbacks {

    private static final String TAG = "Information";
    private QiContext qiContext;

    Handler myHandler;

    private QiChatbot information_chatBot;
    public QiChatVariable information_type;
    private Chat information_chat;
    public Future<Void> future_chat;

    private Animate animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set type and position of speechbar https://android.aldebaran.com/sdk/doc/pepper-sdk/ch4_api/conversation/conversation_feedbacks.html
        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.OVERLAY);
        setSpeechBarDisplayPosition(SpeechBarDisplayPosition.TOP);
        //Set the current layout view to activity_main.xml
        setContentView(R.layout.activity_information);
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

        Animation greetingAnimationObject = AnimationBuilder.with(qiContext).withResources(R.raw.affirmation_a004).build();
        animation = AnimateBuilder.with(qiContext).withAnimation(greetingAnimationObject).build();

        Future<Say> askInformation = SayBuilder.with(qiContext).withText("Come posso aiutarti?").buildAsync();
        askInformation.andThenConsume(say->{
            Future.waitAll(say.async().run(),animation.async().run());
            initUiElements();
            future_chat = information_chat.async().run();
        });
    }

    @Override
    public void onRobotFocusLost() {
        information_chat.removeAllOnStartedListeners();
        information_chatBot.removeAllOnEndedListeners();
    }

    @Override
    public void onRobotFocusRefused(String reason) {

    }

    private void initUiElements(){
        final Button timeButton = findViewById(R.id.time_button);
        final Button indicationButton = findViewById(R.id.indication_button);
        final Button humorButton = findViewById(R.id.humor_button);
        final Button weatherButton = findViewById(R.id.weather_button);
        final TextView textView = (TextView)findViewById(R.id.textView3);
        final ImageView weatherImage = (ImageView)findViewById(R.id.weather_image);

        Animation humorAnimationObject = AnimationBuilder.with(qiContext).withResources(R.raw.show_tablet_a002).build();
        Animate humor_animation = AnimateBuilder.with(qiContext).withAnimation(humorAnimationObject).build();

        Animation timeAnimationObject = AnimationBuilder.with(qiContext).withResources(R.raw.check_time_right_b001).build();
        Future<Animate> time_animation = AnimateBuilder.with(qiContext).withAnimation(timeAnimationObject).buildAsync();

        final String[] jokes = {"Perché la bambina è caduta dall'altalena? Perché non aveva le braccia!",
                                "Come fa una mucca senza labbra? Uuuuuuuuuuuuuuu.",
                                "Come fa una pecora ubriaca? Beeeeeeeeeeeecks."};

        final String[] weather = {"É nuvoloso", "C'è il sole", "Poco nuvoloso", "Sta piovendo"};

        timeButton.setOnClickListener(v->{


            time_animation.andThenConsume(animate -> {
                animate.async().run();
                Thread.sleep(2000);
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                Say time_say =SayBuilder.with(qiContext).withText("Ora sono le "+ sdf.format(new Date()).split(" ")[1]).build() ;
                time_say.async().run();
                textView.setText(sdf.format(new Date()));


            });

            //SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            //textView.setText(sdf.format(new Date()));
            //Say time_say =SayBuilder.with(qiContext).withText("Ora sono le "+ sdf.format(new Date()).split(" ")[1]).build() ;
            //time_say.async().run();

        });

        indicationButton.setOnClickListener(v->{
            future_chat.requestCancellation();


            Future<Say> prepareNextActivity = SayBuilder.with(qiContext).withText("Dove vuoi andare?").buildAsync();
            prepareNextActivity.andThenConsume(say->{
                Animation AnimationObject = AnimationBuilder.with(qiContext).withResources(R.raw.show_tablet_a004).build();
                animation = AnimateBuilder.with(qiContext).withAnimation(AnimationObject).build();
                Future.waitAll(say.async().run(),animation.async().run());
                Thread.sleep(1500);
                startActivity(new Intent(this, Indications.class));
            });
        });

        humorButton.setOnClickListener(v->{

            humor_animation.async().run();

            int rnd = new Random().nextInt(jokes.length);
            textView.setText(jokes[rnd]);
            textView.setTextSize(40);
        });

        weatherButton.setOnClickListener(v->{
            int rnd = new Random().nextInt(weather.length);
            textView.setText(weather[rnd]);
            fadeOutButton(timeButton);
            fadeOutButton(indicationButton);
            fadeOutButton(humorButton);
            fadeOutButton(weatherButton);

            switch (rnd){
                case 0:
                    weatherImage.setImageResource(R.drawable.cloud);
                    weatherImage.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    weatherImage.setImageResource(R.drawable.sun);
                    weatherImage.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    weatherImage.setImageResource(R.drawable.nuvoloso);
                    weatherImage.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    weatherImage.setImageResource(R.drawable.rain);
                    weatherImage.setVisibility(View.VISIBLE);
                    break;
            }

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    weatherImage.setVisibility(View.GONE);
                    fadeInButton(timeButton);
                    fadeInButton(indicationButton);
                    fadeInButton(humorButton);
                    fadeInButton(weatherButton);
                    textView.setText("Posso fare altro?");
                }
            }, 3000);

        });
    }

    private void initChat() {

        Topic information_topic = TopicBuilder.with(qiContext).withResource(R.raw.small_talk).build();

        information_chatBot = QiChatbotBuilder.with(qiContext).withTopic(information_topic).build();

        information_chat = ChatBuilder.with(qiContext).withChatbot(information_chatBot).build();
        information_chat.addOnStartedListener(() -> Log.i(TAG, "Chat started."));
        information_chat.addOnNoPhraseRecognizedListener(() -> {
            Say repeat = SayBuilder.with(qiContext).withText("Mi dispiace, non ho capito. Puoi ripetere?").build();
            repeat.async().run();
        });

        information_chatBot.addOnEndedListener(endReason -> {
            Log.i(TAG, "qichatbot end reason = " + endReason);
            future_chat.requestCancellation();
            startActivity(new Intent(this, Indications.class));
        });
    }

    //region Animation methods
    private void fadeOutButton(Button b) {
        b.animate()
                .alpha(0f) //Button becomes transparent
                .setDuration(0) //Set the length of the animation
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
                .setDuration(100) //Set the length of the animation
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