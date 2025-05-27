package com.example.cpsplatform;

/**
 * 페이징에 필요한 시작 페이지와 끝 페이지를 계산하는 유틸리티 클래스입니다.
 * 사용 예시:
 * 하단 페이지 버튼이 <1 2 3 4 5 6 7 8 9 10>과 같이 일정 범위로 묶어 표시할 때 유용합니다.
 */
public class PagingUtils {

    //현재 페이지를 기준으로 시작 페이지 번호를 계산합니다.
    public static int getStartPage(int currentPage,int parsePage){
        if (parsePage <= 1) {
            //parsePage가 1 이하인 경우 처리
            return 1;
        }
        return (currentPage % parsePage == 0) ?
                (currentPage / (parsePage - 1)) * parsePage + 1 :
                (currentPage / parsePage) * parsePage + 1;
    }

    //시작 페이지와 전체 페이지 수를 기준으로 끝 페이지 번호를 계산합니다.
    public static int getEndPage(int startPage,int totalPage){
        return Math.min((startPage - 1) + 10, totalPage);
    }
}
