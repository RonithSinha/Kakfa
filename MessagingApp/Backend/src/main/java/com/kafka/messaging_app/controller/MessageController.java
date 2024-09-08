package com.kafka.messaging_app.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import com.kafka.messaging_app.dto.Message;
import com.kafka.messaging_app.service.Sender;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class MessageController {

    private final Sender sender;
    private final SimpMessageSendingOperations messagingTemplate;

    public MessageController(Sender sender, SimpMessageSendingOperations messagingTemplate) {
        this.sender = sender;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.send-message")
    public void sendMessage(@Payload Message chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        chatMessage.setSessionId(headerAccessor.getSessionId());
        sender.send("messaging", chatMessage);
        log.info("Sending message to /topic/public: " + chatMessage);
        messagingTemplate.convertAndSend("/topic/public", chatMessage);
        log.info("Message sent");
    }

    @MessageMapping("/chat.add-user")
    @SendTo("/topic/public")
    public Message addUser(
            @Payload Message chatMessage,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        if (headerAccessor.getSessionAttributes() != null) {
            headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        }

        return chatMessage;
    }
}