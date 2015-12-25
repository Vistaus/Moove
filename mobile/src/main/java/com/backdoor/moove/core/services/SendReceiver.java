package com.backdoor.moove.core.services;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

import com.backdoor.moove.core.interfaces.SendListener;

/**
 * Copyright 2015 Nazar Suhovich
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class SendReceiver extends BroadcastReceiver{

    private SendListener listener;

    public SendReceiver(SendListener listener){
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                if (listener != null) {
                    listener.messageSendResult(true);
                }
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                if (listener != null) {
                    listener.messageSendResult(false);
                }
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                if (listener != null) {
                    listener.messageSendResult(false);
                }
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                if (listener != null) {
                    listener.messageSendResult(false);
                }
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                if (listener != null) {
                    listener.messageSendResult(false);
                }
                break;
        }
    }
}
