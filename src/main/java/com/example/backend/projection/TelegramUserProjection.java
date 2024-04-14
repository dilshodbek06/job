package com.example.backend.projection;

import org.springframework.beans.factory.annotation.Value;

public interface TelegramUserProjection {
    @Value("#{target.telegram_token}")
    String getTelegramToken();

    @Value("#{target.chat_id}")
    String getChatId();
}
