package com.example.cpsplatform.contest.admin.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HardDeleteContestRequest {

    @NotNull(message = "삭제할 대회의 정보는 필수입니다.")
    private Long contestId;

}
