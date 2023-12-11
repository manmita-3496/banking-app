package com.example.bank.dto;

import lombok.*;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailDetails {
    private String recipient;
    private String messageBody;
    private String subject;
    private String attachment;

}
