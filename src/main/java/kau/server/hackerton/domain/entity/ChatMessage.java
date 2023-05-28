package kau.server.hackerton.domain.entity;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChatMessage {

    private Timestamp time;
    private Long type;
    private String sender;
    private String chat;

}
