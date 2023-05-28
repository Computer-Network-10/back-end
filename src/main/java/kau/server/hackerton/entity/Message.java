package kau.server.hackerton.entity;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Message {
    private String sender;
    private Integer channelId;
    private String chat;
    private String time;
}
