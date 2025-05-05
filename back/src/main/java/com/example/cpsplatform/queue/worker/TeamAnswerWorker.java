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
@Profile({"local","prod"})
@RequiredArgsConstructor
public class TeamAnswerWorker {

    public static final int SUBMIT_ANSWER_WORKER_THREAD = 3;

    private final QueueService queueService;
    private final AnswerSubmitService answerSubmitService;


    @PostConstruct
    public void init(){
        for (int i = 0; i < SUBMIT_ANSWER_WORKER_THREAD; i++) {
            new Thread(this::work, "answer-submit-worker" + i).start();
        }
    }

    private void work(){
        while (true){
            try {
                AnswerSubmitJob job = queueService.take();
                if(job != null){
                    log.info("[TeamAnswerWorker] 팀(id:{})의 문제(id : {}) 답안지 제출 작업 시작",job.getTeamId(),job.getProblemId());
                    answerSubmitService.saveTeamSolve(job);
                }
            } catch (InterruptedException e) {
                //todo 실패했을 경우 실패한 작업들 큐잉 처리 필요
                throw new RuntimeException(e);
            }
        }
    }

}
