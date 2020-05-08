package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.util.LogPrinter;
import android.widget.Button;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.ChatBuilder;
import com.aldebaran.qi.sdk.builder.EnforceTabletReachabilityBuilder;
import com.aldebaran.qi.sdk.builder.QiChatbotBuilder;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.builder.TopicBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayPosition;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy;
import com.aldebaran.qi.sdk.object.actuation.EnforceTabletReachability;
import com.aldebaran.qi.sdk.object.conversation.Chat;
import com.aldebaran.qi.sdk.object.conversation.QiChatbot;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.aldebaran.qi.sdk.object.conversation.Topic;

public class MainActivity extends RobotActivity implements RobotLifecycleCallbacks {

    private QiContext qiContext;
    private static final String TAG = "Main_Activity";

    //region Tablet Reachability variables
    private Button tabletReachabilitybutton;
    private EnforceTabletReachability enforceTabletReachability;
    private Future<Void> enforceTabletReachabilityFuture;
    private Say actionEndedSay;
    //endregion

    //region QiChatbot variables
    public boolean isGreetings = true;
    public boolean isMainDiscussion = false;
    private QiChatbot qiChatbot;
    private Topic greetingTopic;
    private Chat greetingChat;
    private Future<Void> greetingChatFuture;
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
        initChat();

    }

    private void initChat() {
        if(isGreetings){
            greetingTopic = TopicBuilder.with(qiContext).withResource(R.raw.greetings).build();
            qiChatbot = QiChatbotBuilder.with(qiContext).withTopic(greetingTopic).build();
            greetingChat = ChatBuilder.with(qiContext).withChatbot(qiChatbot).build();
            greetingChat.addOnStartedListener(()-> Log.i(TAG, "Discussion started."));
            //Start greetings chat
            startGreetingsChat();
            isGreetings = false;
        }else if(isMainDiscussion){
            //TO-DO
            //Main topic of discussion
        }else{
            //TO-DO
            //Regards
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
        if(greetingChat != null){
            greetingChat.removeAllOnStartedListeners();
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

    private void startGreetingsChat(){
        greetingChatFuture = greetingChat.async().run();
        greetingChatFuture.thenConsume(future->{
            if (future.hasError()) {
                Log.e(TAG, "Discussion finished with error.", future.getError());
            }
        });
    }
}
