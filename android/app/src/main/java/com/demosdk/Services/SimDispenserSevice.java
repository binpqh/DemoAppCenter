
package com.demosdk.Services;

import asim.sdk.sdksimdispenser.SimdispenserMain;
import java.util.*;

public class SimDispenserSevice
{
    private static final String TIME_TO_RECYCE_CARD = "60000";
    private static final String  BAURATE = "115200";

    public HashMap<String, Object> initSimDispenser(String port) {
        return new SimdispenserMain().Init(TIME_TO_RECYCE_CARD,port,BAURATE);
    }
    public HashMap<String, Object> getAllStatus(SimdispenserMain dispenser) {
        HashMap<String,Object> getAllStatus =  SimdispenserMain.m_control.controlGetStatus(dispenser);
        HashMap<String,Object> statusSensor = (HashMap<String, Object>) getAllStatus.get("sensorStatus");
        return  statusSensor;
    }
    public boolean getStatusBoxCard(SimdispenserMain dispenser){
       HashMap<String, Object> getStatusResult = getAllStatus(dispenser);
       return getStatusResult.get("card box") == "have card";
   }
    public boolean getStatusReadIdArea(SimdispenserMain dispenser) {
        HashMap<String, Object> allSensor = getAllStatus(dispenser);
        return  allSensor.get("sensor3") == "have card";
    }
    public boolean getStatusReciverCard(SimdispenserMain dispenser){
        HashMap<String, Object> allSensor = getAllStatus(dispenser);
        return allSensor.get("sensor1") == "have card";
    }
    public boolean moveCardToReadSerial(SimdispenserMain dispenser){
        return  SimdispenserMain.m_control.controlICCardPosiion(dispenser);
    }
    public boolean moveCardToReciverCard(SimdispenserMain dispenser) {
        return SimdispenserMain.m_control.controlClipPosiion(dispenser);
    }
    public boolean rejectCard(SimdispenserMain dispenser) {
       return SimdispenserMain.m_control.controlRetainCard(dispenser);
    }
    public HashMap<String, Object> readCCIDAndStatus(SimdispenserMain dispenser){
        return SimdispenserMain.m_control.controlReadICCIDSim(dispenser);
    }
}
