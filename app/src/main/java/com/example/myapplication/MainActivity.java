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
import com.aldebaran.qi.sdk.builder.ChatBuilder;
import com.aldebaran.qi.sdk.builder.ListenBuilder;
import com.aldebaran.qi.sdk.builder.PhraseSetBuilder;
import com.aldebaran.qi.sdk.builder.QiChatbotBuilder;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.builder.TopicBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayPosition;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy;
import com.aldebaran.qi.sdk.object.actuation.Animate;
import com.aldebaran.qi.sdk.object.actuation.Animation;
import com.aldebaran.qi.sdk.object.conversation.Chat;
import com.aldebaran.qi.sdk.object.conversation.Listen;
import com.aldebaran.qi.sdk.object.conversation.ListenResult;
import com.aldebaran.qi.sdk.object.conversation.PhraseSet;
import com.aldebaran.qi.sdk.object.conversation.QiChatbot;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.aldebaran.qi.sdk.object.conversation.Topic;

public class MainActivity extends RobotActivity implements RobotLifecycleCallbacks {

    private QiContext qiContext;
    private static final String TAG = "Main_Activity";

    //region Animation variables
    private Animate greeting_animation;
    private Animate goodbye_animation;
    public Future<Void> animation_future;
    private Animate animation;
    //endregion

    //region Chatbot variables
    private QiChatbot helpNeeded_qiChatbot;
    private Topic helpNeeded_topic;
    private Chat helpNeeded_chat;
    //endregion

    public Future<ListenResult> listen_result_future;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //Set type and position of speechbar https://android.aldebaran.com/sdk/doc/pepper-sdk/ch4_api/conversation/conversation_feedbacks.html
        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.OVERLAY);
        setSpeechBarDisplayPosition(SpeechBarDisplayPosition.TOP);
        //Set the current layout view to activity_main.xml
        setContentView(R.layout.activity_main);
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

        //Init chats and their relative components(Topics, Chatbots, etc)
        initChat();

        //Initialize animations
        initAnimations();

        //Pepper greets the approached user
        animation_future = greeting_animation.async().run();
        //After the animation is finished, wait for human input
        animation_future.andThenConsume(chatting ->{

            //Initialize UI elements
            initUiElements();
            //Create set of words to look for in user's answer
            PhraseSet response = PhraseSetBuilder.with(qiContext).withTexts("Si", "No").build();
            //Build asynchronously the Listen action
            Future<Listen> listen_future = ListenBuilder.with(qiContext).withPhraseSet(response).buildAsync();
            //Consume the listen action
            listen_future.andThenConsume(listen->{
                //The listen action is running, store the heard phrase inside an asynchronous object Future<ListenResult>
                listen_result_future = listen.async().run();
                //Consume the result of the listen action
                listen_result_future.andThenConsume(heard_phrase->{
                    //If what Pepper heard contains "yes"
                    if( (heard_phrase.getHeardPhrase().getText().toLowerCase()).equals("si")){
                        //User need helps, pass to another activity
                        Intent changeActivity = new Intent(this, Disability.class);
                        startActivity(changeActivity);
                    //Otherwise
                    }else if( (heard_phrase.getHeardPhrase().getText().toLowerCase()).equals("no")){
                        //No help is required, Pepper says goodbye
                        animation_future = goodbye_animation.async().run();
                        //A new user comes by - Restart scenario
                        animation_future.andThenConsume(restart->{
                            Log.i(TAG, "Interaction ended. Restarting.");
                            //Restart by starting this same activity
                            startActivity(new Intent(this, MainActivity.class));
                        });
                    }
                });
            });
        });
    }

    @Override
    public void onRobotFocusLost() {
        //Set the qiContext to null
        this.qiContext = null;

        //Remove all the listeners
        greeting_animation.removeAllOnStartedListeners();

        if (goodbye_animation != null){
            goodbye_animation.removeAllOnStartedListeners();
        }

        if (helpNeeded_chat != null){
            helpNeeded_chat.removeAllOnStartedListeners();
        }
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        // The robot focus is refused.
    }

    private void initTopics(){
        //Build the topic from resources
        helpNeeded_topic = TopicBuilder.with(qiContext).withResource(R.raw.approach).build();
    }

    private void initChat(){

        //Initialize first all the topics
        initTopics();

        //Build the qiChatbot
        helpNeeded_qiChatbot = QiChatbotBuilder.with(qiContext)
                                           .withTopic(helpNeeded_topic) // One topic per chatbot
                                           .build();

        //Build the chat
        helpNeeded_chat = ChatBuilder.with(qiContext)
                               .withChatbot(helpNeeded_qiChatbot) // Here we can pass multiple chatBots
                               .build();

        //Log if the chat started correctly
        helpNeeded_chat.addOnStartedListener(() -> Log.i(TAG, "Discussion started."));

        //Whenever the robot can not hear correctly, ask for repetition - Might not be required since voice inputs are not available
        helpNeeded_chat.addOnNoPhraseRecognizedListener(() -> {
            Animation AnimationObject = AnimationBuilder.with(qiContext).withResources(R.raw.confused_a001).build();
            animation = AnimateBuilder.with(qiContext).withAnimation(AnimationObject).build();
            Say repeat = SayBuilder.with(qiContext).withText("Mi dispiace, non ho capito. Puoi ripetere?").build();
            Future.waitAll(repeat.async().run(),animation.async().run());

        });
    }

    private void initAnimations() {

        //Build greeting animation
        Animation greetingAnimationObject = AnimationBuilder.with(qiContext).withResources(R.raw.hello_a010).build();
        greeting_animation = AnimateBuilder.with(qiContext).withAnimation(greetingAnimationObject).build();
        //As soon as the animation starts, Pepper greets vocally the user
        greeting_animation.addOnStartedListener(()->{
            Say greet = SayBuilder.with(qiContext).withText("Ciao! Sono Pepper, hai bisogno di aiuto?").build();
            greet.async().run();
            //speak("Ciao! Sono Pepper, hai bisogno di aiuto?",(float) 1.0, (float) 1.0);

        });

        //Build the goodbye animation
        Animation goodbyeAnimationObject = AnimationBuilder.with(qiContext).withResources(R.raw.goodbye).build();
        goodbye_animation = AnimateBuilder.with(qiContext).withAnimation(goodbyeAnimationObject).build();
        //As soon as the animation starts, Pepper says goodbye to the user
        goodbye_animation.addOnStartedListener(()->{
            Say goodbye = SayBuilder.with(qiContext).withText("Ci vediamo!").build();
            goodbye.async().run();
        });
    }

    private void initUiElements(){

        final TextView textView = (TextView)findViewById(R.id.textView2);

        final Button button_yes = findViewById(R.id.button_yes);
        button_yes.setOnClickListener(v -> {
            Intent changeActivity = new Intent(this, Disability.class);
            startActivity(changeActivity);
        });

        final Button button_no = findViewById(R.id.button_no);
        button_no.setOnClickListener(v->{
            button_yes.setVisibility(View.GONE);
            button_no.setVisibility(View.GONE);
            textView.setText("Ci vediamo!");
            //Stop listening
            listen_result_future.requestCancellation();
            //Help is refused - Restart scenario
            animation_future = goodbye_animation.async().run();
            animation_future.andThenConsume(restart->{
                Log.i(TAG, "Interaction ended. Restarting.");
                //Restart by starting this same activity
                startActivity(new Intent(this, MainActivity.class));
            });
        });
    }
}
