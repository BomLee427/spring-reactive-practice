package io.bom.section03;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Slf4j
public class MonoExample01 {

    public static void main(String[] args) {
//        justMono();
//        emptyMono();
        monoPracticeWithRestTemplate();
    }

    /**
     * 1개의 data를 consume하는 예제
     */
    private static void justMono() {
        Mono.just("Hello Reactor!") // publisher
            .subscribe(data -> log.info("# emmited data: {}", data)); // subscriber / Just 입장에서의 Downstream
    }

    /**
     * data consume 없이 oncomplete signal만 consume하는 예제
     */
    private static void emptyMono() {
        Mono.empty()
            .subscribe(
                data -> log.info("# emitted data: {}", data), // consumer
                error -> {}, // error consumer
                () -> log.info("# emitted onComplete signal") // complete consumer
            );
    }

    /**
     * Mono 활용 예제 - RestTemplate 버전
     * worldtimeapi.org Open API를 이용해서 서울의 현재 시간을 조회한다.
     */
    private static void monoPracticeWithRestTemplate() {

        URI worldTimeUrl = UriComponentsBuilder.newInstance().scheme("http")
            .host("worldtimeapi.org")
            .port(80)
            .path("/api/timezone/Asia/Seoul")
            .build()
            .encode()
            .toUri();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Mono.just(
            restTemplate.exchange(worldTimeUrl, HttpMethod.GET, new HttpEntity<String>(headers), String.class)
        )
        .map(response -> {
            try {
                return getMapper().readValue(response.getBody(), MonoDto.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        })
        .subscribe(
            data -> log.info("# emitted data: " + data),
            error -> {
                log.error(error.getMessage());
            },
            () -> log.info("# emitted onComplete signal")
        );

    }

    private static ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    public record MonoDto(
        String datetime
    ){}
}
