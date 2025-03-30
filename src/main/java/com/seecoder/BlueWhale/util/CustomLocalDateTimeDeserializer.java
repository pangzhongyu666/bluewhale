package com.seecoder.BlueWhale.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;

public class CustomLocalDateTimeDeserializer extends StdDeserializer<LocalDateTime> {

				public CustomLocalDateTimeDeserializer() {
								super(LocalDateTime.class);
				}


				@Override
				public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
								JsonNode node = p.getCodec().readTree(p);
								int year = node.get("year").asInt();
								int month = node.get("monthValue").asInt();
								int day = node.get("dayOfMonth").asInt();
								int hour = node.get("hour").asInt();
								int minute = node.get("minute").asInt();
								int second = node.get("second").asInt();
								int nano = node.get("nano").asInt();
								return LocalDateTime.of(year, month, day, hour, minute, second, nano);
				}
}
