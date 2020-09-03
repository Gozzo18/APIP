package com.example.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
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
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy;
import com.aldebaran.qi.sdk.object.actuation.Animate;
import com.aldebaran.qi.sdk.object.actuation.Animation;
import com.aldebaran.qi.sdk.object.conversation.Chat;
import com.aldebaran.qi.sdk.object.conversation.QiChatbot;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.aldebaran.qi.sdk.object.conversation.Topic;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class Information extends RobotActivity implements RobotLifecycleCallbacks {

    private static final String TAG = "Information";
    private QiContext qiContext;

    private GlobalVariables globalVariables;

    private QiChatbot information_chatBot;
    private Chat information_chat;
    public Future<Void> future_chat;

    private Animate affirmationAnimation;
    private Future<Animate> humor_animation;
    private Future<Animate> time_animation;

    public Animate goodbyeAnimation;
    public Future<Void> goodbyeAnimationFuture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        globalVariables = (GlobalVariables) getIntent().getSerializableExtra("globalVariables");

        // Set the type of the speechBar
        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.OVERLAY);

        // Set the current layout view
        if (globalVariables.getVisuallyImpaired()) {
            setContentView(R.layout.activity_information_XL);
        } else {
            setContentView(R.layout.activity_information);
        }

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

        initTerminateAnimation();

        //User has no disability - Both chat and animation are active
        if (!globalVariables.getMute() & !globalVariables.getBlind() & !globalVariables.getColorBlind() & !globalVariables.getDeaf() & !globalVariables.getVisuallyImpaired()){
            initChat();
            initAnimations();
            affirmationAnimation.run();
        }else if (globalVariables.getMute()){
            //User can't talk, no chat
            initAnimations();
            affirmationAnimation.run();
        }

    }

    @Override
    public void onRobotFocusLost() {
        if (information_chat != null){
            information_chat.removeAllOnStartedListeners();
        }
        if (information_chatBot != null){
            information_chatBot.removeAllOnEndedListeners();
        }
    }

    @Override
    public void onRobotFocusRefused(String reason) {
    }

    private void initUiElements(){
        final Button timeButton = findViewById(R.id.time_button);
        final Button indicationButton = findViewById(R.id.indication_button);
        final Button humorButton = findViewById(R.id.humor_button);
        final Button weatherButton = findViewById(R.id.weather_button);
        final Button concludeInteraction = findViewById(R.id.end_interaction);
        final TextView textView = (TextView)findViewById(R.id.textView3);
        final ImageView weatherImage = (ImageView)findViewById(R.id.weather_image);

        final String[] jokes = {"Joke 1", "Joke 2", "Joke 3", "Joke n"};

        timeButton.setOnClickListener(v->{
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            textView.setText(sdf.format(new Date()));
            time_animation.andThenConsume(animation->{
                animation.async().run();
            });

        });

        indicationButton.setOnClickListener(v->{
            if (future_chat != null){
                future_chat.requestCancellation();
            }
            Future<Say> prepareNextActivity = SayBuilder.with(qiContext).withText("Where do you wish to go?").buildAsync();
            prepareNextActivity.andThenConsume(say->{
                Animation AnimationObject = AnimationBuilder.with(qiContext).withResources(R.raw.show_tablet_a004).build();
                affirmationAnimation = AnimateBuilder.with(qiContext).withAnimation(AnimationObject).build();
                Future.waitAll(say.async().run(), affirmationAnimation.async().run());
                Thread.sleep(1000);
                Intent changeActivity = new Intent(this, Indications.class);
                changeActivity.putExtra("globalVariables", globalVariables);
                startActivity(changeActivity);
            });
        });

        humorButton.setOnClickListener(v->{
            int rnd = new Random().nextInt(jokes.length);
            textView.setText(jokes[rnd]);
            humor_animation.andThenConsume(animation->{
                animation.async().run();
            });
        });

        weatherButton.setOnClickListener(v->{

            //Weather API from openWeather
            Weather.placeIdTask asyncTask =new Weather.placeIdTask(new Weather.AsyncResponse() {
                public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_pressure, String weather_updatedOn) {

                    //We are interested only on the current weather, not on tempetarute, pressure and so on
                    String current_weather = weather_description.substring(0,1).toUpperCase() + weather_description.substring(1);
                    textView.setText(current_weather);
                    fadeOutButton(timeButton);
                    fadeOutButton(indicationButton);
                    fadeOutButton(humorButton);
                    fadeOutButton(weatherButton);
                    fadeOutButton(concludeInteraction);
                    switch (current_weather){
                        case "Few clouds":
                        case "Scattered clouds":
                            weatherImage.setImageResource(R.drawable.cloud);
                            weatherImage.setVisibility(View.VISIBLE);
                            break;
                        case "Clear sky":
                            weatherImage.setImageResource(R.drawable.sun);
                            weatherImage.setVisibility(View.VISIBLE);
                            break;
                        case "Broken clouds":
                            weatherImage.setImageResource(R.drawable.nuvoloso);
                            weatherImage.setVisibility(View.VISIBLE);
                            break;
                        case "Light rain":
                        case "Moderate rain":
                            weatherImage.setImageResource(R.drawable.rain);
                            weatherImage.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            });
            //ROME "latitude" and "longitude"
            asyncTask.execute("41.89", "12.48");

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    weatherImage.setVisibility(View.GONE);
                    fadeInButton(timeButton);
                    fadeInButton(indicationButton);
                    fadeInButton(humorButton);
                    fadeInButton(weatherButton);
                    fadeInButton(concludeInteraction);
                    textView.setText("Anything else?");
                }
            }, 3000);
        });

        concludeInteraction.setOnClickListener(v->{
            if (future_chat != null){
                future_chat.requestCancellation();
            }
            fadeOutButton(timeButton);
            fadeOutButton(indicationButton);
            fadeOutButton(humorButton);
            fadeOutButton(weatherButton);
            fadeOutButton(concludeInteraction);
            textView.setText("Bye bye!");
            goodbyeAnimationFuture = goodbyeAnimation.async().run();
            goodbyeAnimationFuture.andThenConsume(finished->{
                startActivity(new Intent(this, MainActivity.class));
            });
        });
    }

    private void initTerminateAnimation(){
        Animation goodbyeAnimationObject = AnimationBuilder.with(qiContext).withResources(R.raw.goodbye).build();
        goodbyeAnimation = AnimateBuilder.with(qiContext).withAnimation(goodbyeAnimationObject).build();
        // As soon as the animation starts, Pepper says goodbye to the user
        goodbyeAnimation.addOnStartedListener(() -> {
            Say goodbye = SayBuilder.with(qiContext).withText("Have a great day, bye!").build();
            goodbye.async().run();
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

    private void initAnimations(){

        Animation affirmationAnimationObject = AnimationBuilder.with(qiContext).withResources(R.raw.affirmation_a004).build();
        affirmationAnimation = AnimateBuilder.with(qiContext).withAnimation(affirmationAnimationObject).build();
        affirmationAnimation.addOnStartedListener(()->{
            Future<Say> askInformation = SayBuilder.with(qiContext).withText("How can I help you?").buildAsync();
            askInformation.andThenConsume(say->{
                initUiElements();
                if (!globalVariables.getMute()){
                    future_chat = information_chat.async().run();
                }
            });
        });

        Animation humorAnimationObject = AnimationBuilder.with(qiContext).withResources(R.raw.show_tablet_a002).build();
        humor_animation = AnimateBuilder.with(qiContext).withAnimation(humorAnimationObject).buildAsync();

        Animation timeAnimationObject = AnimationBuilder.with(qiContext).withResources(R.raw.check_time_right_b001).build();
        time_animation = AnimateBuilder.with(qiContext).withAnimation(timeAnimationObject).buildAsync();

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