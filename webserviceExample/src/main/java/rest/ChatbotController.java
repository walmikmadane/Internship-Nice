package rest;

import entity.BotResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.ChatbotService;

@RestController
@RequestMapping("/chatbotmanager/chatbot/startchat")
public class ChatbotController implements ErrorController
{
    @Autowired
    private ChatbotService chatbotService;

    @SuppressWarnings("rawtypes")
    @CrossOrigin
    @GetMapping(value = "")
    public ResponseEntity<BotResponse> startChat(@RequestParam(name = "msg", required = true) String msg, @RequestParam(value = "botname",required = true)String botname,@RequestParam(value="learning",required = false)boolean learning)
    {
        BotResponse tmp=chatbotService.chat(msg,botname,learning);
        return ResponseEntity.ok(tmp);

    }

    @Override
    public String getErrorPath() {
        return null;
    }
}
