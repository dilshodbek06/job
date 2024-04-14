package com.example.backend.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqClient {
    private String phone;

    private String password;

    private String category_id;

    private Timestamp expiration_date;
}
