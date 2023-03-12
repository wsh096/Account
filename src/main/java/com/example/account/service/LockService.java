package com.example.account.service;

import com.example.account.exception.AccountException;
import com.example.account.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor //config에 자주 붙는 3종 셋
@Slf4j
@Service
public class LockService {
    private final RedissonClient redissonClient;

    public void lock(String accountNumber) { //잠시 삭제 AccountController 떄문 String id
        RLock lock = redissonClient.getLock(getLockKey(accountNumber));
        log.debug("Trying lock for accountNumber : {}", accountNumber);

        try {
            boolean isLock = lock.tryLock(1, 5, TimeUnit.SECONDS );
            if (!isLock) {
                log.error("==========Lock acquisition failed===========");
                throw new AccountException(ErrorCode.ACCOUNT_TRANACTION_LOCK);

            }
        } catch(AccountException e){
            throw e;
        }
        catch (Exception e) {
            log.error("lock failed", e);
        }
    }

    public void unlock(String accountNumber){
        log.debug("Unlock for accountNumber : {} ", accountNumber);
        redissonClient.getLock(getLockKey(accountNumber)).unlock();
    }

    private String getLockKey(String accountNumber) {
        return "ACLK:" + accountNumber;
    }
}
