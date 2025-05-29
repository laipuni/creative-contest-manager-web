package com.example.cpsplatform.utils;

import com.example.cpsplatform.contest.Contest;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 시간과 날짜를 원하는 형식으로 변환해주는 유틸 클래스
 */
public class TimeConvertUtils {

    /**
     * 대회 시작 시간과 종료 시간을 받아 포맷된 문자열을 반환
     * ex) "2025년 05월 06일 18:00~20:00"
     *
     * @param startTime 대회 시작 시간
     * @param endTime   대회 종료 시간
     * @return 포맷된 대회 시간 문자열
     */
    public static String getContestDateTime(final LocalDateTime startTime, final LocalDateTime endTime) {
        //ex)2025년 05월 06일 18:00 ~ 20:00
        return convertDateToString(LocalDate.from(startTime)) + " " + convertTimeToString(startTime) + "~" + convertTimeToString(endTime);
    }

    /**
     * LocalDate를 "YYYY년 MM월 DD일" 형식의 문자열로 변환
     * ex) "2025년 05월 06일"
     *
     * @param localDate 변환할 날짜
     * @return 포맷된 날짜 문자열
     */
    public static String convertDateToString(LocalDate localDate) {
        //ex)2025년 05월 06일`
        return String.format("%d년 %02d월 %02d일",
                localDate.getYear(),
                localDate.getMonthValue(),
                localDate.getDayOfMonth()
        );
    }

    /**
     * LocalDateTime을 "YYYY년 MM월 DD일 HH:MM" 형식의 문자열로 변환
     * ex) "2025년 05월 06일 15:00"
     *
     * @param localDateTime 변환할 날짜 및 시간
     * @return 포맷된 날짜+시간 문자열
     */
    public static String convertDateTimeToString(LocalDateTime localDateTime) {
        //ex)2025년 05월 06일 15:00
        return String.format("%d년 %02d월 %02d일 %02d:%02d",
                localDateTime.getYear(),
                localDateTime.getMonthValue(),
                localDateTime.getDayOfMonth(),
                localDateTime.getHour(),
                localDateTime.getMinute()
        );
    }

    /**
     * LocalDateTime에서 시간 정보만 추출하여 "HH:MM" 형식의 문자열로 변환
     * ex) "18:00"
     *
     * @param localDateTime 변환할 시간 정보
     * @return 포맷된 시간 문자열
     */
    public static String convertTimeToString(LocalDateTime localDateTime){
        //ex)18:00
        return String.format("%02d:%02d",
                localDateTime.getHour(),
                localDateTime.getMinute()
        );
    }

}
