package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.AnimateBuilder;
import com.aldebaran.qi.sdk.builder.AnimationBuilder;
import com.aldebaran.qi.sdk.builder.EnforceTabletReachabilityBuilder;
import com.aldebaran.qi.sdk.builder.ListenBuilder;
import com.aldebaran.qi.sdk.builder.PhraseSetBuilder;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayPosition;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy;
import com.aldebaran.qi.sdk.object.actuation.Animate;
import com.aldebaran.qi.sdk.object.actuation.Animation;
import com.aldebaran.qi.sdk.object.actuation.EnforceTabletReachability;
import com.aldebaran.qi.sdk.object.conversation.Listen;
import com.aldebaran.qi.sdk.object.conversation.ListenResult;
import com.aldebaran.qi.sdk.object.conversation.PhraseSet;
import com.aldebaran.qi.sdk.object.conversation.Say;

public class MainActivity extends RobotActivity implements RobotLifecycleCallbacks {

    private QiContext qiContext;
    private static final String TAG = "Main_Activity";
    private boolean isInfoPointActive = false;

    //region Tablet Reachability variables
    private Button tabletReachabilitybutton;
    private EnforceTabletReachability enforceTabletReachability;
    private Future<Void> enforceTabletReachabilityFuture;
    private Say actionEndedSay;
    //endregion

    //region QiChatbot variables
    public boolean isGreetings = true;
    //endregion

    //region Animation variables
    private Animate greetingAnimation;
    private Animate goodbyeAnimation;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set type and position of speechbar https://android.aldebaran.com/sdk/doc/pepper-sdk/ch4_api/conversation/conversation_feedbacks.html
        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.OVERLAY);
        setSpeechBarDisplayPosition(SpeechBarDisplayPosition.TOP);
        //Set the current layout view to activity_main.xml
        setContentView(R.layout.activity_main);
        QiSDK.register(this, this);

        //Retrieve layout elements and add listeners
        initUiElements();
    }

    private void initUiElements() {
        tabletReachabilitybutton = (Button)findViewById(R.id.tablet_reachability_button);
        tabletReachabilitybutton.setOnClickListener(v -> {
             if (enforceTabletReachabilityFuture == null || enforceTabletReachabilityFuture.isDone()) {
                // The EnforceTabletReachability action is not running
                startEnforceTabletReachability();
            } else {
                // The EnforceTabletReachability is running
                enforceTabletReachabilityFuture.requestCancellation();
            }
        });
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
        //Initialize all the actions
        initActions();

        //Initialize chats

        //Initialize animations
        initAnimations();

    }

    private void initAnimations() {
        if(isGreetings){
            Animation greetingAnimationObject = AnimationBuilder.with(qiContext).withResources(R.raw.greet).build();
            greetingAnimation = AnimateBuilder.with(qiContext).withAnimation(greetingAnimationObject).build();
            //As soon as the animation starts, Pepper greets vocally the user
            greetingAnimation.addOnStartedListener(()->{
                Say greet = SayBuilder.with(qiContext).withText("Hi do you need any help?").build();
                greet.async().run();
            });
            //Execute animation
            Future<Void> greetingAnimateFuture = greetingAnimation.async().run();
            //Listen to human answer
            PhraseSet response = PhraseSetBuilder.with(qiContext).withTexts("Yes", "No").build(); //Create set of words to look for in user's answer
            Listen listen = ListenBuilder.with(qiContext).withPhraseSet(response).build();
            ListenResult result = listen.run();
            //If the response contains "yes"
            if( (result.getHeardPhrase().getText().toLowerCase()).equals("yes")){
                //Need to start an info point chat bot
                isInfoPointActive = true;
            }else if( (result.getHeardPhrase().getText().toLowerCase()).equals("no")){
                //No help is required, so we need to thank the user
                Animation goodbyeAnimationObject = AnimationBuilder.with(qiContext).withResources(R.raw.goodbye).build();
                goodbyeAnimation = AnimateBuilder.with(qiContext).withAnimation(goodbyeAnimationObject).build();
                goodbyeAnimation.addOnStartedListener(()->{
                    Say goodbye = SayBuilder.with(qiContext).withText("See you soon!").build();
                    goodbye.async().run();
                });
                Future<Void> goodByeAnimateFuture = goodbyeAnimation.async().run();
            }
        }

    }

    private void initActions() {
        enforceTabletReachability = EnforceTabletReachabilityBuilder.with(qiContext).build();
        String actionEndedText ="My movements are back to normal. Run the action again to see the difference." ;
        actionEndedSay = SayBuilder.with(qiContext).withText(actionEndedText).build();

    }

    @Override
    public void onRobotFocusLost() {
        this.qiContext = null;

        //Remove all the listeners
        if(greetingAnimation != null){
            greetingAnimation.removeAllOnStartedListeners();
        }
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        // The robot focus is refused.
    }

    private void startEnforceTabletReachability(){
        enforceTabletReachabilityFuture = enforceTabletReachability.async().run();

        enforceTabletReachabilityFuture.thenConsume(future->{
            actionEndedSay.run();
        });
    }

}
