package com.example.bank.controller;

import com.example.bank.dto.*;
import com.example.bank.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Account Management APIs")
public class UserController {
    @Autowired
    UserService userService;
    @Operation(
            summary = "Create new User Account",
            description = "Creating new user and assigning an account ID"
    )
    @ApiResponse(
            responseCode = "201",
            description = "HTTP status 201 created"
    )

    @PostMapping
    public BankResponse createAccount(@RequestBody UserRequest userRequest){
        return userService.createAccount(userRequest);
    }
    @PostMapping("/login")
    public BankResponse login(@RequestBody LoginDto loginDto){
        return  userService.login(loginDto);
    }
    @Operation(
            summary = "Balance Enquiry",
            description = "Check the account balance"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP status 200 Success"
    )
    @GetMapping("/balanceEnquiry")
    public BankResponse balanceEnquiry(@RequestBody EnquiryRequest enquiryRequest){
        return userService.balanceEnquiry(enquiryRequest);
    }
    @GetMapping("/nameEnquiry")
    public String nameEnquiry(@RequestBody EnquiryRequest request){
        return userService.nameEnquiry(request);
    }
    @PostMapping("/credit")
    public BankResponse creditAccount(@RequestBody CreditDebitRequest request){
        return userService.creditAccount(request);
    }
    @PostMapping("/debit")
    public BankResponse debitAccount(@RequestBody CreditDebitRequest request){
        return userService.debitAccount(request);
    }
    @PostMapping("/transfer")
    public BankResponse transfer(@RequestBody TransferRequest request){
        return userService.transfer(request);
    }
}
