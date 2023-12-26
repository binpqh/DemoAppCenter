package com.demosdk.DeviceModule;

import android.content.Context;
import android.util.Log;
import com.demosdk.Controls.SimDepenserControl;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class Despenser extends ReactContextBaseJavaModule {

    private static final SimDepenserControl CONTROL = new SimDepenserControl();
    public final Context context;
    public Despenser(ReactApplicationContext reactContext) {
        super(reactContext);
        this.context = reactContext.getApplicationContext();
    }
    @ReactMethod
    public void initDespenser(Integer index , String port,Promise promise){
        boolean status = CONTROL.InitDispenser(index, port);
        promise.resolve(status);
    }
    @ReactMethod
    public void checkReadArea(Integer index , Promise promise){
        boolean status = CONTROL.checkSensorReadArea(index);
        promise.resolve(status);
    }
    @ReactMethod
    public void checkOutPut(Integer index , Promise promise){
        boolean status = CONTROL.checkSensorOutPutCard(index);
        promise.resolve(status);
    }
    @ReactMethod
    public void moveCard(Integer index,Promise promise){
        boolean status = CONTROL.moveCardToRead(index);
        promise.resolve(status);
    }
    @ReactMethod
    public void outCard(Integer index,Promise promise){
        boolean status = CONTROL.injectcard(index);
        promise.resolve(status);
    }
    @ReactMethod
    public void rejectCard(Integer index,Promise promise){
        boolean status = CONTROL.rejectCardInReadArea(index);
        promise.resolve(status);
    }
    @ReactMethod
    public void readSerial(Integer index,Promise promise){
        String serial = CONTROL.readCCID(index);
        promise.resolve(serial);
    }



    @ReactMethod
    public void GetCard() throws InterruptedException {
        int[]index = {1 };
        int[]total ={10};
        boolean type = true; // default return Sim
       for(int i = 0 ; i < index.length ;i++ )
       {

           for (int j = 0 ; j < total[i] ;)
           {
               boolean Check;
               if(type){
                   Check = ReturnSim(index[i]);
               }else {
                   Check = ReturnCard(index[i]);
               }
               if(Check){
                   //cout check for user get card
                  int wait = 0 ;
                  while (CONTROL.checkSensorOutPutCard(index[i])|| wait != 5){
                      TimeUnit.SECONDS.sleep(3);
                      Log.d("Warning: ", "Take Card !!");
                      if(wait == 4 ){
                        CONTROL.rejectCardInReadArea(index[i]);
                      }
                      wait++;
                  }
                  j++;
               }

           }
       }
    }
    @ReactMethod
    public boolean ReturnSim(int index) throws InterruptedException {
        //init dispenser for test
       boolean Init = CONTROL.InitDispenser(index, "/dev/ttyS0");

        // reject sim if have sim in read Area of Take Sim Area
       if( CONTROL.checkSensorReadArea(index) || CONTROL.checkSensorOutPutCard(index))
       {
           Log.d("Warning : ", "Area Read CCID Have Card Move Card To Recycle !!");
           CONTROL.rejectCardInReadArea(index);
       }
        //move card to read Area Read CCID
        if (CONTROL.moveCardToRead(index)) {
                //Read CCID if read False return if have CCID continue
                String CCIDSIm = CONTROL.readCCID(index);
            if (CCIDSIm != null) {
                // call function InitSim With para CCID for if success OutCard
                boolean checkInnit = GetAPIInitSim(CCIDSIm);
                if (checkInnit) {
                    Log.d("Success: ", "Out Card Ok !!");
                    return CONTROL.injectcard(index);
                }
            }
        }
        // move card to Recycle if something wrong !!
        CONTROL.rejectCardInReadArea(index);
        Log.d("False: ", "Out Card False !!");
        return false;
    }

    public boolean ReturnCard(int index) {
        //init dispenser for test
        boolean Init = CONTROL.InitDispenser(index, "/dev/ttyS0");

        // reject card if have sim in read Area of Take Sim Area
        if( CONTROL.checkSensorReadArea(index) || CONTROL.checkSensorOutPutCard(index))
        {
            Log.d("Warning : ", "Area Read CCID Have Card Move Card To Recycle !!");
            CONTROL.rejectCardInReadArea(index);
        }

        if (CONTROL.moveCardToRead(index)) {
                    Log.d("Success: ", "Out Card Ok !!");
                    return CONTROL.injectcard(index);
            }

        // move card to Recycle if something wrong !!
        CONTROL.rejectCardInReadArea(index);
        Log.d("False: ", "Out Card False !!");
        return false;

    }

    public boolean GetAPIInitSim(String Serial) throws InterruptedException {
        Log.d("String CCID Sim  ", " "+ Serial);
        TimeUnit.SECONDS.sleep(3);
        return true;
    }
    @NotNull
    @Override
    public String getName() {
        return "Despenser";
    }
}
