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
import com.aldebaran.qi.sdk.builder.ChatBuilder;
import com.aldebaran.qi.sdk.builder.QiChatbotBuilder;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.builder.TopicBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayPosition;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy;
import com.aldebaran.qi.sdk.object.conversation.Chat;
import com.aldebaran.qi.sdk.object.conversation.QiChatVariable;
import com.aldebaran.qi.sdk.object.conversation.QiChatbot;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.aldebaran.qi.sdk.object.conversation.Topic;

public class Disability extends RobotActivity implements RobotLifecycleCallbacks {

    private static final String TAG = "Disability";
    private QiContext qiContext;

    private QiChatbot disability_chatBot;
    public QiChatVariable disability_type;
    private Chat disability_chat;
    public Future<Void> future_chat;


    //region Tablet Reachability variables
    /*private Button tabletReachabilitybutton;
    private EnforceTabletReachability enforceTabletReachability;
    private Future<Void> enforceTabletReachabilityFuture;
    private Say actionEndedSay;*/
    //endregion

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
        askDisability.andThenConsume(say -> {
            say.run();
            //Start chatting with the user
            disability_type = disability_chatBot.variable("disability_type");
            //Catch the type of disability
            disability_type.addOnValueChangedListener(currentValue -> {
                Log.i(TAG, "Disability type: " + currentValue);
                switch (currentValue) {
                    case "cieco":
                        //User is completely blind - Only voice from now on
                        ((GlobalVariables) this.getApplication()).setBlind(true);
                        Log.i(TAG, "isBlind: " + ((GlobalVariables) this.getApplication()).getBlind());
                        break;
                    case "ipovedente":
                        //User not so blind - Voice and pinch in
                        ((GlobalVariables) this.getApplication()).setAlmostBlind(true);
                        Log.i(TAG, "isAlmostBlind: " + ((GlobalVariables) this.getApplication()).getAlmostBlind());
                        break;
                    case "daltonico":
                        //Pay attention to color palette
                        ((GlobalVariables) this.getApplication()).setColorBlind(true);
                        Log.i(TAG, "isColorBlind: " + ((GlobalVariables) this.getApplication()).getColorBlind());
                        break;
                    case "sordo":
                        //User is deaf - Avoid use voice
                        ((GlobalVariables) this.getApplication()).setDeaf(true);
                        Log.i(TAG, "isDeaf: " + ((GlobalVariables) this.getApplication()).getDeaf());
                        break;
                    case "menomato":
                        //User has a physical disability - Might be impaired in movement
                        ((GlobalVariables) this.getApplication()).setPhysicallyDisabled(true);
                        Log.i(TAG, "isPhysicallyDisabled: " +  ((GlobalVariables) this.getApplication()).getPhysicallyDisabled());
                        break;
                }
            });
            future_chat = disability_chat.async().run();
            //NEED TO ADD INSIDE THE TOPIC A COMMAND TO END THE CHAT AND MOVE TO THE NEXT ACTIVITY
            //startActivity(new Intent(this, Indications.class));
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

    private void initChat() {

        Topic disability_topic = TopicBuilder.with(qiContext).withResource(R.raw.disability).build();

        disability_chatBot = QiChatbotBuilder.with(qiContext).withTopic(disability_topic).build();

        disability_chat = ChatBuilder.with(qiContext).withChatbot(disability_chatBot).build();
        disability_chat.addOnStartedListener(() -> Log.i(TAG, "Chat started."));
        disability_chat.addOnNoPhraseRecognizedListener(() -> {
            Say repeat = SayBuilder.with(qiContext).withText("Mi dispiace, non ho capito. Puoi ripetere?").build();
            repeat.async().run();
        });

        disability_chatBot.addOnEndedListener(endReason -> {
            Log.i(TAG, "qichatbot end reason = " + endReason);
            future_chat.requestCancellation();
        });
    }

    private void initUiElements() {

        final TextView textView = (TextView) findViewById(R.id.textView2);

        final Button button_mute = findViewById(R.id.button_mute);
        final Button button_blind = findViewById(R.id.button_blind);
        final Button button_deaf = findViewById(R.id.button_deaf);
        final Button button_other = findViewById(R.id.button_other);
        final Button button_almostBlind = findViewById(R.id.button_almostBlind);
        final Button button_colorBlind = findViewById(R.id.button_colorblind);

        button_mute.setOnClickListener(v -> {
            ((GlobalVariables) this.getApplication()).setMute(true);
            Log.i(TAG, "isMute: " + ((GlobalVariables) this.getApplication()).getMute());
            future_chat.requestCancellation();
            //THIS CRAHSES THE APP! FIX IT
            Say memorized = SayBuilder.with(qiContext).withText("Ricevuto").build();
            memorized.async().run();
            startActivity(new Intent(this, Indications.class));
        });

        button_blind.setOnClickListener(v -> {
            //Need to be more precise
            fadeOutButton(button_mute);
            fadeOutButton(button_blind);
            fadeOutButton(button_deaf);
            fadeOutButton(button_other);
            textView.setText("Puoi essere più specifico?");
            fadeInButton(button_almostBlind);
            button_almostBlind.setOnClickListener(w -> {
                ((GlobalVariables) this.getApplication()).setAlmostBlind(true);
                Log.i(TAG, "isAlmostBlind: " + ((GlobalVariables) this.getApplication()).getAlmostBlind());
                future_chat.requestCancellation();
                startActivity(new Intent(this, Indications.class));
            });
            fadeInButton(button_colorBlind);
            button_colorBlind.setOnClickListener(w -> {
                ((GlobalVariables) this.getApplication()).setColorBlind(true);
                Log.i(TAG, "isColorBlind: " + ((GlobalVariables) this.getApplication()).getColorBlind());
                future_chat.requestCancellation();
                Say memorized = SayBuilder.with(qiContext).withText("Ricevuto").build();
                memorized.async().run();
                startActivity(new Intent(this, Indications.class));
            });
        });

        button_deaf.setOnClickListener(v -> {
            ((GlobalVariables) this.getApplication()).setDeaf(true);
            Log.i(TAG, "isDeaf: " + ((GlobalVariables) this.getApplication()).getDeaf());
            future_chat.requestCancellation();
            textView.setText("Ricevuto!");
            startActivity(new Intent(this, Indications.class));
        });

        button_other.setOnClickListener(v -> {
            ((GlobalVariables) this.getApplication()).setPhysicallyDisabled(true);
            Log.i(TAG, "isPhysicallyDisabled: " +  ((GlobalVariables) this.getApplication()).getPhysicallyDisabled());
            future_chat.requestCancellation();
            Say memorized = SayBuilder.with(qiContext).withText("Ricevuto").build();
            memorized.async().run();
            startActivity(new Intent(this, Indications.class));
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
