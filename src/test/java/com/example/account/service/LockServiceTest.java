package com.example.account.service;

import com.example.account.exception.AccountException;
import com.example.account.type.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LockServiceTest {
    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RLock rLock;

    @InjectMocks
    private LockService lockService;

    @Test
    @DisplayName("거래 중입니다. 잠금되었습니다.")
    void successGetLock() throws InterruptedException {
        //given
        given(redissonClient.getLock(anyString()))
                .willReturn(rLock);
        given(rLock.tryLock(anyLong(), anyLong(), any()))
                .willReturn(true);
        //when

        //then
        assertDoesNotThrow(() -> lockService.lock("1234567890"));
    }
    @Test
    @DisplayName("음 거래 중인데 거래가 이루어졌네요. 문제가 발생했습니다. 확인하세요.")

    void failGetLock() throws InterruptedException{
        //given
        given(redissonClient.getLock(anyString()))
                .willReturn(rLock);
        given(rLock.tryLock(anyLong(),anyLong(),any()))
                .willReturn(false);
        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> lockService.lock("1234567890"));
        //then
        assertEquals(ErrorCode.ACCOUNT_TRANACTION_LOCK,exception.getErrorCode());
    }
}