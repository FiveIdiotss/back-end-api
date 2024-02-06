package mementee.mementee.api.controller.boardDTO;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import mementee.mementee.api.domain.enumtype.BoardType;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@AllArgsConstructor
public class WriteBoardRequest {
    private String title;
    private String content;
    private BoardType boardType;

    @DateTimeFormat(pattern = "HH:mm:ss")
    @JsonSerialize(using = LocalTimeSerializer.class)
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    private LocalTime startTime;                // 예약 가능한 시작 시간

    @DateTimeFormat(pattern = "HH:mm:ss")
    @JsonSerialize(using = LocalTimeSerializer.class)
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    private LocalTime lastTime;                 // 예약 가능한 종료 시간

    private List<DayOfWeek> availableDays;    // 상담 가능한 요일

    public static class LocalTimeSerializer extends JsonSerializer<LocalTime> {
        @Override
        public void serialize(LocalTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            String formattedTime = value.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            gen.writeString(formattedTime);
        }
    }

    public static class LocalTimeDeserializer extends JsonDeserializer<LocalTime> {
        @Override
        public LocalTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            String timeString = p.getValueAsString();
            return LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm:ss"));
        }
    }
}
