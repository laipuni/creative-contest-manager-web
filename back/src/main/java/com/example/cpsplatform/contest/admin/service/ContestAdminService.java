package com.example.cpsplatform.contest.admin.service;

import com.example.cpsplatform.contest.admin.service.dto.ContestCreateDto;
import com.example.cpsplatform.contest.repository.ContestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ContestAdminService {

    private final ContestRepository contestRepository;

    @Transactional
    public void createContest(ContestCreateDto createDto){
        contestRepository.save(createDto.toEntity());
    }

}
