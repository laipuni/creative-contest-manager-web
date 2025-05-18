package com.example.cpsplatform.file;

import com.example.cpsplatform.exception.FileDownloadAuthException;
import com.example.cpsplatform.exception.FileDownloadException;
import com.example.cpsplatform.file.domain.FileType;
import com.example.cpsplatform.file.repository.FileRepository;
import com.example.cpsplatform.memberteam.repository.MemberTeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 유저가 파일을 다운로드 할 때, 권한을 체크하는 서비스
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FileAccessService {

    private final MemberTeamRepository memberTeamRepository;
    private final FileRepository fileRepository;

    /**
     * 다운로드할 파일이 팀의 답안지 인지, 유저가 해당 팀에 속해있는지 검증하는 로직
     * @param teamId 유저가 열람할 답안지를 제출한 팀의 id
     * @param fileId 유저가 다운로드할 파일 id
     * @param loginId 열람 요청한 유저의 아이디
     */
    public void validateMemberFileAccess(final Long teamId,final Long fileId, final String loginId){
        log.info("사용자({})가 팀(id:{})의 답안지 파일(id:{})를 조회합니다.",loginId,teamId,fileId);
        boolean existsByTeamIdAndLoginId = memberTeamRepository.existsByTeamIdAndLoginId(teamId, loginId);
        if(!existsByTeamIdAndLoginId){
            // 해당 팀에 팀원이 아닌 경우
            log.info("사용자({})가 팀(id:{})의 답안지 파일(id:{})를 조회할 권한이 없습니다.",loginId,teamId,fileId);
            throw new FileDownloadAuthException("해당 파일을 다운로드 받을 권한이 없습니다.");
        }
        // 해당 팀에 팀원인 경우, 팀의 답안지 파일 이 맞는지 확인
        boolean existsAnswerFileForTeam = fileRepository.existsAnswerFileForTeam(teamId, fileId, FileType.TEAM_SOLUTION);
        if(!existsAnswerFileForTeam){
            log.info("사용자({})가 팀(id:{})의 답안지 파일(id:{})이 유효하지 않아 다운로드 할 수 없습니다.",loginId,teamId,fileId);
            // 다운로드할 파일이 팀의 답안지가 아니거나 다운로드가 유효한 상태가 아닌 경우
            throw new FileDownloadAuthException("해당 파일을 다운로드 받을 수 없습니다.");
        }
    }

    /**
     * 공지사항에 첨부된 파일인지 검증하는 메서드
     * 만약 공지사항에 첨부되지 않고, 해당 api로는 접근하면 안되는 파일일 경우 예외 발생
     * @param noticeId 파일이 첨부된 공지사항의 pk
     * @param fileId 첨부된 파일의 pk
     */
    public void validateNoticeFileAccess(final Long noticeId, final Long fileId) {
        //해당 파일이 공지사항 파일인지 확인
        boolean result = fileRepository.existsFileByNoticeId(noticeId, fileId);
        if(!result){
            //만약 해당 공지사항의 첨부파일이 아닌 경우
            throw new FileDownloadAuthException("해당 파일을 다운로드 받을 권한이 없습니다.");
        }
    }
}
