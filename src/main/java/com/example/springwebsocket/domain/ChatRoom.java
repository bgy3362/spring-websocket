package com.example.springwebsocket.domain;

import com.example.springwebsocket.service.ChatService;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
public class ChatRoom {
    @Id @GeneratedValue
    private Long id;

    private String roomId; // 채팅방 아이디
    private String name; // 채팅방 이름

    @Transient
    private Set<WebSocketSession> sessions = new HashSet<>();

//    @Builder
    public ChatRoom(String roomId, String name){
        this.roomId = roomId;
        this.name = name;
    }

    public void handleAction(WebSocketSession session, ChatMessage chatContent, ChatService service) {
        // message 에 담긴 타입을 확인한다.
        // 이때 message 에서 getType 으로 가져온 내용이
        // ChatDTO 의 열거형인 MessageType 안에 있는 ENTER 과 동일한 값이라면
        if (chatContent.getType().equals(ChatMessage.MessageType.ENTER)) {
            // sessions 에 넘어온 session 을 담고,
            sessions.add(session);

            // message 에는 입장하였다는 메시지를 띄운다
            chatContent.setMessage(chatContent.getSender() + " 님이 입장하셨습니다");
            sendMessage(chatContent, service);
//        } else if (message.getType().equals(ChatDTO.MessageType.TALK)) {
//            message.setMessage(message.getMessage());
//            sendMessage(message, service);
//        }
        }
        sendMessage(chatContent, service);
    }

    public <T> void sendMessage(T message, ChatService service) {
        sessions.parallelStream().forEach(session -> service.sendMessage(session, message));
    }
}