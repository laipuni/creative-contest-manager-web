package com.example.cpsplatform.queue.worker;

import com.example.cpsplatform.queue.job.AnswerSubmitJob;
import com.example.cpsplatform.queue.service.QueueService;
import com.example.cpsplatform.teamsolve.service.AnswerSubmitService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TeamAnswerWorker {

    public static final int SUBMIT_ANSWER_WORKER_THREAD = 3;

    private final QueueService queueService;
    private final AnswerSubmitService answerSubmitService;


    @PostConstruct
    public void init(){
        log.info("[TeamAnswerWorker] 큐 초기화 시작");
        queueService.clear();
        for (int i = 0; i < SUBMIT_ANSWER_WORKER_THREAD; i++) {
            new Thread(this::work, "answer-submit-worker" + i).start();
            log.info("[TeamAnswerWorker] 스레드 {} 시작", "answer-submit-worker" + i);
        }
    }

    private void work(){
        while (true) {
            try {
                AnswerSubmitJob job = queueService.take();
                if (job != null) {
                    log.info("[TeamAnswerWorker] 팀(id:{})의 문제(id : {}) 답안지 제출 작업 시작", job.getTeamId(), job.getProblemId());
                    answerSubmitService.saveTeamSolve(job);
                }
            } catch (InterruptedException e) {
                log.warn("[TeamAnswerWorker] 인터럽트 발생, 워커 스레드 종료 시도", e);
                Thread.currentThread().interrupt(); // 인터럽트 플래그 복원
                break; //안전하게 루프 종료
            } catch (Exception e) {
                log.error("[TeamAnswerWorker] 작업 처리 중 예외 발생", e);
                // TODO: 실패한 job 재큐잉 또는 별도 에러 큐로 보관
            }
        }

    }

}
