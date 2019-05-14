package com.example.retaildemo;

import android.util.Log;

import com.example.retaildemo.Utils.Item;
import com.example.retaildemo.Utils.XmlRessourceLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Status {

    private final String TAG = "MSI_ConversationStatus";

    public String gender;
    public Boolean sendEmail;
    public String email = "";
    public Integer estimatedAge;

    public String productString;
    public String productColor;
    public String upsellProductString = "";
    public String currentTicket = "";

    public String productOne;
    public String productTwo;
    public String productThree;
    public String productFour;

    public ArrayList<Item> stocks;

    //contains the different tickets with Items to return;
    public HashMap<String, ArrayList<Item>> dataReturned;

    //stock of each shoes.
    //private Map<String,Map<String,Boolean>> stocks = new HashMap<>();

    public Status(MainActivity mainActivity){
        gender = "unknown";
        sendEmail = false;
        estimatedAge = -1;
        stocks = XmlRessourceLoader.loadStockData(mainActivity.getApplication());
        dataReturned = XmlRessourceLoader.loadRetrunData(mainActivity.getApplication());
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        Log.i(TAG,"setting gender to : "+ gender);
        this.gender = gender;
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

    public Boolean getStock(String type, String color){
        Log.d(TAG,"getting the stock for type : "+type +" with color : "+color);
        for(Item i:stocks){
            if(i.getRessourceName().equals(type)){
                return i.getStock(color);
            }
        }
        return false;
    }
}
