package com.example.cpsplatform.exception.controller.dto;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum UniqueConstraintMessage {

    //Certificate 관련 제약조건
    CERTIFICATE_UNIQUE("uk_preliminary_certificate", "이미 발급된 인증서입니다."),

    //Contest 관련 제약조건
    CONTEST_SEASON("uk_contest_season", "해당 시즌의 대회가 이미 존재합니다."),

    //File 관련 제약조건
    FILE_TEAM_SOLVE("uk_file_team_solve_id", "팀 솔루션에 이미 파일이 등록되어 있습니다."),

    //Member 관련 제약조건
    MEMBER_LOGIN_ID("uk_member_login_id", "이미 사용 중인 로그인 아이디입니다."),
    MEMBER_EMAIL("uk_member_email", "이미 사용 중인 이메일 주소입니다."),
    MEMBER_PHONE("uk_member_phone_number", "이미 등록된 전화번호입니다."),
    MEMBER_ORGANIZATION("uk_member_organization_id", "이미 연결된 조직이 있습니다."),

    //MemberTeam 관련 제약조건
    MEMBER_TEAM("uk_member_team_memberid_teamid", "이미 해당 팀에 등록된 회원입니다."),

    //Problem 관련 제약조건
    PROBLEM_SECTION_ORDER("uk_problem_contest_section_order", "해당 대회의 섹션에 이미 같은 순서의 문제가 존재합니다."),
    PROBLEM_TITLE("uk_problem_title", "이미 사용 중인 문제 제목입니다."),

    //Team 관련 제약조건
    TEAM_CONTEST_NUMBER("uk_team_contestid_number", "해당 대회에 이미 같은 번호의 팀이 존재합니다."),
    TEAM_NAME("uk_contestid_team_name", "이미 해당 대회에 사용 중인 팀 이름입니다."),

    //TeamNumber 관련 제약조건
    TEAM_NUMBER_CONTEST("uk_team_number_contestid", "해당 대회의 팀 번호가 이미 등록되어 있습니다."),

    //TeamSolve 관련 제약조건
    TEAM_SOLVE_PROBLEM("uk_team_solve_teamid_problemid_type", "해당 팀은 이미 이 문제에 대한 솔루션을 제출했습니다.");

    private final String constraintName;
    private final String message;
    private static final Map<String,String> messageMap = Arrays.stream(UniqueConstraintMessage.values()).collect(
            Collectors.toMap(UniqueConstraintMessage::getConstraintName, UniqueConstraintMessage::getMessage)
    );

    public static String findUniqueConstraintMessage(String constraintName) {
        String message = messageMap.get(constraintName);
        if (message == null) {
            log.warn("정의되지 않은 제약조건 이름: {}", constraintName);
            return null;
        }
        return message;
    }

}
