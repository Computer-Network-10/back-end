package kau.server.hackerton.controller;

import kau.server.hackerton.domain.dto.ChatMessageRequestDto;
import kau.server.hackerton.domain.entity.Member;
import kau.server.hackerton.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    @MessageMapping({"/chat"})
    public void sendMessage(ChatMessageRequestDto requestDto){
        chatService.sendMessage(requestDto);
        log.info("메세지 전송"+ requestDto.toString());
    }

    @GetMapping("api/chat/member")
    public ResponseEntity<List<Member>> getMemberList(){
        try{
            List<Member> members = chatService.getMemberList();
            return new ResponseEntity<>(members,HttpStatus.OK);
        } catch (Exception e){
            log.info("오류 "+e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
    }

    @DeleteMapping("api/chat/{member}")
    public ResponseEntity<String> deleteMember(@PathVariable("member")String member){
        try{
            chatService.deleteMember(member);

            return new ResponseEntity<>("Delete Success", HttpStatus.OK);
        }catch (Exception e){
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NO_CONTENT);
        }
    }

    @EventListener
    public void connected(SessionConnectedEvent event){
        try {
            chatService.connectedEvent(event);
            log.info("연결되었다");
        }catch (Exception ignored){
            ignored.printStackTrace();
            log.info("오류 발생 "+ignored.getMessage());
        }
    }

    @EventListener
    public void disconnected(SessionDisconnectEvent event){
//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//        String username = (String) headerAccessor.getSessionAttributes().get("username");
        log.info("연결이 끊겼다."+event.toString());
    }
}
