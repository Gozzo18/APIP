package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

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
    //endregion

    //region Chatbot variables
    private QiChatbot helpNeeded_qiChatbot;
    private Topic helpNeeded_topic;
    private Chat helpNeeded_chat;
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

        //Initialize UI elements
        initUiElements();

        //Pepper greets the approached user
        animation_future = greeting_animation.async().run();
        //After the animation is finished, wait for human input
        animation_future.andThenConsume(chatting ->{
            PhraseSet response = PhraseSetBuilder.with(qiContext).withTexts("Si", "No").build(); //Create set of words to look for in user's answer
            Listen listen = ListenBuilder.with(qiContext).withPhraseSet(response).build();
            //Pepper start listening
            ListenResult result = listen.run();
            //If the response contains "yes"
            if( (result.getHeardPhrase().getText().toLowerCase()).equals("si")){
            //User need helps, Pepper start discussing with it
                helpNeeded_chat.async().run();
            //Otherwise
            }else if( (result.getHeardPhrase().getText().toLowerCase()).equals("no")){
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
            Say repeat = SayBuilder.with(qiContext).withText("Mi dispiace, non ho capito. Puoi ripetere?").build();
            repeat.async().run();
        });
    }

    private void initAnimations() {

        //Build greeting animation
        Animation greetingAnimationObject = AnimationBuilder.with(qiContext).withResources(R.raw.greet).build();
        greeting_animation = AnimateBuilder.with(qiContext).withAnimation(greetingAnimationObject).build();
        //As soon as the animation starts, Pepper greets vocally the user
        greeting_animation.addOnStartedListener(()->{
            Say greet = SayBuilder.with(qiContext).withText("Ciao! Sono Pepper, hai bisogno di aiuto?").build();
            greet.async().run();
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
        final Button button_yes = findViewById(R.id.button_yes);
        button_yes.setOnClickListener(v -> {
            Intent changeActivity = new Intent(this, Disability.class);
            startActivity(changeActivity);
        });

        /*final Button button_no = findViewById(R.id.button_no);
        button_no.setOnClickListener(v->{
            Future<Void> goodByeAnimateFuture = goodbyeAnimation.async().run();
            //Intent restart = new Intent(this, MainActivity.class);
            //startActivity(restart);
        });*/
    }


    /*public void sendMessage(View view){
        Log.e("myTag", "Entrato");
        Intent intent = new Intent(this, Main2Activity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        Log.e("message", message);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);

    }*/
}
