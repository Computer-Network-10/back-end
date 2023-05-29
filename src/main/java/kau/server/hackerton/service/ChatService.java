package kau.server.hackerton.service;

import jakarta.persistence.NoResultException;
import kau.server.hackerton.domain.dto.ChatMessageRequestDto;
import kau.server.hackerton.domain.entity.ChatMessage;
import kau.server.hackerton.domain.entity.Member;
import kau.server.hackerton.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import javax.swing.text.html.Option;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChatService{

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatRepository chatRepository;

    public void sendMessage(ChatMessageRequestDto requestDto) {

        ChatMessage chatMessage = ChatMessage.builder()
                .type(1L)
                .time(setTime())
                .sender(requestDto.getSender())
                .chat(requestDto.getChat())
                .build();

        simpMessagingTemplate.convertAndSend("/sub/chat/" + requestDto.getChannelId(), chatMessage);
    }

    public void connectedEvent(SessionConnectedEvent event) throws Exception{

        MessageHeaders messageHeaders = event.getMessage().getHeaders();
        GenericMessage<?> genericMessage = messageHeaders.get("simpConnectMessage", GenericMessage.class);

        if (genericMessage != null) {
            Map nativeHeaders = genericMessage.getHeaders().get("nativeHeaders", Map.class);

            String id = nativeHeaders.get("channelId").toString();
            id = id.substring(1, id.length()-1);
            Long channelId = Long.parseLong(id);

            String sender = nativeHeaders.get("sender").toString();
            sender = sender.substring(1, sender.length()-1);

            try {
                if (chatRepository.findMemberByName(sender).isPresent()) log.info("연결은 되었지만 이미 데이터가 존재하므로 공지x");
                else throw new Exception("Header error!");
            } catch (NoResultException | EmptyResultDataAccessException e) {
                e.printStackTrace();
                log.info(e.getMessage());
                ChatMessage chatMessage = ChatMessage.builder()
                        .type(0L)
                        .time(setTime())
                        .sender(sender)
                        .chat(sender + "님이 입장하셨습니다!")
                        .build();
                log.info("입장: " + chatMessage.toString());

                chatRepository.addMember(new Member(null, sender));
                simpMessagingTemplate.convertAndSend("/sub/chat/" + channelId, chatMessage);
            }
        }
    }

    public List<Member> getMemberList() throws Exception{
        Optional<List<Member>> members = chatRepository.getMemberList();
        if (members.isEmpty()) throw new Exception("He or she is not a member");
        return members.get();
    }

    public void deleteMember(String name) throws Exception{
        try {
            Optional<Member> member = chatRepository.findMemberByName(name);
            chatRepository.deleteMember(member.get());

            ChatMessage chatMessage = ChatMessage.builder()
                    .type(-1L)
                    .time(setTime())
                    .sender(name)
                    .chat(name + "님이 퇴장하셨습니다!")
                    .build();
            simpMessagingTemplate.convertAndSend("/sub/chat/1", chatMessage);

        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    public void disconnectedEvent(SessionDisconnectEvent event) {

    }

    private Timestamp setTime(){
        ZoneId zoneId = ZoneId.of("Asia/Seoul");
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
        return Timestamp.valueOf(zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

}