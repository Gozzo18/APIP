package com.example.myapplication;

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

public class MainActivity extends RobotActivity implements RobotLifecycleCallbacks {

    private QiContext qiContext;
    private static final String TAG = "MainActivity";

    // Global variables
    private GlobalVariables globalVariables;

    // Animation variables
    private Animate greeting_animation;
    private Animate goodbye_animation;
    public Future<Void> animation_future;

    public Future<ListenResult> listen_result_future;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the type of the speechbar
        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.OVERLAY);

        // Set the current layout view to activity_main.xml
        setContentView(R.layout.activity_main);

        // Register the RobotLifecycleCallbacks for this activity
        QiSDK.register(this, this);
    }

    @Override
    protected void onDestroy() {
        // Unregister the RobotLifecycleCallbacks for this activity
        QiSDK.unregister(this, this);

        super.onDestroy();
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        this.qiContext = qiContext;

        // Initialize global variables
        this.globalVariables = new GlobalVariables();

        // Create the sets of words to look for in user's answer
        PhraseSet phrase_set_yes = PhraseSetBuilder.with(qiContext).withTexts("Yes", "Yea", "Yup", "Yeah", "Ok").build();
        PhraseSet phrase_set_no = PhraseSetBuilder.with(qiContext).withTexts("No", "Nope", "Nah", "Nada").build();
        PhraseSet phrase_set_bye = PhraseSetBuilder.with(qiContext).withTexts("Goodbye", "Bye", "Bye bye", "See you").build();

        // Build the listen action
        Listen listen = ListenBuilder.with(qiContext).withPhraseSets(phrase_set_yes, phrase_set_no, phrase_set_bye).build();

        // Initialize animations
        initAnimations();

        // Initialize UI elements
        initUiElements();

        // Pepper greets the approached user
        animation_future = greeting_animation.async().run();

        animation_future.andThenConsume(consumer -> {
            // After the animation is finished, wait for human input
            listen_result_future = listen.async().run();

            // Consume the listen action
            listen_result_future.andThenConsume(result -> {
                if (PhraseSetUtil.equals(result.getMatchedPhraseSet(), phrase_set_yes)) {
                    //User need helps, pass to another activity
                    Intent changeActivity = new Intent(this, Disability.class);
                    changeActivity.putExtra("globalVariables", globalVariables);
                    startActivity(changeActivity);
                } else if (PhraseSetUtil.equals(result.getMatchedPhraseSet(), phrase_set_no) ||
                           PhraseSetUtil.equals(result.getMatchedPhraseSet(), phrase_set_bye)) {
                    listen_result_future.requestCancellation();
                    // No help is required, Pepper says goodbye
                    animation_future = goodbye_animation.async().run();
                    // A new user comes by - restart scenario
                    animation_future.andThenConsume(restart -> {
                        Log.i(TAG, "Interaction ended. Restarting.");
                        // Restart by starting this same activity
                        startActivity(new Intent(this, MainActivity.class));
                    });
                }
            });
        });
    }

    @Override
    public void onRobotFocusLost() {
        // Set the qiContext to null
        this.qiContext = null;

        // Remove all the listeners
        greeting_animation.removeAllOnStartedListeners();
        goodbye_animation.removeAllOnStartedListeners();
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        // The robot focus is refused
    }



    private void initAnimations() {
        // Build greeting animation
        Animation greetingAnimationObject = AnimationBuilder.with(qiContext).withResources(R.raw.hello_a010).build();
        greeting_animation = AnimateBuilder.with(qiContext).withAnimation(greetingAnimationObject).build();
        // As soon as the animation starts, Pepper greets vocally the user
        greeting_animation.addOnStartedListener(() -> {
            Say greet = SayBuilder.with(qiContext).withText("Hi, I'm Pepper. Do you need help?").build();
            greet.async().run();
        });

        // Build the goodbye animation
        Animation goodbyeAnimationObject = AnimationBuilder.with(qiContext).withResources(R.raw.goodbye).build();
        goodbye_animation = AnimateBuilder.with(qiContext).withAnimation(goodbyeAnimationObject).build();
        // As soon as the animation starts, Pepper says goodbye to the user
        goodbye_animation.addOnStartedListener(() -> {
            Say goodbye = SayBuilder.with(qiContext).withText("Bye!").build();
            goodbye.async().run();
        });
    }

    private void initUiElements() {
        final TextView textView = (TextView) findViewById(R.id.textView2);

        final Button button_yes = findViewById(R.id.button_yes);
        button_yes.setOnClickListener(v -> {
            // Stop listening
            if (listen_result_future != null) {
                listen_result_future.requestCancellation();
            }

            Intent changeActivity = new Intent(this, Disability.class);
            changeActivity.putExtra("globalVariables", globalVariables);
            startActivity(changeActivity);
        });

        final Button button_no = findViewById(R.id.button_no);
        button_no.setOnClickListener(v -> {
            // Stop listening
            if (listen_result_future != null) {
                listen_result_future.requestCancellation();
            }

            // Hide the buttons and set the text
            button_yes.setVisibility(View.GONE);
            button_no.setVisibility(View.GONE);
            textView.setText("Bye!");

            // Help is refused - restart scenario
            animation_future = goodbye_animation.async().run();
            animation_future.andThenConsume(restart -> {
                Log.i(TAG, "Interaction ended. Restarting.");
                //Restart by starting this same activity
                Intent changeActivity = new Intent(this, MainActivity.class);
                startActivity(changeActivity);
            });
        });
    }

}
