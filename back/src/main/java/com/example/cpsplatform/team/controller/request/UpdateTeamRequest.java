package com.example.cpsplatform.team.controller.request;

import com.example.cpsplatform.team.service.dto.TeamUpdateDto;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateTeamRequest {
    @Size(max = 6, message = "팀명은 7자 이내로 작성할 수 있습니다")
    private String teamName;

    private List<String> memberIds;

    public TeamUpdateDto toServiceDto() {
        return new TeamUpdateDto(teamName, memberIds);
    }
}
