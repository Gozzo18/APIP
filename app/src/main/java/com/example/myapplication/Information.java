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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class Information extends RobotActivity implements RobotLifecycleCallbacks {

    private QiContext qiContext;
    private GlobalVariables globalVariables;

    private int previousRnd = -1;
    private boolean firstTime = true;
    private String[] jokes = {"I want to make a joke about Sodium, but… Na.",
                                "Why do cows wear bells? Because their horns don't work.",
                                    "A bicycle can't stand on its own because it is two-tired.",
                                        "Two peanuts walk into a bar, and one was a-salted.",
                                            "I tried to sue the airport for misplacing my luggage. I lost my case.",
                                                "If you’re waiting for the waiter at a restaurant, aren’t you the waiter?",
                                                    "There was a kidnapping at school… Don´t worry, he woke up.",
                                                        "What do you call an alligator with a vest? An investigator.",
                                                            "I would tell you a construction pun, but I’m still working on it.",
                                                                "I went to buy some camouflage trousers the other day but I couldn’t find any."};

    private String currentWeather = "";

    private Animate tabletfocusAnimation;
    private Future<Void> tabletfocusAnimationFuture;
    public Animate goodbyeAnimation;
    public Future<Void> goodbyeAnimationFuture;
    private Animate affirmationAnimation;
    private Animate humorAnimation;
    private Future<Void> humorAnimationFuture;
    private Animate timeAnimation;
    private Future<Void> timeAnimationFuture;

    private Future<ListenResult> listen_result_future;

    public TextView textView;
    public Button timeButton;
    public Button indicationButton;
    public Button humorButton;
    public Button weatherButton;
    public Button concludeInteraction;
    public ImageView weatherImage;

    public PhraseSet indications;
    public PhraseSet weather;
    public PhraseSet humour;
    public PhraseSet time;
    public PhraseSet endInteraction;
    public Listen listen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the global variables
        globalVariables = (GlobalVariables) getIntent().getSerializableExtra("globalVariables");

        // Set the type of the speechBar
        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.OVERLAY);

        // Set the current layout view
        if (globalVariables.getVisuallyImpaired()) {
            setContentView(R.layout.activity_information_xl);
        } else {
            setContentView(R.layout.activity_information);
        }

        timeButton = findViewById(R.id.time_button);
        indicationButton = findViewById(R.id.indication_button);
        humorButton = findViewById(R.id.humor_button);
        weatherButton = findViewById(R.id.weather_button);
        concludeInteraction = findViewById(R.id.end_interaction);
        textView = findViewById(R.id.textView3);
        weatherImage = findViewById(R.id.weather_image);

        retrieveWeather();

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

        indications = PhraseSetBuilder.with(qiContext).withTexts("map", "direction", "directions", "shop", "shops").build();
        weather = PhraseSetBuilder.with(qiContext).withTexts("weather", "outside", "sun", "cloud", "rain", "snow", "temperature").build();
        humour = PhraseSetBuilder.with(qiContext).withTexts("joke", "jokes", "make me laugh", "want to laugh", "something funny", "funny jokes").build();
        time = PhraseSetBuilder.with(qiContext).withTexts("time", "current time", "what time is it", "hour").build();
        endInteraction = PhraseSetBuilder.with(qiContext).withTexts("Goodbye", "Bye", "Bye bye", "See you", "Don't need help anymore", "Terminate", "Finish", "Stop").build();
        listen = ListenBuilder.with(qiContext).withPhraseSets(indications, weather, humour, time, endInteraction).build();

        initUiElements();
        initAnimations();

        affirmationAnimation.run();
    }

    @Override
    public void onRobotFocusLost() {
        //
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        //
    }

    private void retrieveWeather(){
        //Weather API from openWeather
        Weather.placeIdTask asyncTask =new Weather.placeIdTask(new Weather.AsyncResponse() {
            public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_pressure, String weather_updatedOn) {
                //We are interested only on the current weather, not on tempetarute, pressure and so on
                currentWeather = weather_description.substring(0,1).toUpperCase() + weather_description.substring(1);
            }
        });
        // Rome "latitude" and "longitude"
        asyncTask.execute("41.89", "12.48");
    }

    private void listen() {
        listen_result_future = listen.async().run();
        listen_result_future.andThenConsume(result -> {
            if (PhraseSetUtil.equals(result.getMatchedPhraseSet(), indications)) {
                runOnUiThread(new Runnable() { @Override public void run() { indicationButton.performClick(); }});
            } else if (PhraseSetUtil.equals(result.getMatchedPhraseSet(), weather)) {
                runOnUiThread(new Runnable() { @Override public void run() { weatherButton.performClick(); }});
            } else if (PhraseSetUtil.equals(result.getMatchedPhraseSet(), humour)) {
                runOnUiThread(new Runnable() { @Override public void run() { humorButton.performClick(); }});
            } else if (PhraseSetUtil.equals(result.getMatchedPhraseSet(), time)) {
                runOnUiThread(new Runnable() { @Override public void run() { timeButton.performClick(); }});
            } else if (PhraseSetUtil.equals(result.getMatchedPhraseSet(), endInteraction)) {
                listen_result_future.requestCancellation();
                goodbyeAnimationFuture = goodbyeAnimation.async().run();
                goodbyeAnimationFuture.andThenConsume(finished->{
                    Intent changeActivity = new Intent(this, MainActivity.class);
                    startActivity(changeActivity);
                });
            }
        });
    }

    private void initUiElements() {
        timeButton.setOnClickListener(v -> {
            if (listen_result_future != null) {
                listen_result_future.requestCancellation();
            }
            //Firstly launch the animation
            timeAnimationFuture = timeAnimation.async().run();
            timeAnimationFuture.andThenConsume(animation -> {
                //When the animation finishes, Pepper tells the time and shows it on screen
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM HH:mm", Locale.getDefault());
                String currentTime = sdf.format(new Date());
                //If the user is not deaf, Pepper also speaks
                if (!globalVariables.getDeaf()) {
                    Future<Say> saytime = SayBuilder.with(qiContext).withText("It's " + currentTime).buildAsync();
                    saytime.andThenConsume(time->{
                        time.async().run();
                        //After the speak action terminates, we update the UI with the time
                        runOnUiThread(new Runnable() {@Override public void run() { textView.setText("It's " + currentTime); }});
                        //If the user can speak, we relaunch the listening action for further information
                        if ( (!globalVariables.getMute() & !globalVariables.getBlind() & !globalVariables.getColorBlind() & !globalVariables.getDeaf() & !globalVariables.getVisuallyImpaired()) || (globalVariables.getBlind() ||globalVariables.getColorBlind() || globalVariables.getVisuallyImpaired())) {
                            listen();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {@Override public void run() { textView.setText("It's " + currentTime); }});
                }
            });
        });

        humorButton.setOnClickListener(v -> {
            if (listen_result_future != null){
                listen_result_future.requestCancellation();
            }
            humorAnimationFuture = humorAnimation.async().run();
            humorAnimationFuture.andThenConsume(animation->{
                //Relaunch listening
                if ( (!globalVariables.getMute() & !globalVariables.getBlind() & !globalVariables.getColorBlind() & !globalVariables.getDeaf() & !globalVariables.getVisuallyImpaired()) || (globalVariables.getBlind() ||globalVariables.getColorBlind() || globalVariables.getVisuallyImpaired())) {
                    listen();
                }
            });
        });

        indicationButton.setOnClickListener(v->{
            if (listen_result_future != null){
                listen_result_future.requestCancellation();
            }
            tabletfocusAnimationFuture = tabletfocusAnimation.async().run();
            tabletfocusAnimationFuture.andThenConsume(change->{
                Intent changeActivity = new Intent(this, Indications.class);
                changeActivity.putExtra("globalVariables", globalVariables);
                startActivity(changeActivity);
            });

        });

        weatherButton.setOnClickListener(v->{
            if (listen_result_future != null){
                listen_result_future.requestCancellation();
            }
            // Change appeareance of UI
            textView.setText(currentWeather);
            fadeOutButton(timeButton);
            fadeOutButton(indicationButton);
            fadeOutButton(humorButton);
            fadeOutButton(weatherButton);
            fadeOutButton(concludeInteraction);
            switch (currentWeather){
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

            if (!globalVariables.getDeaf()){
                String weatherMessage = "";
                switch (currentWeather){
                    case "Few clouds":
                    case "Scattered clouds":
                        weatherMessage = "There are very few clouds otuside, you won't need an umbrella.";
                        break;
                    case "Clear sky":
                        weatherMessage = "The sun is shining outside, I love the sun.";
                        break;
                    case "Broken clouds":
                        weatherMessage = "The sky is covered by clouds, you might need an umbrella later on.";
                        break;
                    case "Light rain":
                    case "Moderate rain":
                        weatherMessage = "At the moment is raining, better stay inside and wait for the rain to stop.";
                        break;
                }
                Future<Say> tellWeather = SayBuilder.with(qiContext).withText(weatherMessage).buildAsync();
                tellWeather.andThenConsume(sayAction->{
                    sayAction.async().run().andThenConsume(finished->{
                        if (!globalVariables.getMute()) {
                            listen();
                        }
                    });
                });
            }

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
            }, 2000);
        });

        concludeInteraction.setOnClickListener(v->{
            if (listen_result_future != null){
                listen_result_future.requestCancellation();
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

    private void initAnimations() {
        // Build the goodbye animation
        Animation goodbyeAnimationObject = AnimationBuilder.with(qiContext).withResources(R.raw.goodbye).build();
        goodbyeAnimation = AnimateBuilder.with(qiContext).withAnimation(goodbyeAnimationObject).build();
        goodbyeAnimation.addOnStartedListener(() -> {
            if (!globalVariables.getDeaf()){
                Say goodbye = SayBuilder.with(qiContext).withText("Have a great day, bye!").build();
                goodbye.async().run();
            }
        });

        // Affirmation animation
        String welcome_text;
        if(globalVariables.getBlind()){
            welcome_text="How can I help you? I can tell you directions, the weather, a joke or what time it is.";
        }
        else{
            welcome_text="How can I help you?";
        }
        Animation affirmationAnimationObject = AnimationBuilder.with(qiContext).withResources(R.raw.affirmation_a004).build();
        affirmationAnimation = AnimateBuilder.with(qiContext).withAnimation(affirmationAnimationObject).build();
        affirmationAnimation.addOnStartedListener(()->{
            if (!globalVariables.getDeaf()){
                Future<Say> askInformation = SayBuilder.with(qiContext).withText(welcome_text).buildAsync();
                askInformation.andThenConsume(say -> say.async().run().andThenConsume(consume -> {
                    if (!globalVariables.getMute()) {
                        listen();
                    }
                }));
            }
        });

        // Tablet focus animation
        Animation tabletfocusAnimationObject = AnimationBuilder.with(qiContext).withResources(R.raw.show_tablet_a004).build();
        tabletfocusAnimation = AnimateBuilder.with(qiContext).withAnimation(tabletfocusAnimationObject).build();
        if (!globalVariables.getDeaf()){
            tabletfocusAnimation.addOnStartedListener(()->{
                if (!globalVariables.getDeaf()) {
                    Say where = SayBuilder.with(qiContext).withText("Where do you wish to go?").build();
                    where.run();
                }
            });
        }

        Animation humorAnimationObject = AnimationBuilder.with(qiContext).withResources(R.raw.show_tablet_a002).build();
        humorAnimation = AnimateBuilder.with(qiContext).withAnimation(humorAnimationObject).build();
        humorAnimation.addOnStartedListener(()->{
            int rnd = new Random().nextInt(jokes.length);
            if (firstTime){
                previousRnd = rnd;
                firstTime = false;
            }else{
                while(previousRnd == rnd){
                    rnd = new Random().nextInt(jokes.length);
                }
                previousRnd = rnd;
            }
            String currentJoke = jokes[rnd];
            if (!globalVariables.getDeaf()){
                Say sayjoke = SayBuilder.with(qiContext).withText(currentJoke).build();
                sayjoke.async().run();
            }
            runOnUiThread(new Runnable() { @Override public void run() { textView.setText(currentJoke); }});

        });

        Animation timeAnimationObject = AnimationBuilder.with(qiContext).withResources(R.raw.check_time_right_b001).build();
        timeAnimation = AnimateBuilder.with(qiContext).withAnimation(timeAnimationObject).build();
    }



    private void fadeOutButton(Button b) {
        b.animate().alpha(0f).setDuration(0).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        b.setVisibility(View.GONE);
                    }
        });
    }

    private void fadeInButton(Button b) {
        b.animate().alpha(1f).setDuration(0).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        b.setVisibility(View.VISIBLE);
                    }
        });
    }

}
