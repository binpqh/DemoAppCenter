package com.demosdk.Controls;

import android.util.Log;
import asim.sdk.sdksimdispenser.SimdispenserMain;
import com.demosdk.Helper;
import com.demosdk.Services.SimDispenserSevice;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SimDepenserControl  {
    private static List<SimdispenserMain> listSimDispenserMain;
    private static final String TIME_TO_RECYCE_CARD = "60000";
    private static final String  BAURATE = "115200";
    private static final SimDispenserSevice Service = new SimDispenserSevice() ;
    public SimDepenserControl() {
        listSimDispenserMain = Arrays.asList(new SimdispenserMain(),new SimdispenserMain() , new SimdispenserMain(), new SimdispenserMain());

    }


    public void getStatus(int locationDespencer){
        HashMap<String,Object> log = Service.getAllStatus(listSimDispenserMain.get(locationDespencer));
        Log.d("status sim : ", Helper.convertToJsonString(log));
    }
    /**
     *
     * @param locationDespencer location ( 1,2,3,4)
     * @param port (/dev/ttyS0)
     * @return true if open port success and else
     * Wanrnig  : need open port after do enything with Card Box
     */
    public boolean InitDispenser(int locationDespencer , String port ){
        HashMap<String,Object> status = listSimDispenserMain.get(locationDespencer).Init(TIME_TO_RECYCE_CARD,port,BAURATE);
        return (boolean) status.get("status");
    }
    /**
     *
     * @param locationDespencer
     * @return true if move card to read serial number success and else
     */
    public boolean moveCardToRead(int locationDespencer ) {
        return Service.moveCardToReadSerial(listSimDispenserMain.get(locationDespencer));
    }
    
    /**
     *
     * @param locationDespencer
     * @return true if move card to hole take card success and else
     */

    public boolean injectcard (int locationDespencer) {

        return Service.moveCardToReciverCard(listSimDispenserMain.get(locationDespencer));
    }
    /**
     *
     * @param locationDespencer
     * @return true if have card in read area Serial Number Sim and esle
     */

    public boolean checkSensorReadArea(int locationDespencer){
       return Service.getStatusReadIdArea(listSimDispenserMain.get(locationDespencer));
    }
    /**
     *
     * @param locationDespencer
     * @return true if have card in output Card and else
     */

    public boolean checkSensorOutPutCard(int locationDespencer) {
        return Service.getStatusReciverCard(listSimDispenserMain.get(locationDespencer));
    }
    /**
     *
     * @param locationDespencer
     * @return true if have card ready output in the box and else
     */

    public boolean checkcardInBox(int locationDespencer) {
        return Service.getStatusBoxCard(listSimDispenserMain.get(locationDespencer));
    }

    /**
     * Test method because don't have card for test Read
     * @param locationDespencer
     * @return String Serialnumber Card
     */
    public String readCCID(int locationDespencer){
        String CCID = (String) Service.readCCIDAndStatus(listSimDispenserMain.get(locationDespencer)).get("ICCIDSim");
        if(CCID != ""){
            return CCID.substring(2, CCID.length() - 2)
                    .replaceAll("(\\d)(\\d)", "$2$1");
        }
        return null;
    }

    /**
     *
     * @param locationDespencer
     * @return true if Reject card to Box error Card and else
     */
    public boolean rejectCardInReadArea(int locationDespencer){
        return Service.rejectCard(listSimDispenserMain.get(locationDespencer));
    }


}
