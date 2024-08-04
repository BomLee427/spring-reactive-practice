package io.bom.section06;

import static reactor.core.publisher.Sinks.EmitFailureHandler.FAIL_FAST;

import io.bom.utils.Logger;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;

@Slf4j
public class SinksManyExample {

    public static void main(String[] args) {
//        sinksManyExample01();
//        sinksManyExample02();
//        sinksManyExample03();
        sinksManyExample04();
    }

    /**
     * Sinks.Many의 Unicast Spec 예제
     * unicast()를 사용하여 하나의 Subscriber에게 데이터를 emit한다.
     */
    private static void sinksManyExample01() {

        // 단 하나의 Subscriber에게만 데이터를 emit할 수 있다.
        Sinks.Many<Integer> unicastSink = Sinks.many().unicast().onBackpressureBuffer();
        Flux<Integer> fluxView = unicastSink.asFlux();

        unicastSink.emitNext(1, FAIL_FAST);
        unicastSink.emitNext(2, FAIL_FAST);

        fluxView.subscribe(data -> Logger.onNext("Subscriber1 : {}", data));

        unicastSink.emitNext(3, FAIL_FAST);

        // 2건 이상의 구독을 시도하면 Exception이 발생
        // java.lang.IllegalStateException: Sinks.many().unicast() sinks only allow a single Subscriber
//        fluxView.subscribe(data -> Logger.onNext("Subscriber2 : {}", data));
    }

    /**
     * Sinks.Many의 Multicast Spec 예제
     * multicast()를 사용하여 하나 이상의 Subscriber에게 데이터를 emit한다.
     */
    private static void sinksManyExample02() {

        // 하나 이상의 Subscriber에게 데이터를 emit할 수 있다.
        Many<Integer> multicastSink = Sinks.many().multicast().onBackpressureBuffer();
        Flux<Integer> fluxView = multicastSink.asFlux();

        multicastSink.emitNext(1, FAIL_FAST);
        multicastSink.emitNext(2, FAIL_FAST);

        fluxView.subscribe(data -> Logger.onNext("Subscriber1 : {}", data));
        fluxView.subscribe(data -> Logger.onNext("Subscriber2 : {}", data));

        // Multicast 스펙은 warm-up 방식으로 동작
        // warm-up: Hot Sequence 동작 방식 중 첫 번째 구독이 발생하는 시점에 데이터 에밋이 시작되는 방식
        multicastSink.emitNext(3, FAIL_FAST);
    }

    /**
     * Sinks.Many의 Multicast-replay Spec 예제
     * replay()를 사용하여 이미 emit된 데이터 중 특정 갯수의 최신 데이터만 전달한다.
     */
    private static void sinksManyExample03() {

        // 구독 이후, emit된 데이터 중에서 최신 데이터 2개만 replay한다.
        Many<Integer> replaySink = Sinks.many().replay().limit(2);
        Flux<Integer> fluxView = replaySink.asFlux();

        replaySink.emitNext(1, FAIL_FAST);
        replaySink.emitNext(2, FAIL_FAST);
        replaySink.emitNext(3, FAIL_FAST);

        fluxView.subscribe(data -> Logger.onNext("Subscriber1 : {}", data));

        replaySink.emitNext(4, FAIL_FAST);

        // 1, 2, 3, 4 중 가장 최신 2개 데이터인 3과 4만 replay된다.
        fluxView.subscribe(data -> Logger.onNext("Subscriber2 : {}", data));
    }

    /**
     * Sinks.Many의 Multicast-replay Spec 중 all() 예제
     */
    private static void sinksManyExample04() {

        // 구독 시점과 상관 없이 emit된 모든 데이터를 replay한다.
        Many<Integer> replaySink = Sinks.many().replay().all();
        Flux<Integer> fluxView = replaySink.asFlux();

        replaySink.emitNext(1, FAIL_FAST);
        replaySink.emitNext(2, FAIL_FAST);

        fluxView.subscribe(data -> Logger.onNext("Subscriber1 : {}", data));
        
        replaySink.emitNext(3, FAIL_FAST);

        fluxView.subscribe(data -> Logger.onNext("Subscriber2 : {}", data));
    }
}
