//package com.example.account.service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.transaction.Transactional;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//
//@RequiredArgsConstructor
//@Slf4j
//@Service
//@RestController
//public class MakeAccountNumber {
//    private List<String> accountNumberList;
//
//    @Autowired
//    public MakeAccountNumber(List<String> accountNumberList) {
//        this.accountNumberList = accountNumberList;
//    }
//    @Transactional
//    public String makeAccountNumber(){
//        Random rand = new Random();
//        StringBuffer sb = new StringBuffer();
//        while(sb.length()!=10){
//            sb.append(rand.nextInt(9)+"");
//        }
//        if(!checkAccountNumber(sb.toString()))
//            makeAccountNumber();//failfast
//        return sb.toString();
//    }
//    //이를 통해, id number 별로 10자리의 난수를 List에 저장하고
//    //이 리스트에 겹친다면 다시
//
//
//    //아래 로직에서 nullpointException의 발생으로 난수 발생은 만들 수 있지만, 검증을 통한
//    //갱신을 구현할 수 없었음.
//    @Transactional
//    public boolean checkAccountNumber(String madeAccountNumber) throws NullPointerException{
//        if(accountNumberList.isEmpty()){
//            accountNumberList.add(madeAccountNumber);
//            return true;
//        }
//        if(accountNumberList.contains(madeAccountNumber)){
//            return false; //실패 먼저 처리하는 로직. failfast
//        }
//        accountNumberList.add(madeAccountNumber);
//        return true;
//    }
//}
