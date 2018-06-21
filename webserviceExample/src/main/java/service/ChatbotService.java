package service;

import entity.BotResponse;
import org.springframework.validation.annotation.Validated;

@Validated
public interface ChatbotService {
    public BotResponse chat(String msg, String botname,boolean learning);
}
