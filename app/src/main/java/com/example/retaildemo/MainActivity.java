package com.example.retaildemo;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.ChatBuilder;
import com.aldebaran.qi.sdk.builder.QiChatbotBuilder;
import com.aldebaran.qi.sdk.builder.TopicBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy;
import com.aldebaran.qi.sdk.object.conversation.AutonomousReactionImportance;
import com.aldebaran.qi.sdk.object.conversation.AutonomousReactionValidity;
import com.aldebaran.qi.sdk.object.conversation.Bookmark;
import com.aldebaran.qi.sdk.object.conversation.Chat;
import com.aldebaran.qi.sdk.object.conversation.EditablePhraseSet;
import com.aldebaran.qi.sdk.object.conversation.Phrase;
import com.aldebaran.qi.sdk.object.conversation.QiChatExecutor;
import com.aldebaran.qi.sdk.object.conversation.QiChatVariable;
import com.aldebaran.qi.sdk.object.conversation.QiChatbot;
import com.aldebaran.qi.sdk.object.conversation.Topic;
import com.aldebaran.qi.sdk.object.conversation.TopicStatus;
import com.example.retaildemo.Barcode.BarcodeTracker;
import com.example.retaildemo.Executors.FragmentExecutor;
import com.example.retaildemo.Executors.StatusExecutor;
import com.example.retaildemo.Fragments.CollectConfirmFragment;
import com.example.retaildemo.Fragments.LoadingFragment;
import com.example.retaildemo.Fragments.MainMenuFragment;
import com.example.retaildemo.Fragments.ProductFragment;
import com.example.retaildemo.Fragments.ReturnMainFragment;
import com.google.android.gms.vision.barcode.Barcode;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends RobotActivity implements RobotLifecycleCallbacks,BarcodeTracker.BarcodeGraphicTrackerCallback {

    private static final String TAG = "MSI_MainActivity";

    private FragmentManager fragmentManager;
    private QiContext qiContext;
    private QiChatbot qiChatbot;
    private Map<String,Map<String, Bookmark>> bookmarks;
    private String previousFragment;
    private TopicStatus previousTopic;
    private List<Topic> topics;
    private List<EditablePhraseSet> dynamic;
    private Map<String, TopicStatus> topicStatuses;
    //private QiChatVariable qiGender;

    //these need to be updated wih the bookmark and topics names
    private final List<String> topicNames = Arrays.asList("collect","collectconfirm","concepts",
            "email","feedback","mainmenu","product","productmap","productselection","productupsell",
            "returnmain","returnproduct","returnreason");

    public Status status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"MainActivity onCreate");
        super.onCreate(savedInstanceState);
        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.OVERLAY);
        this.fragmentManager = getSupportFragmentManager();
        this.bookmarks = new HashMap<>();
        this.status = new Status(this);
        this.topicStatuses = new HashMap<>();
        this.topics = new ArrayList<>();
        this.dynamic = new ArrayList<>();
        QiSDK.register(this, this);
        setContentView(R.layout.activity_main);
    }

    public String getPreviousFragment(){
        return previousFragment;
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        Log.d(TAG, "onRobotFocusGained");
        this.qiContext = qiContext;
        runChat();
    }

    @Override
    public void onRobotFocusLost() {
        Log.d(TAG, "onRobotFocusLost");
        this.qiContext = null;
        QiSDK.unregister(this,this);
    }

    @Override
    public void onRobotFocusRefused(String reason) {

    }

    @Override
    protected void onDestroy() {
        QiSDK.unregister(this, this);
        super.onDestroy();
    }

    public void setFragment(Fragment fragment){
        previousFragment = fragment.getClass().getSimpleName();
        String topicName = previousFragment.toLowerCase().replace("fragment","");
        if(! (fragment instanceof LoadingFragment)){
            this.goToBookmarkNewTopic("init",topicName);
            Log.i(TAG,"going to bookmark init in : " + topicName);
        }
        if(fragment instanceof ProductFragment){
            if(status.getGender().equals("MALE")){
                //qiGender.async().setValue("MALE");
            }else {
                //qiGender.async().setValue("FEMALE");
            }
        }
        Log.i(TAG, "starting fragment Transaction for fragment : " + fragment.getClass().getSimpleName());
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.placholder, fragment,"currentFragment");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public android.support.v4.app.Fragment getFragment() {
        return fragmentManager.findFragmentByTag("currentFragment");
    }

    public void setFragment(Fragment fragment, String productString){
        Log.i(TAG,"product String : " + productString);
        this.status.productString = productString;
        this.setFragment(fragment);

    }

    public Integer getThemeId(){
        try {
            return getPackageManager().getActivityInfo(getComponentName(), 0).getThemeResource();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void runChat(){
        setTopics();
        this.qiChatbot = QiChatbotBuilder.with(qiContext).withTopics(topics).build();
        Chat chat = ChatBuilder.with(qiContext).withChatbot(qiChatbot).build();
        for(Topic t: qiChatbot.getTopics()){
            bookmarks.put(t.getName(), t.getBookmarks());
        }
        //qiGender = qiChatbot.variable("gender");
        setTopicStatuses();
        setupExecutors();
        setupDynamicPhrases();
        chat.async().run();
        setFragment(new MainMenuFragment());
        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.ALWAYS);
    }

    private void setTopics() {
        for(String s : this.topicNames){
            Topic tmpTop = TopicBuilder.with(qiContext).withResource(getResId(s,R.raw.class)).build();
            topics.add(tmpTop);
        }
    }

    private void setTopicStatuses() {
        for (Topic t: this.topics) {
            TopicStatus tmpStat = qiChatbot.topicStatus(t);
            tmpStat.setEnabled(false);
            topicStatuses.put(t.getName(),tmpStat);
        }
    }

    public String getProductString(){
        return status.productString;
    }

    public static int getResId(String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    protected void onResume() {
        super.onResume();
        this.setFragment(new LoadingFragment());
    }

    public void goToBookmarkInTopic(String bookmark, String topic){
        if (topicNames.contains(topic)) {
            if (!bookmark.equals("")) {
                Log.i(TAG, "going to String bookmark " + bookmark + " in topic : " + topic);
                Log.i(TAG, bookmarks.keySet().toString());
                qiChatbot.async().goToBookmark(bookmarks.get(topic).get(bookmark),
                        AutonomousReactionImportance.HIGH, AutonomousReactionValidity.IMMEDIATE);
            }
        } else {
            Log.e(TAG, "could not find bookmark: " + bookmark + " or topic: " + topic + " in bookmarkNames or topicNames");
        }
    }

    public void goToBookmarkNewTopic(String bookmark, String topic){
        //enabling new topic
        TopicStatus nextTopicStatus = topicStatuses.get(topic);
        if (nextTopicStatus != null) {
            nextTopicStatus.async().setEnabled(true);
        }
        //going to bookmark in topic
        goToBookmarkInTopic(bookmark, topic);
        //disabling previous topic
        if (previousTopic != null) {
            previousTopic.async().setEnabled(false);
        }
        previousTopic = nextTopicStatus;
    }

    public void setupExecutors() {
        Map<String, QiChatExecutor> executors = new HashMap<>();
        executors.put("FragmentExecutor", new FragmentExecutor(qiContext, this));
        executors.put("StatusExecutor", new StatusExecutor(qiContext, this));
        qiChatbot.setExecutors(executors);
    }

    public void setupDynamicPhrases(){
        dynamic.add(qiChatbot.dynamicConcept("product_0"));
        dynamic.add(qiChatbot.dynamicConcept("product_1"));
        dynamic.add(qiChatbot.dynamicConcept("product_2"));
        dynamic.add(qiChatbot.dynamicConcept("product_3"));
        dynamic.add(qiChatbot.dynamicConcept("product_4"));
    }

    //concept name must be "product_#"
    public void setDynamicProductName(String conceptName, String productName){
        Log.i(TAG,"Dynamic Index : " + conceptName.charAt(8));
        dynamic.get(Character.getNumericValue(conceptName.charAt(8))).async().addPhrases(Collections.singletonList(new Phrase(productName)));
    }

    public QiContext getQiContext() {
        return qiContext;
    }

    @Override
    public void onDetectedQrCode(Barcode barcode) {
        Log.i(TAG,"I can see the QRcode");
        try {
            if(getFragment() instanceof  ReturnMainFragment){
                status.currentTicket = barcode.displayValue;
                ReturnMainFragment rmf = (ReturnMainFragment) getFragment();
                rmf.updateProductList();
            }else{
                Log.d(TAG,"barcode"+ barcode.displayValue);
                status.currentTicket = barcode.displayValue;
                setFragment(new CollectConfirmFragment());
            }
        }catch (Exception e){
            Log.e(TAG,"wrong fragment needs update in Collect Fragment");
        }

        /*if (barcodeDetected.contactInfo.name.last != null) {

            barcode = barcodeDetected;
            Log.d(TAG, "BarcodeString : " + barcode.rawValue);
            //Log.d(TAG, "onDetectedQrCode url title: "+barcode.url.title);
            Log.d(TAG, "onDetectedQrCode url url: " + barcode.contactInfo.urls[0]);
            runOnUiThread(() -> {
                name.setText(barcode.contactInfo.name.last);
                reference.setText(barcode.contactInfo.title);
            });
            launchActivityWithExtra();
        } else {
            runOnUiThread(() -> {
                name.setText(R.string.no_barcode_captured);
                reference.setText(R.string.no_barcode_captured);
                Log.d(TAG, "onDetectedQrCode: Barcode is empty");
            });
        }*/
    }
}

