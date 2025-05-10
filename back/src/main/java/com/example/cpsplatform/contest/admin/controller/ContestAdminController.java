package com.example.cpsplatform.contest.admin.controller;

import com.example.cpsplatform.ApiResponse;
import com.example.cpsplatform.admin.annotaion.AdminLog;
import com.example.cpsplatform.contest.admin.controller.request.HardDeleteContestRequest;
import com.example.cpsplatform.contest.admin.controller.response.*;
import com.example.cpsplatform.contest.admin.request.CreateContestRequest;
import com.example.cpsplatform.contest.admin.request.DeleteContestRequest;
import com.example.cpsplatform.contest.admin.request.UpdateContestRequest;
import com.example.cpsplatform.contest.admin.request.WinnerTeamsRequest;
import com.example.cpsplatform.contest.admin.service.ContestAdminService;
import com.example.cpsplatform.contest.admin.service.ContestDeleteService;
import com.example.cpsplatform.exception.DuplicateDataException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/contests")
@RequiredArgsConstructor
public class ContestAdminController {

    private final ContestAdminService contestAdminService;
    private final ContestDeleteService contestDeleteService;

    //최신 대회 정보를 받아오는 api
    @AdminLog
    @GetMapping("/latest")
    public ApiResponse<ContestLatestResponse> findContestLatest(){
        ContestLatestResponse response = contestAdminService.findContestLatest();
        return ApiResponse.ok(response);
    }

    @AdminLog
    @GetMapping
    public ApiResponse<ContestListResponse> searchContestList(@RequestParam(value = "page",defaultValue = "0") int page){
        ContestListResponse response = contestAdminService.searchContestList(page);
        return ApiResponse.ok(response);
    }

    @AdminLog
    @GetMapping("/{contestId}")
    public ApiResponse<ContestDetailResponse> findContestDetail(@PathVariable("contestId")Long contestId){
        ContestDetailResponse response = contestAdminService.findContestDetail(contestId);
        return ApiResponse.ok(response);
    }

    @AdminLog
    @PostMapping
    public ApiResponse<Object> createContest(@Valid @RequestBody CreateContestRequest request){
        try{
            //todo 추후 데이터 중복 예외 일괄 처리 요함
            contestAdminService.createContest(request.toContestCreateDto());
        } catch (DataIntegrityViolationException e){
            throw new DuplicateDataException("동일한 회의 대회가 있습니다.");
        }
        return ApiResponse.ok(null);
    }

    @AdminLog
    @PutMapping
    public ApiResponse<Object> updateContest(@Valid @RequestBody UpdateContestRequest request){
        try{
            //todo 추후 데이터 중복 예외 일괄 처리 요함
            contestAdminService.updateContest(request.toContestUpdateDto());
        } catch (DataIntegrityViolationException e){
            throw new DuplicateDataException("동일한 회의 대회가 있습니다.");
        }
        return ApiResponse.ok(null);
    }

    @AdminLog
    @DeleteMapping
    public ApiResponse<Object> deleteContest(@Valid @RequestBody DeleteContestRequest request){
        contestAdminService.deleteContest(request.toContestDeleteDto());
        return ApiResponse.ok(null);
    }

    @AdminLog
    @GetMapping("/{contestId}/teams")
    public ApiResponse<TeamListByContestResponse> searchTeamListByContest(@PathVariable("contestId")Long contestId,
                                                                          @RequestParam(value = "page",defaultValue = "0") int page){
        TeamListByContestResponse teamListResponse = contestAdminService.searchTeamListByContest(contestId, page);
        return ApiResponse.ok(teamListResponse);
    }

    @AdminLog
    @PatchMapping("/{contestId}/winners")
    public ApiResponse<Void> selectWinnerTeams(@PathVariable Long contestId,
                                               @Valid @RequestBody WinnerTeamsRequest request){
        contestAdminService.selectWinnerTeams(contestId, request.toWinnerTeamsDto());
        return ApiResponse.ok(null);
    }
  
    @PatchMapping("/{contestId}/recover")
    public ApiResponse<Object> recoverContest(@PathVariable("contestId")Long contestId){
        contestAdminService.recoverContest(contestId);
        return ApiResponse.ok(null);
    }

    @AdminLog
    @GetMapping("/deleted")
    public ApiResponse<DeletedContestListResponse> recoverContest(){
        DeletedContestListResponse response = contestAdminService.findDeletedContest();
        return ApiResponse.ok(response);
    }

    @AdminLog
    @DeleteMapping("/hard")
    public ApiResponse<Object> removeCompletelyContest(@Valid @RequestBody HardDeleteContestRequest request){
        contestDeleteService.deleteCompletelyContest(request.getContestId());
        return ApiResponse.ok(null);
    }
}
