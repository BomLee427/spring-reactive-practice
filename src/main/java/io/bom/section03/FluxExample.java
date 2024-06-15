package io.bom.section03;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class FluxExample {
    public static void main(String[] args) {
//        fluxExample01();
//        fluxExample02();
//        fluxExample03();
        fluxExample04();
    }

    /**
     * Flux 기본 예제
     */
    private static void fluxExample01() {
        Flux.just(6, 9, 13) // Mono와 달리 여러 건의 데이터를 입력받을 수 있음
            .map(num -> num % 2)
            .subscribe(remainder ->
                log.info("# remainder: {}", remainder));

        // 생성 오퍼레이터의 인자로 들어오는 데이터를 데이터 소스라고 부름
    }

    /**
     * Flux에서의 Operator chain 사용 예제
     */
    private static void fluxExample02() {
        Flux.fromArray(new Integer[]{3, 6, 7, 9}) // 배열을 데이터 소스로 전달받는 Operator
            .filter(num -> num > 6)
            .map(num -> num * 2)
            .subscribe(multiply -> log.info("# multiply : {}", multiply));
    }

    /**
     * 2개의 Mono를 연결해서 Flux로 변환하는 예제
     */
    private static void fluxExample03() {
        Flux<Object> flux =
            Mono.justOrEmpty(null)                          // justOrEmpty: nullable
                .concatWith(Mono.just("Jobs"));      // concatWith: Mono와 Mono를 연결하는 Operator
        flux.subscribe(data -> log.info("# result : {}", data));
    }

    /**
     * 여러 개의 Flux를 결합해 한 개의 Flux로 만드는 예제
     */
    private static void fluxExample04() {
        Flux.concat( // concat: 파라미터로 전달된 퍼블리셔들을 연결해줌
            Flux.just("Venus", "Venus2"),
            Flux.just("Earth"),
            Flux.just("Mars", "Sun"))
//            .collectList() // 왜 Mono<List>가 될까? -> 각각의 데이터가 연결된 스트림이 아니라, 데이터를 리스트로 묶어서 하나의 데이터로 변환하기 때문임
            .subscribe(planetList -> log.info("# Solar system: {}", planetList));
    }
}
