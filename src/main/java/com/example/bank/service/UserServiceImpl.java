package com.example.bank.service;

import com.example.bank.Repository.UserRepository;
import com.example.bank.config.JwtTokenProvider;
import com.example.bank.dto.*;
import com.example.bank.entity.Role;
import com.example.bank.entity.User;
import com.example.bank.utils.AccountUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService{
    @Autowired
    UserRepository userRepository;
    @Autowired
    EmailService emailService;
    @Autowired
    TransactionService transactionService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User user=User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .phoneNumber(userRequest.getPhoneNumber())
                .status("ACTIVE")
                .role(Role.ROLE_ADMIN)
                .build();
        User savedUser=userRepository.save(user);
        EmailDetails emailDetails=EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("Account Created")
                .messageBody("Congratulations !! Your account has been created. \n"
                +" Account Name: " +savedUser.getFirstName()+" "+savedUser.getLastName()+
                "Account Number : "+savedUser.getAccountNumber())
                .build();
        emailService.sendEmailAlert(emailDetails);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountName(savedUser.getFirstName()+" "+savedUser.getLastName())
                        .build())
                .build();
    }

    public BankResponse login(LoginDto loginDto){
        Authentication authentication=null;
        authentication=authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(),loginDto.getPassword())
        );
        EmailDetails loginAlert= EmailDetails.builder()
                .subject("You are logged in !!")
                .recipient(loginDto.getEmail())
                .messageBody("You logged into your account. If you didn't initiate this conversation ,please contact your bank.")
                .build();
        emailService.sendEmailAlert(loginAlert);
        return BankResponse.builder()
                .responseCode("Login Success")
                .responseMessage(jwtTokenProvider.generateToken(authentication))
                .build();
    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest request) {
        boolean isAccountExist=userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExist){
            return  BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User foundUser=userRepository.findByAccountNumber(request.getAccountNumber());
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .accountName(foundUser.getFirstName()+" "+foundUser.getLastName())
                        .build())
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest request) {
        boolean isAccountExist=userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExist){
            return  AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE;
        }
        User foundUser=userRepository.findByAccountNumber(request.getAccountNumber());
        return foundUser.getFirstName()+" "+foundUser.getLastName();
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        boolean isAccountExist=userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User user=userRepository.findByAccountNumber(request.getAccountNumber());
        user.setAccountBalance(user.getAccountBalance().add(request.getAmount()));
        userRepository.save(user);

        TransactionDto transactionDto=TransactionDto.builder()
                .accountNumber(user.getAccountNumber())
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .build();
        transactionService.saveTransaction(transactionDto);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(user.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .accountName(user.getFirstName()+" "+user.getLastName())
                        .build())
                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        boolean isAccountExist=userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User user=userRepository.findByAccountNumber(request.getAccountNumber());

            if(user.getAccountBalance().compareTo(request.getAmount())<0){
                return BankResponse.builder()
                        .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                        .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                        .accountInfo(null)
                        .build();
            }

            else {
                user.setAccountBalance(user.getAccountBalance().subtract(request.getAmount()));
                userRepository.save(user);
                return BankResponse.builder()
                        .responseCode(AccountUtils.ACCOUNT_DEBITED_CODE)
                        .responseMessage(AccountUtils.ACCOUNT_DEBITED_MESSAGE)
                        .accountInfo(AccountInfo.builder()
                                .accountBalance(user.getAccountBalance())
                                .accountNumber(request.getAccountNumber())
                                .accountName(user.getFirstName()+" "+user.getLastName())
                                .build())
                        .build();
            }
    }

    @Override
    public BankResponse transfer(TransferRequest request) {
        boolean isADestinationAccountExist=userRepository.existsByAccountNumber(request.getDestinationAccount());
        if (!isADestinationAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User sourceAccountUser=userRepository.findByAccountNumber(request.getSourceAccount());
        if(sourceAccountUser.getAccountBalance().compareTo(request.getAmount())<0){
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        sourceAccountUser.setAccountBalance(sourceAccountUser.getAccountBalance().subtract(request.getAmount()));
        userRepository.save(sourceAccountUser);
        EmailDetails debitAlert= EmailDetails.builder()
                .recipient(sourceAccountUser.getEmail())
                .subject("DEBIT ALERT")
                .messageBody("The sum of "+request.getAmount()+" has been debited from your account.Your current account balance is"+sourceAccountUser.getAccountBalance())
                .build();
        emailService.sendEmailAlert(debitAlert);

        User destinationAccountUser= userRepository.findByAccountNumber(request.getDestinationAccount());
        destinationAccountUser.setAccountBalance(destinationAccountUser.getAccountBalance().add(request.getAmount()));
        userRepository.save(destinationAccountUser);
        EmailDetails creditAlert= EmailDetails.builder()
                .recipient(destinationAccountUser.getEmail())
                .subject("CREDIT ALERT")
                .messageBody("The sum of "+request.getAmount()+" has been credited to your account.Your current account balance is"+destinationAccountUser.getAccountBalance())
                .build();
        emailService.sendEmailAlert(creditAlert);

        TransactionDto transactionDto=TransactionDto.builder()
                .accountNumber(destinationAccountUser.getAccountNumber())
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .build();
        transactionService.saveTransaction(transactionDto);

        return BankResponse.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESSFUL_CODE)
                .responseMessage(AccountUtils.TRANSFER_SUCCESSFUL_MESSAGE)
                .accountInfo(null)
                .build();
    }

}
