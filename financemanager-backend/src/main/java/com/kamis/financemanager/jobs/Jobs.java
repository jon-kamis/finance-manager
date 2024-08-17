package com.kamis.financemanager.jobs;

import com.kamis.financemanager.business.LoanBusiness;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Component
public class Jobs {

    @Autowired
    private LoanBusiness loanBusiness;

    @Scheduled(cron="0 0 * * * *")
    public void updateLoanBalances() {
        Instant start = Instant.now();
        log.info("Beginning Async task to update all loan balances");
        loanBusiness.updateLoanBalances();
        log.info("Completed Async task to update all loan balances. Execution time: {}", Duration.between(start, Instant.now()));
    }
}
