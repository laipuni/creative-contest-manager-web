package com.example.cpsplatform.certificate.admin.service;

import com.example.cpsplatform.certificate.domain.Certificate;
import com.example.cpsplatform.certificate.repository.CertificateRepository;
import com.example.cpsplatform.certificate.repository.dto.AdminSearchCertificateCond;
import com.example.cpsplatform.certificate.repository.dto.AdminSearchCertificateResponse;
import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.contest.repository.ContestRepository;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.memberteam.domain.MemberTeam;
import com.example.cpsplatform.memberteam.repository.MemberTeamRepository;
import com.example.cpsplatform.team.domain.Team;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CertificateAdminService {

    public static final String CERTIFICATE_ADMIN_LOG = "[CertificateAdmin]";

    private final CertificateRepository certificateRepository;
    private final ContestRepository contestRepository;
    private final MemberTeamRepository memberTeamRepository;

    /*
    contest Id에 해당하는 예선 대회에 참가한 팀원들의 예선 참가 확인증을 일괄적으로 생성해주는 로직
     */
    @Transactional
    public void batchCreatePreliminaryCertificates(final Long contestId) {
        Contest contest = findContest(contestId);
        List<MemberTeam> memberTeamList = memberTeamRepository.findAllByContestId(contestId);
        createAndSavePreliminaryCertificate(memberTeamList,contest);
        log.info("{} 대회(id:{})에 예선 참가 확인증 저장",CERTIFICATE_ADMIN_LOG,contest.getId());
    }

    private Contest findContest(final Long contestId) {
        log.debug("{} 대회(id:{})를 조회 시도",CERTIFICATE_ADMIN_LOG,contestId);
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new IllegalArgumentException("해당 대회는 존재하지 않습니다."));
        return contest;
    }

    private void createAndSavePreliminaryCertificate(final List<MemberTeam> memberTeamList, final Contest contest) {
        List<Certificate> certificateList = new ArrayList<>();
        for(MemberTeam mt : memberTeamList){
            String serialNumber = UUID.randomUUID().toString();
            Member member = mt.getMember();
            Team team = mt.getTeam();
            Certificate certificate = Certificate.createPreliminaryCertificate(serialNumber, contest, member, team);
            certificateList.add(certificate);
            log.info("{} 팀(id:{})에 유저(id:{})의 예선 참가 확인증 생성",CERTIFICATE_ADMIN_LOG,team.getId(),member.getId());
        }
        certificateRepository.saveAll(certificateList);
    }

    @Transactional
    public void deleteCertificate(final Long certificateId) {
        log.debug("{} 확인증(id:{}) 제거 시도",CERTIFICATE_ADMIN_LOG,certificateId);
        certificateRepository.deleteById(certificateId);
        log.info("{} 확인증(id:{}) 제거",CERTIFICATE_ADMIN_LOG,certificateId);
    }

    public AdminSearchCertificateResponse searchCertificates(final AdminSearchCertificateCond cond) {
        return certificateRepository.SearchAdminCertificate(cond);
    }
}
