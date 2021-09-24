package com.netmania.checklod.util;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class Utility {

    public static final byte BYTE_CMD_RECORD	= (byte)0xC1;

    public static final UUID CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    // public static final UUID RX_SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    // public static final UUID RX_CHAR_UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
    // public static final UUID TX_CHAR_UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");

    public static final UUID RX_SERVICE_UUID = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    public static final UUID RX_CHAR_UUID = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb"); // 10,12,13
    public static final UUID TX_CHAR_UUID = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");

    public static final int GATT_COMMAND_STX = 0x63;

    public static String getThreadSignature() {
        Thread t = Thread.currentThread();
        long l = t.getId();
        String name = t.getName();
        long p = t.getPriority();
        String gname = t.getThreadGroup().getName();
        return (name + ":(id)" + l + ":(priority)" + p + ":(group)" + gname);
    }

    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public static int convertTemp(byte[] adv, int bcnExTempPtr) {
        byte[] bytes = {adv[bcnExTempPtr + 1], adv[bcnExTempPtr]};
        short s = ByteBuffer.wrap(bytes).getShort();
        int iTemp = (int)s;
        return iTemp;
    }

    public static int convertSeq(byte[] adv, int bcnExTempPtr) {
        ByteBuffer buf = ByteBuffer.allocate(2);
        buf.put(adv[bcnExTempPtr]);
        buf.put(adv[bcnExTempPtr + 1]);
        int parsedNum = (((int) buf.array()[1] & 0xff) << 8) | (((int) buf.array()[0] & 0xff));;
        buf.clear();
        return parsedNum;
    }

    public static Integer convertDate(byte[] adv, int bcnExTempPtr) {
        byte[] bytes = {adv[bcnExTempPtr], adv[bcnExTempPtr + 1]};
        short s = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getShort();
        int iTemp = (int) s;
        return iTemp;
    }

    public static byte[] convertLittleEndian(int value) {
        byte[] bytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array();
        return bytes;
    }

    public static JSONArray sortJsonArray(JSONArray array) throws JSONException {
        List<JSONObject> jsons = new ArrayList<JSONObject>();
        for (int i = 0; i < array.length(); i++) {
            jsons.add(array.getJSONObject(i));
        }

        Collections.sort(jsons, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject lhs, JSONObject rhs) {
                String lid = null;
                String rid = null;
                try {
                    lid = lhs.getString("alias");
                    rid = rhs.getString("alias");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Here you could parse string id to integer and then compare.
                return lid.compareTo(rid);
            }
        });
        return new JSONArray(jsons);
    }

    public static String getJsonFromAssets(Context context, String fileName) {
        String jsonString;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return jsonString;
    }
}
