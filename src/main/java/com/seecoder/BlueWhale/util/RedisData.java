package com.seecoder.BlueWhale.util;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RedisData {
				private Object data;
				@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
				private LocalDateTime expireTime;
}
