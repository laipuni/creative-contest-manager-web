package com.example.cpsplatform.queue.service;

import com.example.cpsplatform.queue.job.AnswerSubmitJob;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;


@Slf4j
public class RedissonQueueService implements QueueService {

    private final RBlockingQueue<AnswerSubmitJob> queue;
    public RedissonQueueService(final RedissonClient redissonClient) {
        this.queue = redissonClient.getBlockingQueue("TeamSolve:answer-submit-processing");
    }

    @Override
    public void enqueue(final AnswerSubmitJob job) {
        try {
            queue.add(job);
        } catch (Exception e) {
            log.error("RedissonQueueService enqueue 실패", e);
            throw new IllegalStateException("Redisson enqueue 실패", e);
        }
    }

    @Override
    public AnswerSubmitJob take() throws InterruptedException {
        return queue.take();
    }
}
