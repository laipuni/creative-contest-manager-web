package com.example.cpsplatform.team.controller.request;

import com.example.cpsplatform.team.service.dto.TeamUpdateDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateTeamRequest {

    @NotBlank(message = "팀명은 필수입니다.")
    @Size(max = 6, message = "팀명은 7자 이내로 작성할 수 있습니다")
    private String teamName;

    @NotNull(message = "팀원 정보는 필수입니다.")
    private List<String> memberIds;

    public TeamUpdateDto toServiceDto() {
        return new TeamUpdateDto(teamName, memberIds);
    }
}
