package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.ChatBuilder;
import com.aldebaran.qi.sdk.builder.QiChatbotBuilder;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.builder.TopicBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayPosition;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy;
import com.aldebaran.qi.sdk.object.conversation.Chat;
import com.aldebaran.qi.sdk.object.conversation.QiChatbot;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.aldebaran.qi.sdk.object.conversation.Topic;

public class Disability extends RobotActivity implements RobotLifecycleCallbacks {

    private QiContext qiContext;
    private boolean isMute = false;
    private boolean isDeaf = false;
    private boolean isBlind = false;
    private boolean physicalDisabilitied = false;

    private static final String TAG = "Disability";


    //region Tablet Reachability variables
    /*private Button tabletReachabilitybutton;
    private EnforceTabletReachability enforceTabletReachability;
    private Future<Void> enforceTabletReachabilityFuture;
    private Say actionEndedSay;*/
    //endregion

    private Chat disability_chat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        initUiElements();

        //Pepper asks if the user has any disabilities
        Future<Say> askDisability = SayBuilder.with(qiContext).withText("Prima dimmi se soffri di qualche disabilità. Mi adatterò al meglio delle mie possibilità!").buildAsync();
        askDisability.andThenConsume(say->{
            say.run();
            //Start chatting with the user
            disability_chat.async().run();
        });



    }

    @Override
    public void onRobotFocusLost() {

        if (disability_chat!=null){
            disability_chat.removeAllOnStartedListeners();
        }

    }

    @Override
    public void onRobotFocusRefused(String reason) {

    }

    /*private void initUiElements() {
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

    private void startEnforceTabletReachability(){
        enforceTabletReachabilityFuture = enforceTabletReachability.async().run();

        enforceTabletReachabilityFuture.thenConsume(future->{
            actionEndedSay.run();
        });
    }

    private void initActions() {
        enforceTabletReachability = EnforceTabletReachabilityBuilder.with(qiContext).build();
        String actionEndedText ="My movements are back to normal. Run the action again to see the difference." ;
        actionEndedSay = SayBuilder.with(qiContext).withText(actionEndedText).build();
    }*/

    private void initUiElements(){

        final Button button_mute = findViewById(R.id.button_mute);
        button_mute.setOnClickListener(v ->{
            isMute = true;
        });

        final Button button_blind = findViewById(R.id.button_blind);
        button_blind.setOnClickListener(v ->{
            isBlind = true;

        });


        final Button button_deaf = findViewById(R.id.button_deaf);
        button_deaf.setOnClickListener(v ->{
            isDeaf = true;

        });

        final Button button_other = findViewById(R.id.button_other);
        button_other.setOnClickListener(v ->{
            physicalDisabilitied = true;

        });
    }


    private void initChat(){

        Topic disability_topic = TopicBuilder.with(qiContext).withResource(R.raw.disability).build();

        QiChatbot disability_chatBot = QiChatbotBuilder.with(qiContext).withTopic(disability_topic).build();

        disability_chat = ChatBuilder.with(qiContext).withChatbot(disability_chatBot).build();
        disability_chat.addOnStartedListener(() -> Log.i(TAG, "Chat started."));
        disability_chat.addOnNoPhraseRecognizedListener(() -> {
            Say repeat = SayBuilder.with(qiContext).withText("Mi dispiace, non ho capito. Puoi ripetere?").build();
            repeat.async().run();
        });
    }
}