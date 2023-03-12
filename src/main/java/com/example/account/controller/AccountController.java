package com.example.account.controller;

import com.example.account.domain.Account;
import com.example.account.dto.AccountInfo;
import com.example.account.dto.CreateAccount;
import com.example.account.dto.DeleteAccount;
import com.example.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AccountController {//호출 로직.
    private final AccountService accountService; //계층화
    //외부에서는 controller 만 접속하고,
    //controller는 서비스에만 접속하고, service에서는 repository로만 접속함

    @PostMapping("/account")
    public CreateAccount.Response createAccount(
            //String userId, Integer initialBalance) {accountService.createAccount(); //accountService에서 createAccount를 호출
            @RequestBody @Valid CreateAccount.Request request
    ) {
        return CreateAccount.Response.from(accountService.createAccount(
                        request.getUserId(),
                        request.getInitialBalance()
                )
        );

    }

    @DeleteMapping("/account")
    public DeleteAccount.Response deleteAccount(
            //String userId, Integer initialBalance) {accountService.createAccount(); //accountService에서 createAccount를 호출
            @RequestBody @Valid DeleteAccount.Request request
    ) {
        return DeleteAccount.Response.from(
                accountService.deleteAccount(
                        request.getUserId(),
                        request.getAccountNumber()
                )
        );

    }

    @GetMapping("/account")
    public List<AccountInfo> getAccountByUserId(
            @RequestParam("user_id") Long userId
    ) {
        return accountService.getAccountByUserId(userId)
                .stream().map(accountDto ->
                        AccountInfo.builder()
                        .accountNumber(accountDto.getAccountNumber())
                        .balance(accountDto.getBalance())
                        .build())
                .collect(Collectors.toList());
    }

    @GetMapping("/account/{id}")
    public Account getAccount(
            @PathVariable Long id) {
        return accountService.getAccount(id);
    }


}
