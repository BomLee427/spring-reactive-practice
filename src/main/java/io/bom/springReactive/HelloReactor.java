package io.bom.springReactive;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class HelloReactor {
    public static void main(String[] args) {
        fluxJust();
    }

    private static void fluxJust() {
        Flux<String> sequence = Flux.just("Hello", "Reactor");

        sequence
            .map(data -> data.toLowerCase())
            .subscribe(data -> System.out.println(data));
    }
    private static void monoJust() {
        Mono.just("Hello, Reactor!")
            .subscribe(message -> System.out.println(message));
    }
}
