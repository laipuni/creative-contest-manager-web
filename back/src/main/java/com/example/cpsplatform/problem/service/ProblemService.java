package com.example.cpsplatform.problem.service;

import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.problem.controller.response.TeamProblemResponse;
import com.example.cpsplatform.problem.domain.Problem;
import com.example.cpsplatform.problem.domain.Section;
import com.example.cpsplatform.problem.repository.ProblemRepository;
import com.example.cpsplatform.team.domain.Team;
import com.example.cpsplatform.team.repository.TeamRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final TeamRepository teamRepository;

    public List<TeamProblemResponse> getProblemsForTeam(Long teamId){
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀을 찾을 수 없습니다."));

        Section teamSection = team.getSection();
        Contest contest = team.getContest();
        Long contestId = contest.getId();

        List<Problem> problems = new ArrayList<>();

        Section specificSection = getSpecificSection(teamSection);
        problems.add(
                problemRepository.findWithFilesByContestIdAndSection(contestId, specificSection)
                        .orElseThrow(() -> new IllegalArgumentException("특정 섹션 문제를 찾을 수 없습니다."))
        );

        problems.add(
                problemRepository.findWithFilesByContestIdAndSection(contestId, Section.COMMON)
                        .orElseThrow(() -> new IllegalArgumentException("공통 문제를 찾을 수 없습니다."))
        );

        return problems.stream()
                .map(TeamProblemResponse::of)
                .collect(Collectors.toList());
    }

    private Section getSpecificSection(Section teamSection) {
        if (teamSection == Section.ELEMENTARY_MIDDLE) {
            return Section.ELEMENTARY_MIDDLE;
        } else if (teamSection == Section.HIGH_NORMAL) {
            return Section.HIGH_NORMAL;
        } else {
            throw new IllegalArgumentException("팀의 섹션이 올바르지 않습니다.");
        }
    }
}
