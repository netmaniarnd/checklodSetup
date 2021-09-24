package com.netmania.checklod.domain.parser;

import android.annotation.SuppressLint;

import com.netmania.checklod.data.constant.DeviceStatus;
import com.netmania.checklod.data.constant.TripStatus;
import com.netmania.checklod.data.repository.entity.DeviceLogEntity;
import com.netmania.checklod.data.repository.entity.TripEntity;
import com.netmania.checklod.util.DateTimeUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 디바이스 시퀀스별 정보 관리
 */
public class DeviceLogParser {

    /**
     * 냉동 테스트 여부 및 온도 차감치
     */
    public static final double FROZEN_SAFE_GAB = 3.0;
    /**
     * 20% 안쪽으로 진입시 경고
     */
    public static final double SAFE_GAB_T = 0.15;
    /**
     * 20% 안쪽으로 진입시 경고
     */
    public static final long MINIMUN_RSSI = -90;
    /**
     * 배터리 체크
     */
    public static final double BATTERY_LOW_LIMIT = 0.0;
    /*
    27    CompanyID
    28    CompanyID
    29    센서온도 하위바이트
    30    센서온도 상위바이트
    31    본체온도 하위바이트
    32    본체온도 상위바이트
    33    현재 날짜 mm
    34    현재 날짜 dd
    35    현재 날짜 HH
    36    현재 날짜 MM
    37    현재 인덱스 하위바이트
    38    현재 인덱스 상위바이트
    39    배터리상태
    */
    /**
     * 27번째 부터 사용
     */
    public static final int PROTOCOL_OFFSET = 27;

    /**
     * 디바이스 로그 관리
     */
    public static DeviceLogEntity getDeviceLogEntity(byte[] rsp, String mac, int offset) throws ParseException {
        DeviceLogEntity entity = new DeviceLogEntity();
        entity.setSequence(Integer.parseInt(String.valueOf(convertSeq(rsp, 10 + offset))));
        entity.setInnerTemp(convertTemp(rsp, 4 + offset));
        entity.setOuterTemp(convertTemp(rsp, 2 + offset));
        entity.setRtc(convertDate(rsp[6 + offset], rsp[7 + offset], rsp[8 + offset], rsp[9 + offset]));
        if (offset != 0) {
            entity.setBattery(rsp[12 + offset]);
        }
        entity.setMac(mac);
        // entity.setRssi(rssi);
        entity.setTimeStamp(DateTimeUtil.getCurrentTimestamp());
        entity.setSent(0);
        return entity;
    }

    private String uniByteToInt(byte b) {
        return ((int) b & 0xff) / 10 < 1
                ? "0" + ((int) b & 0xff)
                : String.valueOf((int) b & 0xff);
    }

    public static byte[] convertLittleEndian(int value) {
        byte[] bytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array();
        return bytes;
    }

    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b : a) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public static double convertTemp(byte[] adv, int bcnExTempPtr) {
        byte[] bytes = {adv[bcnExTempPtr + 1], adv[bcnExTempPtr]};
        short s = ByteBuffer.wrap(bytes).getShort();
        int iTemp = s;
        return (iTemp / 100.0);
    }

    @SuppressLint("NewApi")
    public static String convertDate(int month, int day, int hour, int minute) {
        SimpleDateFormat timeStampFormat = new SimpleDateFormat(DateTimeUtil.RTC_DATE_FORMAT_NEW, Locale.KOREA);
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(cal.getWeekYear(), month - 1, day, hour, minute);// 안드로이드 month : 0~11
        String dateStr = timeStampFormat.format(cal.getTime());
        // Log.e("dateStr : ", dateStr);
        return dateStr;
    }

    public static int convertSeq(byte[] adv, int bcnSeqPtr) {
        ByteBuffer buf = ByteBuffer.allocate(2);
        buf.put(adv[bcnSeqPtr]);
        buf.put(adv[bcnSeqPtr + 1]);
        int parsedNum = (((int) buf.array()[1] & 0xff) << 8) | (((int) buf.array()[0] & 0xff));
        buf.clear();
        return parsedNum;
    }

    public static int getTempToStatus(TripEntity entity) {
        int step = entity.getStep();
        int takeOver = entity.getTakeOver();
        Double minTemp = entity.getLowerLimit();
        Double maxTemp = entity.getUpperLimit();
        Double currentTemp = entity.getOuterTemp();
        int returnValue = 0;
        double safe_gab = (maxTemp - minTemp) * SAFE_GAB_T;
        if (step == TripStatus.READY && takeOver == 1) {
            returnValue = DeviceStatus.TEMP_STABLE;
        } else if (minTemp <= -1000) {
            // 냉동 대응(이전 디바이스)
            if (Double.valueOf(currentTemp) > maxTemp) {
                //범위 초과일때 --->
                returnValue = DeviceStatus.TEMP_EMERGENCY;
            } else if (Double.valueOf(currentTemp) > maxTemp - FROZEN_SAFE_GAB) {
                //위험구간일때 --->
                returnValue = DeviceStatus.TEMP_CAUTION;
            } else {
                //안정권일때 --->
                returnValue = DeviceStatus.TEMP_STABLE;
            }
        } else {
            if (Double.valueOf(currentTemp) > minTemp + safe_gab && Double.valueOf(currentTemp) < maxTemp - safe_gab) {
                //안정권일때 --->
                returnValue = DeviceStatus.TEMP_STABLE;
            } else if (Double.valueOf(currentTemp) > minTemp && Double.valueOf(currentTemp) < maxTemp) {
                //위험구간일때 --->
                returnValue = DeviceStatus.TEMP_CAUTION;
            } else {
                //범위 초과일때 --->
                returnValue = DeviceStatus.TEMP_EMERGENCY;
            }
        }
        return returnValue;
    }

}
