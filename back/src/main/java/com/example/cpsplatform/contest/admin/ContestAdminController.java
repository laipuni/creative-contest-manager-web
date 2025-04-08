package com.example.cpsplatform.contest.admin;

import com.example.cpsplatform.ApiResponse;
import com.example.cpsplatform.admin.annotaion.AdminLog;
import com.example.cpsplatform.contest.admin.request.CreateContestRequest;
import com.example.cpsplatform.contest.admin.request.UpdateContestRequest;
import com.example.cpsplatform.contest.admin.service.ContestAdminService;
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

}
