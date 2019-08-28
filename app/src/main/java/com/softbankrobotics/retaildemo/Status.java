package com.softbankrobotics.retaildemo;

import android.util.Log;

import com.softbankrobotics.retaildemo.Utils.Item;
import com.softbankrobotics.retaildemo.Utils.XmlRessourceLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class Status {

    private final String TAG = "MSI_ConversationStatus";

    public String gender;
    public Boolean sendEmail;
    public String email = "";
    public Integer estimatedAge;
    public String templateEmail;

    public String productString;
    public String productColor;
    public String upsellProductString = "";
    public String currentTicket = "";

    public String product11;
    public String product12;
    public String product21;
    public String product22;

    public String option1_1 = "";
    public String option1_2 = "";
    public String option2_1 = "";
    public String option2_2 = "";
    public String upsellPrice = "";

    public Boolean promotion = false;

    public int lockerNumber = 3;

    public ArrayList<Item> stocks;

    //contains the different tickets with Items to return;
    public HashMap<String, ArrayList<Item>> dataReturned;

    public Status(MainActivity mainActivity){
        gender = "unknown";
        sendEmail = false;
        estimatedAge = -1;
        XmlRessourceLoader.loadStockData(mainActivity.getApplication(),this);

        product11 = stocks.get(3).getRessourceName();
        product12 = stocks.get(1).getRessourceName();
        product21 = stocks.get(2).getRessourceName();
        product22 = stocks.get(0).getRessourceName();

        dataReturned = XmlRessourceLoader.loadRetrunData(mainActivity.getApplication());
        templateEmail = readEmail(mainActivity);
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        Log.d(TAG,"setting gender to : "+ gender);
        this.gender = gender;
    }

    public void reset(){
        gender = "unknown";
        sendEmail = false;
        estimatedAge = -1;
    }

    public ArrayList<Item> getTicket(String ticketId){
        return dataReturned.get(ticketId);
    }

    public Boolean getSendEmail() {
        return sendEmail;
    }

    public void setSendEmail(Boolean sendEmail) {
        this.sendEmail = sendEmail;
    }

    public Integer getEstimatedAge() {
        return estimatedAge;
    }

    public void setEstimatedAge(Integer estimatedAge) {
        this.estimatedAge = estimatedAge;
    }

    public String getPrice(String type){
        Log.d(TAG,"getting price for type : "+type );
        for(Item i:stocks){
            if(i.getRessourceName().equals(type)){
                return i.getPrice();
            }
        }
        return "00.00";
    }
    public String getName(String type){
        Log.d(TAG,"getting price for type : "+type );
        for(Item i:stocks){
            if(i.getRessourceName().equals(type)){
                return i.getName();
            }
        }
        return "Unknown";
    }

    public Boolean getStock(String type, String color){
        Log.d(TAG,"getting the stock for type : "+type +" with color : "+color);
        for(Item i:stocks){
            if(i.getRessourceName().equals(type)){
                return i.getStock(color);
            }
        }
        return false;
    }

    private String readEmail(MainActivity mainActivity){
        String email = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(mainActivity.getAssets().open("EmailTemplate.html")));
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                email += mLine.trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d(TAG,email);
        return email;
    }
}
