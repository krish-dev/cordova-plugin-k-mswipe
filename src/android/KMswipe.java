package in.co.indusnet.cordova.plugins.mswipe;

import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;

import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mswipetech.wisepad.sdk.MSWisepadController;
import com.mswipetech.sdk.network.MSGatewayConnectionListener;
import com.mswipetech.wisepad.sdk.data.CardData;
import com.mswipetech.wisepad.sdk.data.CardSaleResponseData;
import com.mswipetech.wisepad.sdk.data.LoginResponseData;
import com.mswipetech.wisepad.sdk.data.MSDataStore;
import com.mswipetech.wisepad.sdk.device.MSWisepadDeviceController;
import com.mswipetech.wisepad.sdk.device.MSWisepadDeviceControllerResponseListener;
import com.mswipetech.wisepad.sdk.device.WisePadConnection;
import com.mswipetech.wisepad.sdk.listeners.MSWisepadControllerResponseListener;

import java.util.ArrayList;
import java.util.Hashtable;


/**
* This class echoes a string called from JavaScript.
*/
public class KMswipe extends CordovaPlugin {

    private MSWisepadController msWisepadController = null;
    private MSWisepadDeviceController msWisepadDeviceController = null;
    public boolean IsMSWisepasConnectionServiceBound;

    private MSGatewayConnectionListener msGatewayConnectionListener;
    private MSWisepadDeviceControllerResponseListener msWisepadDeviceControllerResponseListener;
    private MSWisepadControllerResponseListener msWisepadControllerResponseListener;

    private Context context;
    private CallbackContext rootCallbackContext;

    private CardData cardInfo = null;
    private LoginResponseData marchentData = null;
    private JSONObject paymentInfo = null;

    private String RES_TYPE_GATEWAY = "GATEWAY_CONNECTION";
    private String RES_TYPE_MARCHENT_AUTHENTICATED = "MARCHENT_AUTHENTICATED";
    private String RES_TYPE_PAYMENT_WISEPAD_CONNECTION = "PAYMENT_WISEPAD_CONNECTION";
    private String RES_TYPE_PAYMENT_CARD_PROCESS = "PAYMENT_CARD_PROCESS";
    private String RES_TYPE_PAYMENT_CARD_PROCESS_RESULTS = "PAYMENT_CARD_PROCESS_RESULTS";
    private String RES_TYPE_PAYMENT_ERROR = "PAYMENT_ERROR";
    private String RES_TYPE_PAYMENT_APPROVED = "PAYMENT_APPROVED";
    private String RES_TYPE_PAYMENT_DISPLAY_TEXT = "PAYMENT_DISPLAY_TEXT";
    private String RES_TYPE_DEVICE_DISCONNECT = "DEVICE_DISCONNECT ";


    private ServiceConnection msWisepadDeviceControllerService = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            try {
                MSWisepadDeviceController.LocalBinder localBinder = (MSWisepadDeviceController.LocalBinder) service;
                msWisepadDeviceController = localBinder.getService();
                IsMSWisepasConnectionServiceBound = true;

                if(msWisepadDeviceController != null) {
                    msWisepadDeviceController.initMswipeWisepadDeviceController(msWisepadDeviceControllerResponseListener,true, false, true,
                            false, MSWisepadDeviceController.WisepadCheckCardMode.SWIPE_OR_INSERT);
                }
            }catch (Exception e) {

            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            msWisepadDeviceController = null;
            IsMSWisepasConnectionServiceBound = false;
        }
    };

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        context = this.cordova.getActivity().getApplicationContext();

        msWisepadDeviceControllerResponseListener = new MSWisepadDeviceControllerResponseListener() {
            @Override
            public void onReturnWisepadConnection(WisePadConnection wisePadConnection, BluetoothDevice bluetoothDevice) {
                sendPaymentCalback(RES_TYPE_PAYMENT_WISEPAD_CONNECTION, wisePadConnection.toString());
                if(wisePadConnection == WisePadConnection.WisePadConnection_FAIL_TO_START_BT) {
                    disconnect();
                }else if(wisePadConnection ==  WisePadConnection.WisePadConnection_BLUETOOTH_SWITCHEDOFF) {
                    disconnect();
                }
            }

            @Override
            public void onRequestWisePadCheckCardProcess(CheckCardProcess checkCardProcess, ArrayList<String> arrayList) {
                sendPaymentCalback(RES_TYPE_PAYMENT_CARD_PROCESS, checkCardProcess.toString());

                if(checkCardProcess == CheckCardProcess.CheckCardProcess_SET_AMOUNT) {
                    try {
                        msWisepadDeviceController.setAmount(paymentInfo.getString("amount"), TransactionType.PAYMENT);
                    }catch (JSONException e){

                    }

                }else if(checkCardProcess == CheckCardProcess.CheckCardProcess_SELECT_APPLICATION) {
                    msWisepadDeviceController.selectApplication(0);
                }

            }

            @Override
            public void onReturnWisePadOfflineCardTransactionResults(CheckCardProcessResults checkCardProcessResults, Hashtable<String, Object> hashtable) {
                sendPaymentCalback(RES_TYPE_PAYMENT_CARD_PROCESS_RESULTS, checkCardProcessResults.toString());

                if(checkCardProcessResults == CheckCardProcessResults.ON_REQUEST_ONLINEPROCESS) {
                    cardInfo = (CardData)hashtable.get("cardData");
                    try {
                        msWisepadController.processCardSaleOnline(
                                marchentData.getReferenceId(),
                                marchentData.getSessionTokeniser(),
                                paymentInfo.getString("amount"),
                                "0.00",
                                paymentInfo.getString("mobileNumber"),
                                paymentInfo.getString("invoiceNumber"),
                                paymentInfo.getString("email"),
                                paymentInfo.getString("notes"),
                                marchentData.isTipsRequired(),
                                marchentData.isReceiptRequired(),
                                "",
                                marchentData.getConveniencePercentage(),
                                marchentData.getServiceTax(),
                                paymentInfo.getString("extraNote1"),
                                paymentInfo.getString("extraNote2"),
                                paymentInfo.getString("extraNote3"),
                                paymentInfo.getString("extraNote4"),
                                paymentInfo.getString("extraNote5"),
                                paymentInfo.getString("extraNote6"),
                                paymentInfo.getString("extraNote7"),
                                paymentInfo.getString("extraNote8"),
                                paymentInfo.getString("extraNote9"),
                                paymentInfo.getString("extraNote10"),
                                msWisepadControllerResponseListener);
                    }catch (JSONException e) {

                    }

                }else if(checkCardProcessResults == CheckCardProcessResults.BAD_SWIPE ||
                        checkCardProcessResults == CheckCardProcessResults.USE_ICC_CARD ||
                        checkCardProcessResults == CheckCardProcessResults.NOT_ICC ||
                        checkCardProcessResults == CheckCardProcessResults.NO_CARD) {
                   if(msWisepadDeviceController != null && msWisepadDeviceController.getWisepadConnectionState() == WisePadConnection.WisePadConnection_CONNECTED) {
                       msWisepadDeviceController.checkCard(MSWisepadDeviceController.WisepadCheckCardMode.SWIPE_OR_INSERT);
                   }

                } else if(checkCardProcessResults == CheckCardProcessResults.CANCEL_CHECK_CARD ||
                        checkCardProcessResults == CheckCardProcessResults.TRANSACTION_CANCELED ||
                        checkCardProcessResults == CheckCardProcessResults.TRANSACTION_CARD_BLOCKED ||
                        checkCardProcessResults == CheckCardProcessResults.PIN_CANCEL ||
                        checkCardProcessResults == CheckCardProcessResults.PIN_INCORRECT_PIN ||
                        checkCardProcessResults == CheckCardProcessResults.PIN_TIMEOUT ||
                        checkCardProcessResults == CheckCardProcessResults.PIN_WRONG_PIN_LENGTH) {
                    disconnect();
                }

            }

            @Override
            public void onError(Error error, String s) {
                sendPaymentCalback(RES_TYPE_PAYMENT_ERROR, s);
            }

            @Override
            public void onRequestDisplayWispadStatusInfo(DisplayText displayText) {
                sendPaymentCalback(RES_TYPE_PAYMENT_DISPLAY_TEXT, displayText.toString());
            }

            @Override
            public void onReturnDeviceInfo(Hashtable<String, String> hashtable) {

            }

            @Override
            public void onReturnWispadNetwrokSettingInfo(WispadNetwrokSetting wispadNetwrokSetting, boolean b, Hashtable<String, Object> hashtable) {

            }

            @Override
            public void onReturnNfcDetectCardResult(NfcDetectCardResult nfcDetectCardResult, Hashtable<String, Object> hashtable) {

            }

            @Override
            public void onReturnNfcDataExchangeResult(boolean b, Hashtable<String, String> hashtable) {

            }
        };

        /**
         * MSwipe gateway listener.
         * It returns gateway connection status.
         * */
        msGatewayConnectionListener = new MSGatewayConnectionListener() {
            @Override
            public void Connected(String msg) {
                sendGatewayCallback(msg);
            }

            @Override
            public void Connecting(String msg) {
                sendGatewayCallback(msg);
            }

            @Override
            public void disConnect(String msg) {
                sendGatewayCallback(msg);
            }
        };

        msWisepadControllerResponseListener = new MSWisepadControllerResponseListener() {
            @Override
            public void onReponseData(MSDataStore msDataStore) {

                if(!msDataStore.getResponseStatus()) {
                    Log.v("K_FAILED",msDataStore.getResponseFailureReason());
                }

                if(msDataStore instanceof LoginResponseData) {
                    LoginResponseData loginResponseData = (LoginResponseData) msDataStore;
                    marchentData = loginResponseData;
                    sendAuthCallback(loginResponseData);
                }else if (msDataStore instanceof CardSaleResponseData) {

                    CardSaleResponseData cardSaleResponseData = (CardSaleResponseData) msDataStore;

                    if(cardSaleResponseData.getResponseStatus()) {
                        try {
                            JSONObject resObj = new JSONObject();
                            JSONObject trxData = new JSONObject()
                                    .put("cardLast4Digits",cardInfo.getLast4Digits())
                                    .put("bankName",cardSaleResponseData.getReceiptData().bankName)
                                    .put("merchantName",cardSaleResponseData.getReceiptData().merchantName)
                                    .put("merchantAdd",cardSaleResponseData.getReceiptData().merchantAdd)
                                    .put("dateTime",cardSaleResponseData.getReceiptData().dateTime)
                                    .put("mId",cardSaleResponseData.getReceiptData().mId)
                                    .put("tId",cardSaleResponseData.getReceiptData().tId)
                                    .put("batchNo",cardSaleResponseData.getReceiptData().batchNo)
                                    .put("voucherNo",cardSaleResponseData.getReceiptData().voucherNo)
                                    .put("refNo",cardSaleResponseData.getReceiptData().refNo)
                                    .put("saleType",cardSaleResponseData.getReceiptData().saleType)
                                    .put("cardNo",cardSaleResponseData.getReceiptData().cardNo)
                                    .put("trxType",cardSaleResponseData.getReceiptData().trxType)
                                    .put("cardType",cardSaleResponseData.getReceiptData().cardType)
                                    .put("expDate",cardSaleResponseData.getReceiptData().expDate)
                                    .put("emvSigExpDate",cardSaleResponseData.getReceiptData().emvSigExpDate)
                                    .put("cardHolderName",cardSaleResponseData.getReceiptData().cardHolderName)
                                    .put("currency",cardSaleResponseData.getReceiptData().currency)
                                    .put("cashAmount",cardSaleResponseData.getReceiptData().cashAmount)
                                    .put("baseAmount",cardSaleResponseData.getReceiptData().baseAmount)
                                    .put("tipAmount",cardSaleResponseData.getReceiptData().tipAmount)
                                    .put("totalAmount",cardSaleResponseData.getReceiptData().totalAmount)
                                    .put("authCode",cardSaleResponseData.getReceiptData().authCode)
                                    .put("rrNo",cardSaleResponseData.getReceiptData().rrNo)
                                    .put("certif",cardSaleResponseData.getReceiptData().certif)
                                    .put("appId",cardSaleResponseData.getReceiptData().appId)
                                    .put("appName",cardSaleResponseData.getReceiptData().appName)
                                    .put("tvr",cardSaleResponseData.getReceiptData().tvr)
                                    .put("tsi",cardSaleResponseData.getReceiptData().tsi)
                                    .put("appVersion",cardSaleResponseData.getReceiptData().appVersion)
                                    .put("isPinVarifed",cardSaleResponseData.getReceiptData().isPinVarifed)
                                    .put("stan",cardSaleResponseData.getReceiptData().stan)
                                    .put("cardIssuer",cardSaleResponseData.getReceiptData().cardIssuer)
                                    .put("emiPerMonthAmount",cardSaleResponseData.getReceiptData().emiPerMonthAmount)
                                    .put("total_Pay_Amount",cardSaleResponseData.getReceiptData().total_Pay_Amount)
                                    .put("noOfEmi",cardSaleResponseData.getReceiptData().noOfEmi)
                                    .put("interestRate",cardSaleResponseData.getReceiptData().interestRate)
                                    .put("billNo",cardSaleResponseData.getReceiptData().billNo)
                                    .put("firstDigitsOfCard",cardSaleResponseData.getReceiptData().firstDigitsOfCard)
                                    .put("isConvenceFeeEnable",cardSaleResponseData.getReceiptData().isConvenceFeeEnable)
                                    .put("invoiceNo",cardSaleResponseData.getReceiptData().invoiceNo)
                                    .put("trxDate",cardSaleResponseData.getReceiptData().trxDate)
                                    .put("trxImgDate",cardSaleResponseData.getReceiptData().trxImgDate)
                                    .put("chequeDate",cardSaleResponseData.getReceiptData().chequeDate)
                                    .put("chequeNo",cardSaleResponseData.getReceiptData().chequeNo);

                            resObj.put("type", RES_TYPE_PAYMENT_APPROVED);
                            resObj.put("message", cardSaleResponseData.getCardSaleApprovedMessage());
                            resObj.put("transactionInfo", trxData);

                            rootCallbackContext.success(resObj);

                        }catch (JSONException e) {
                            Log.v("K_ERROR", e.toString());
                        }

                    }else {
                        disconnect();
                        rootCallbackContext.error(cardSaleResponseData.getResponseFailureReason());
                    }
                }
            }
        };

        msWisepadController = MSWisepadController.getSharedMSWisepadController(context, MSWisepadController.GATEWAY_ENVIRONMENT.LABS, MSWisepadController.NETWORK_SOURCE.WIFI, msGatewayConnectionListener);

    }

    void sendGatewayCallback(String msg) {
        try{
            JSONObject resObj = new JSONObject();
            resObj.put("type", RES_TYPE_GATEWAY);
            resObj.put("message", msg);
            PluginResult result = new PluginResult(PluginResult.Status.OK, resObj);
            result.setKeepCallback(true);
            rootCallbackContext.sendPluginResult(result);
        }catch (JSONException e) {
            PluginResult result = new PluginResult(PluginResult.Status.ERROR, e.toString());
            result.setKeepCallback(true);
            rootCallbackContext.sendPluginResult(result);
            rootCallbackContext = null;
        }
    }

    void sendAuthCallback(LoginResponseData loginResponseData) {
        if(loginResponseData.getResponseStatus()) {
            try{
                JSONObject resObj = new JSONObject();
                JSONObject userObj = new JSONObject();

                userObj.put("firstName", loginResponseData.getFirstName());
                userObj.put("currency", loginResponseData.getCurrency());
                userObj.put("merchantName", loginResponseData.getMerchantName());
                userObj.put("merchantAccountNo", loginResponseData.getMerchantAccNo());
                userObj.put("referenceId", loginResponseData.getReferenceId());
                userObj.put("sessionToken", loginResponseData.getSessionTokeniser());
                userObj.put("isVoidEnabled", loginResponseData.isVoidEnabled());
                userObj.put("isTipsRequired", loginResponseData.isTipsRequired());
                userObj.put("isConvenienceFeesEnabled", loginResponseData.isConvenienceFeesEnabled());
                userObj.put("conveniencePercentage", loginResponseData.getConveniencePercentage());
                userObj.put("serviceTax", loginResponseData.getServiceTax());
                userObj.put("isReceiptRequired", loginResponseData.isReceiptRequired());
                userObj.put("isAdminUser", loginResponseData.isAdminUser());
                userObj.put("isPinBypass", loginResponseData.isPinBypass());
                userObj.put("isPinBasedLogin", loginResponseData.isPinBasedLogin());
                userObj.put("isRefundEnabled", loginResponseData.isRefundEnabled());
                userObj.put("isPreauthEnabled", loginResponseData.isPreauthEnabled());

                resObj.put("type", RES_TYPE_MARCHENT_AUTHENTICATED);
                resObj.put("message", loginResponseData.getResponseSuccessMessage());
                resObj.put("userInfo", userObj);

                PluginResult result = new PluginResult(PluginResult.Status.OK, resObj);
                result.setKeepCallback(true);
                rootCallbackContext.sendPluginResult(result);
            }catch (JSONException e) {
                PluginResult result = new PluginResult(PluginResult.Status.ERROR, e.toString());
                result.setKeepCallback(true);
                rootCallbackContext.sendPluginResult(result);
            }
        }else {
            rootCallbackContext.error(loginResponseData.getResponseFailureReason());
        }
    }

    void sendPaymentCalback(String type, String msg) {
        try{
            JSONObject resObj = new JSONObject();
            resObj.put("type", type);
            resObj.put("message", msg);
            PluginResult result = new PluginResult(PluginResult.Status.OK, resObj);
            result.setKeepCallback(true);
            rootCallbackContext.sendPluginResult(result);
        }catch (JSONException e) {
            PluginResult result = new PluginResult(PluginResult.Status.ERROR, e.toString());
            result.setKeepCallback(true);
            rootCallbackContext.sendPluginResult(result);
            rootCallbackContext = null;
        }
    }




    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        rootCallbackContext = callbackContext;
        JSONObject msgObject = args.getJSONObject(0);

        if (action.equals("pay")) {
            this.pay(msgObject, callbackContext);
            return true;
        } else if(action.equals("verifyMarchent")) {
            this.verifyMarchent(msgObject, callbackContext);
            return true;
        } else if(action.equals("disconnect")) {
            this.disconnect(RES_TYPE_DEVICE_DISCONNECT,callbackContext);
            return true;
        }

        return false;
    }

    private void verifyMarchent(JSONObject msgObject, CallbackContext callbackContext) {
        try {
            msWisepadController.authenticateMerchant(msgObject.getString("userId"),msgObject.getString("pin"), msWisepadControllerResponseListener);
        }catch (JSONException e) {
            callbackContext.error(e.toString());
        }
    }

    private void pay(JSONObject msgObject, CallbackContext callbackContext) throws JSONException {
        paymentInfo = new JSONObject();

        try {
            paymentInfo.put("amount", msgObject.getString("amount"));
            paymentInfo.put("mobileNumber", msgObject.getString("mobileNumber"));
            paymentInfo.put("invoiceNumber", msgObject.getString("invoiceNumber"));
            paymentInfo.put("email", msgObject.getString("email"));
            paymentInfo.put("notes", msgObject.getString("notes"));
            paymentInfo.put("extraNote1", msgObject.getString("extraNote1"));
            paymentInfo.put("extraNote2", msgObject.getString("extraNote2"));
            paymentInfo.put("extraNote3", msgObject.getString("extraNote3"));
            paymentInfo.put("extraNote4", msgObject.getString("extraNote4"));
            paymentInfo.put("extraNote5", msgObject.getString("extraNote5"));
            paymentInfo.put("extraNote6", msgObject.getString("extraNote6"));
            paymentInfo.put("extraNote7", msgObject.getString("extraNote7"));
            paymentInfo.put("extraNote8", msgObject.getString("extraNote8"));
            paymentInfo.put("extraNote9", msgObject.getString("extraNote9"));
            paymentInfo.put("extraNote10", msgObject.getString("extraNote10"));

            context.bindService(new Intent(context, MSWisepadDeviceController.class), msWisepadDeviceControllerService, Context.BIND_AUTO_CREATE);

        }catch (JSONException e) {
                callbackContext.error(e.toString());
        }
    }

    private void disconnect(String type, CallbackContext callbackContext) {
        try {
            if(msWisepadDeviceController != null) {
                msWisepadDeviceController.disconnect();
                context.unbindService(msWisepadDeviceControllerService);
                msWisepadDeviceController = null;
                try{
                    JSONObject resObj = new JSONObject();
                    resObj.put("type", type);
                    resObj.put("message", "");
                    callbackContext.success(resObj);
                    rootCallbackContext = null;
                }catch (JSONException e) {
                    callbackContext.error(e.toString());
                    rootCallbackContext = null;
                }
            }else {
                msWisepadDeviceController = null;
                try{
                    JSONObject resObj = new JSONObject();
                    resObj.put("type", type);
                    resObj.put("message", "");
                    callbackContext.success(resObj);
                    rootCallbackContext = null;
                }catch (JSONException e) {
                    callbackContext.error(e.toString());
                    rootCallbackContext = null;
                }
            }
        }catch (Exception e) {
            callbackContext.error(e.toString());
        }
    }

    private void disconnect() {
        try {
            if(msWisepadDeviceController != null) {
                msWisepadDeviceController.disconnect();
                context.unbindService(msWisepadDeviceControllerService);
                msWisepadDeviceController = null;
            }else {
                msWisepadDeviceController = null;
            }
        }catch (Exception e) { }
    }
}