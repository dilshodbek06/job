package com.example.backend.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqAgent {

    private String phone;

    private String telegram_token;

    private String chatId;

    private String password;

    private String category_id;

    private String role_name;

    private Timestamp expiration_date;
}
