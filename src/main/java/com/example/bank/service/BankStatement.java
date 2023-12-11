package com.example.bank.service;

import com.example.bank.Repository.TransactionRepository;
import com.example.bank.Repository.UserRepository;
import com.example.bank.dto.EmailDetails;
import com.example.bank.entity.Transaction;
import com.example.bank.entity.User;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class BankStatement {
    private TransactionRepository transactionRepository;
    private UserRepository userRepository;
    private EmailService emailService;
    private static final String FILE="E:\\Spring\\Documents\\MyStatement.pdf";

    public List<Transaction> generateStatement(String accountNumber,String startDate,String endDate) throws FileNotFoundException, DocumentException {
        LocalDate start=LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate end= LocalDate.parse(endDate,DateTimeFormatter.ISO_DATE);

        List<Transaction> transactionList=transactionRepository.findAll().stream().filter(transaction ->
                transaction.getAccountNumber().equals(accountNumber))
                .filter(transaction -> transaction.getCreatedAt().isEqual(start)).filter(transaction -> transaction.getCreatedAt().isEqual(end)).toList();

        User user=userRepository.findByAccountNumber(accountNumber);
        String customerName= user.getFirstName()+" "+user.getLastName();

        Rectangle statementSize=new Rectangle(PageSize.A4);
        Document document=new Document(statementSize);
        log.info("Setting size of document");
        OutputStream outputStream=new FileOutputStream(FILE);
        PdfWriter.getInstance(document,outputStream);
        document.open();

        PdfPTable bankInfoTable= new PdfPTable(1);
        PdfPCell bankName=new PdfPCell(new Phrase("The Free Company"));
        bankName.setBorder(0);
        bankName.setBackgroundColor(BaseColor.CYAN);
        bankName.setPadding(20f);

        PdfPCell bankAddress=new PdfPCell(new Phrase("12,Baker Street, London"));
        bankAddress.setBorder(0);
        bankInfoTable.addCell(bankName);
        bankInfoTable.addCell(bankAddress);

        PdfPTable statementInfo= new PdfPTable(2);
        PdfPCell customerInfo= new PdfPCell(new Phrase("Start Date :"+ startDate));
        customerInfo.setBorder(0);
        PdfPCell statement=new PdfPCell(new Phrase("STATEMENT OF ACCOUNT"));
        statement.setBorder(0);
        PdfPCell stopDate= new PdfPCell(new Phrase("End Date: "+ endDate));
        stopDate.setBorder(0);
        PdfPCell name=new PdfPCell(new Phrase("Customer Name : "+ customerName));
        name.setBorder(0);
        PdfPCell space=new PdfPCell();
        space.setBorder(0);
        PdfPCell address=new PdfPCell(new Phrase("Customer Address: "+user.getAddress()));
        address.setBorder(0);

        PdfPTable transactionTable=new PdfPTable(4);
        PdfPCell date=new PdfPCell(new Phrase("DATE"));
        date.setBackgroundColor(BaseColor.CYAN);
        date.setBorder(0);
        PdfPCell transactionType=new PdfPCell(new Phrase("TRANSACTION TYPE"));
        transactionType.setBackgroundColor(BaseColor.CYAN);
        transactionType.setBorder(0);
        PdfPCell amount=new PdfPCell(new Phrase("AMOUNT"));
        amount.setBackgroundColor(BaseColor.CYAN);
        amount.setBorder(0);
        PdfPCell status=new PdfPCell(new Phrase("STATUS"));
        status.setBackgroundColor(BaseColor.CYAN);
        status.setBorder(0);

        transactionTable.addCell(date);
        transactionTable.addCell(transactionType);
        transactionTable.addCell(amount);
        transactionTable.addCell(status);

        transactionList.forEach(transaction -> {
            transactionTable.addCell(new Phrase(transaction.getCreatedAt().toString()));
            transactionTable.addCell(new Phrase(transaction.getTransactionType()));
            transactionTable.addCell(new Phrase(transaction.getAmount().toString()));
            transactionTable.addCell(new Phrase(transaction.getStatus()));
        });

        statementInfo.addCell(customerInfo);
        statementInfo.addCell(statement);
        statementInfo.addCell(stopDate);
        statementInfo.addCell(endDate);
        statementInfo.addCell(name);
        statementInfo.addCell(space);
        statementInfo.addCell(address);

        document.add(bankInfoTable);
        document.add(statementInfo);
        document.add(transactionTable);

        document.close();

        EmailDetails emailDetails=EmailDetails.builder()
                .recipient(user.getEmail())
                .subject("STATEMENT OF ACCOUNT")
                .messageBody("Kindly find your requested account statement attachment.")
                .attachment(FILE)
                .build();
        emailService.sendEmailWithAttachment(emailDetails);

        return transactionList;
    }

}
