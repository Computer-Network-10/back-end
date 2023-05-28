package kau.server.hackerton.domain.dto;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChatMessageRequestDto {

    private Long channelId;
    private String sender;
    private String chat;
}
