package com.softbankrobotics.retaildemo.Utils;

import android.app.Application;
import android.content.res.AssetManager;
import android.util.Log;

import com.softbankrobotics.retaildemo.Status;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class XmlRessourceLoader {

    private final static String TAG = "MSI_XmlRessourceLoader";

    public static Void loadStockData(Application app, Status status) {
        Log.d(TAG,"Starting loadStocksData");
        AssetManager assetManager = app.getAssets();
        ArrayList<Item> items = new ArrayList<>();
        try (InputStream in = assetManager.open("Stocks.xml")) {
            XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserFactory.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,false);
            parser.setInput(in, null);
            int event = parser.getEventType();
            String itemName = "None";
            String itemPrice = "None";
            String itemRessource = "None";
            String upsellName = "None";
            HashMap<String,Boolean> itemStock = new HashMap<String,Boolean>();
            while (event!= XmlPullParser.END_DOCUMENT) {
                String tag = parser.getName();
                switch (event){
                    case XmlPullParser.START_TAG:
                        switch (tag){
                            case "itemsdata":
                                status.option1_1 = parser.getAttributeValue(null,"option1_1");
                                status.option1_2 = parser.getAttributeValue(null,"option1_2");
                                status.option2_1 = parser.getAttributeValue(null,"option2_1");
                                status.option2_2 = parser.getAttributeValue(null,"option2_2");
                                status.upsellPrice = parser.getAttributeValue(null,"upsellPrice");
                            case "item":
                                itemName = parser.getAttributeValue(null,"name");
                                itemPrice = parser.getAttributeValue(null,"price");
                                itemRessource = parser.getAttributeValue(null,"ressource");
                                upsellName = parser.getAttributeValue(null,"upsellname");
                                Log.d(TAG,"found new item : " +itemName +", " + itemPrice +", "+ itemRessource );
                                break;
                            case "attribute":
                                String name = parser.getAttributeValue(null,"name");
                                Boolean available = Boolean.parseBoolean(parser.getAttributeValue(null,"available"));
                                itemStock.put(name,available);
                                Log.d(TAG,"attribute : " + name + ", " +available);
                                break;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        switch (tag){
                            case "item":
                                Log.d(TAG,"new item");
                                items.add(new Item(itemName,itemPrice, itemRessource, itemStock,upsellName));
                                itemStock = new HashMap<>();
                                break;
                        }
                        break;
                }
                event = parser.next();
            }
        } catch (IOException | XmlPullParserException | NullPointerException e) {
            Log.d(TAG,"execption");
            e.printStackTrace();
        }
        status.stocks = items;
        return(null);
    }

    @SuppressWarnings("unchecked")
    public static HashMap<String,ArrayList<Item>> loadRetrunData(Application app) {
        Log.d(TAG,"Starting loadReturnData");
        AssetManager assetManager = app.getAssets();
        HashMap<String, ArrayList<Item>> tickets = new HashMap();
        ArrayList<Item> returnedItems = new ArrayList<>();
        try (InputStream in = assetManager.open("DataTickets.xml")) {
            XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserFactory.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,false);
            parser.setInput(in, null);
            int event = parser.getEventType();
            String ticketId = "None";
            while (event!= XmlPullParser.END_DOCUMENT) {
                String tag = parser.getName();
                switch (event){
                    case XmlPullParser.START_TAG:
                        switch (tag){
                            case "ticket":
                                Log.d(TAG,"updating ticket Id");
                                ticketId = parser.getAttributeValue(null,"id");
                                break;
                            case "item":
                                Log.d(TAG,"Adding item to the current ticket");
                                String name = parser.getAttributeValue(null,"name");
                                String price = parser.getAttributeValue(null,"price");
                                String imageId = parser.getAttributeValue(null,"imageId");
                                returnedItems.add(new Item(name,price,imageId));
                                break;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        switch (tag){
                            case "ticket":
                                Log.d(TAG,"New ticket");
                                tickets.put(ticketId,returnedItems);
                                returnedItems = new ArrayList<>();
                                break;
                        }
                        break;
                }
                event = parser.next();
            }
        } catch (IOException | XmlPullParserException | NullPointerException e) {
            e.printStackTrace();
        }
        return tickets;
    }
}