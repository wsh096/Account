package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.dto.AccountDto;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRepository;
import com.example.account.type.AccountStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import static com.example.account.type.AccountStatus.IN_USE;
import static com.example.account.type.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountUserRepository accountUserRepository;
    private final List<String> AccountNumberCheckList;
    /**
     * 사용자가 있는지 확인
     * 계좌의 번호를 생성하고
     * 계좌를 저장하고, 그 정보를 넘긴다.
     */
    @Transactional
    public AccountDto createAccount(Long userId, Long initialBalance) {
        AccountUser accountUser = getAccountUser(userId);

        validateCreateAccount(accountUser);
    
        String newAccountNumber = getMakeAccountNumber();
        
        return AccountDto.fromEntity(
                accountRepository.save(Account.builder()
                        .accountUser(accountUser)
                        .accountStatus(IN_USE)
                        .accountNumber(newAccountNumber)
                        .balance(initialBalance)
                        .registeredAt(LocalDateTime.now())
                        .build())
        );
    }

    /**
     * 새로운 서비스를 만들어서 하고 싶었지만 계속 nullpointException이 나서
     * 해당 부분에 위치시키게 되었습니다.
     * 코드는 간단합니다. List에 저장하고 List에 해당 값이 존재하면 겹치는 난수이기 때문에
     * 재실행하도록 로직을 구현하는 방식을 취했습니다 :)
     * @return
     * 테스트 코드에서는 값이 담기지 않는 것을 확인할 수 있는데,
     * 이를 해결할 필요가 있음.(못한 채로 제출합니다 ㅠ
     * CreateAccountTest와 CreateFirstAccountTest통과 실패.)
     */
    private String getMakeAccountNumber() {
        Random rand = new Random();
        StringBuffer sb = new StringBuffer();
        while(sb.length()!=10){
            sb.append(rand.nextInt(9)+"");
        }
        if(!isCheckAccountNumber(sb.toString())) {
            getMakeAccountNumber();//failfast
        }
        return sb.toString();
    }

    private boolean isCheckAccountNumber(String needToCheck) {
//        if(AccountNumberCheckList.isEmpty()){
//            AccountNumberCheckList.add(needToCheck);
//            return true;
//        }

        if(AccountNumberCheckList.contains(needToCheck)){
            return false;
        }
        AccountNumberCheckList.add(needToCheck);
        return true;
    }

    private void validateCreateAccount(AccountUser accountUser) {
        if (accountRepository.countByAccountUser(accountUser) == 10) {
            throw new AccountException(MAX_ACCOUNT_PER_USER_10);
        }
    }

    @Transactional
    public Account getAccount(Long id) {
        if (id < 0) {
            throw new RuntimeException("Minus");
        }
        return accountRepository.findById(id).get();
    }

    @Transactional
    public AccountDto deleteAccount(Long userId, String accountNumber) {
        AccountUser accountUser = getAccountUser(userId);
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));

        vaildateDeleteAccount(accountUser, account);

        account.setAccountStatus(AccountStatus.UNRESITERED);
        account.setUnRegisteredAt(LocalDateTime.now());

        accountRepository.save(account);//필요한 건 아니지만 그래도 확인하는 한 가지 방법

        return AccountDto.fromEntity(account);
    }

    private void vaildateDeleteAccount(AccountUser accountUser, Account account) {
        if (!Objects.equals(accountUser.getId(), account.getAccountUser().getId())) {
            throw new AccountException(USER_ACCOUNT_UN_MATCH);

        }
        if (account.getAccountStatus() == AccountStatus.UNRESITERED) {
            throw new AccountException(ACCOUNT_ALREADY_UNREGISTERED);
        }
        if (account.getBalance() > 0) {
            throw new AccountException(BALANCE_NOT_EMPTY);
        }
    }

    @Transactional//이게 없으면 정상적인 조회가 불가능
    public List<AccountDto> getAccountByUserId(Long userId) {
        AccountUser accountUser = getAccountUser(userId);

        List<Account> account = accountRepository
                .findByAccountUser(accountUser);
        return account.stream()
                .map(AccountDto::fromEntity)
                .collect(Collectors.toList());
    }

    private AccountUser getAccountUser(Long userId) {
        return accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));
    }
}
