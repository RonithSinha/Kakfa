package com.kafka.messaging_app.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.user.SimpSession;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

import com.kafka.messaging_app.dto.Message;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class Receiver {

	private final SimpMessageSendingOperations messagingTemplate;
	private final SimpUserRegistry userRegistry;

	public Receiver(SimpMessageSendingOperations messagingTemplate, SimpUserRegistry userRegistry) {
		this.messagingTemplate = messagingTemplate;
		this.userRegistry = userRegistry;
	}

	@KafkaListener(topics = "messaging", groupId = "chat")
	public void consume(Message chatMessage) {
		log.info("Received message from Kafka: " + chatMessage);
		for (SimpUser user : userRegistry.getUsers()) {
			for (SimpSession session : user.getSessions()) {
				if (!session.getId().equals(chatMessage.getSessionId())) {
					messagingTemplate.convertAndSendToUser(session.getId(), "/topic/public", chatMessage);
				}
			}
		}
	}
}