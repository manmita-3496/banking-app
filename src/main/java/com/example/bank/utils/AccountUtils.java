package com.example.bank.utils;

import java.time.Year;

public class AccountUtils {
    public static String generateAccountNumber(){
        Year currentYear=Year.now();
        int min =100000;
        int max=999999;
        int randNumber= (int) Math.floor(Math.random()*(max-min+1)+min);
        String year=String.valueOf(currentYear);
        String randomNumber= String.valueOf(randNumber);
        return year + randomNumber;
    }
    public static final  String ACCOUNT_EXISTS_CODE="001";
    public static final  String ACCOUNT_EXISTS_MESSAGE="User account already exists !!";
    public static final  String ACCOUNT_CREATION_SUCCESS="002";
    public static final  String ACCOUNT_CREATION_MESSAGE="Account created successfully !!";
    public static final  String ACCOUNT_NOT_EXISTS_CODE="003";
    public static final  String ACCOUNT_NOT_EXISTS_MESSAGE="User account does not exist !!";
    public static final  String ACCOUNT_FOUND_CODE="004";
    public static final  String ACCOUNT_FOUND_MESSAGE="User account found successfully !!";
    public static final  String ACCOUNT_CREDITED_CODE="005";
    public static final  String ACCOUNT_CREDITED_MESSAGE="Amount credited in your account successfully !!";
    public static final  String INSUFFICIENT_BALANCE_CODE="006";
    public static final  String INSUFFICIENT_BALANCE_MESSAGE="Account balance is not sufficient !!";
    public static final  String ACCOUNT_DEBITED_CODE="007";
    public static final  String ACCOUNT_DEBITED_MESSAGE="Amount debited from your account successfully !!";
    public static final  String TRANSFER_SUCCESSFUL_CODE="008";
    public static final  String TRANSFER_SUCCESSFUL_MESSAGE="Transfer done successfully !!";

}
