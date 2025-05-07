package com.example.cpsplatform.queue.service;

import com.example.cpsplatform.queue.job.AnswerSubmitJob;

public interface QueueService {

    void enqueue(AnswerSubmitJob job);
    AnswerSubmitJob take() throws InterruptedException;

    void clear();

}
