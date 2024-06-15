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
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Slf4j
public class MonoExample {

    /**
     * Mono는 HTTP Request를 처리하기에 적합하다.
     */

    public static void main(String[] args) {
        monoExample01();
        monoExample02();
        monoExample03();
        monoPracticeBySelf();
    }

    /**
     * 1개의 data를 consume하는 예제
     */
    private static void monoExample01() {
        Mono.just("Hello Reactor!") // publisher
            .subscribe(data -> log.info("# emmited data: {}", data)); // subscriber / Just 입장에서의 Downstream
    }

    /**
     * data consume 없이 oncomplete signal만 consume하는 예제
     */
    private static void monoExample02() {
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
    private static void monoExample03() {

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

    /**
     * Mono 활용 예제 - WebClient 버전
     * Truble shooting: Mac silicon일 경우 error, netty dns resolver 의존성 추가하여 해결
     * https://github.com/netty/netty/issues/11020#issuecomment-1879138714
     * resolver만으로는 해결되지 않아서 netty all을 일단 끌어왔더니 작동함. 나중에 해결책 알아볼것
     * 오류는 안 나는데 response 내용이 전혀 찍히지 않는다. request가 발송되긴 하는 걸까? 이것도 디버깅해볼것
     * webclient는 급하게 찾아서 코딩중이라 그냥 내가 잘못 코딩했을 확률 높을듯,,,
     */
    private static void monoPracticeBySelf() {

        // WebClient
        WebClient webClient = WebClient.builder()
            .baseUrl("http://worldtimeapi.org")
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString())
            .build();

        log.info("webclient");

        Mono<String> mono = webClient.get().uri("/api/timezone/Asia/Seoul")
            .retrieve()
            .bodyToMono(String.class).log();

        log.info("mono retrieved");

        mono.subscribe(
            data -> log.info("# emitted data: " + data),
            error -> log.error("# error occured: " + error.getMessage()),
            () -> log.info("# emitted oncomplete signal")
        );

        log.info("method ended");
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
