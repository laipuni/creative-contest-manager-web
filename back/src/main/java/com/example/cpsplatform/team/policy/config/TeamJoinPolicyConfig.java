package com.example.cpsplatform.team.policy.config;

import com.example.cpsplatform.team.policy.EveryYearSignUpPolicy;
import com.example.cpsplatform.team.policy.TeamJoinEligibilityPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TeamJoinPolicyConfig {

    @Bean
    public TeamJoinEligibilityPolicy teamJoinEligibilityPolicy(){
        return new EveryYearSignUpPolicy();
    }

}
