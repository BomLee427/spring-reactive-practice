package io.bom.section04;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
public class ColdSequenceExample {
    public static void main(String[] args) {
        Flux<String> coldFlux = Flux.fromIterable(Arrays.asList("RED", "BLUE", "YELLOW"))
            .map(String::toLowerCase);

        coldFlux.subscribe(color -> log.info("# Sub1: {}", color));
        log.info("------------------------------------");
        coldFlux.subscribe(color -> log.info("# Sub2: {}", color));
    }
}
