package com.example.cpsplatform;

import com.example.cpsplatform.utils.PagingUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PagingUtilsTest {

    @DisplayName("지금 0번째 페이지, 나누는 5페이지 일때, 1을 반환한다.")
    @Test
    void getStartPage(){
        //given
        //when
        //then
        Assertions.assertThat(PagingUtils.getStartPage(0,5)).isEqualTo(1);
    }

    @DisplayName("첫번째 페이지는 6, 총 페이지는 6, 나누는 페이지 5일때, 6을 반환한다.")
    @Test
    void getEndPage(){
        //given
        //when
        //then
        Assertions.assertThat(PagingUtils.getEndPage(6,6)).isEqualTo(6);
    }

}