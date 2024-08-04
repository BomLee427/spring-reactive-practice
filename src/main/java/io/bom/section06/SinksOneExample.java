package io.bom.section06;

import static reactor.core.publisher.Sinks.EmitFailureHandler.FAIL_FAST;

import io.bom.utils.Logger;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

public class SinksOneExample {

    public static void main(String[] args) {
//        sinksOneExample01();
        sinksOneExample02();
    }

    /**
     * Sinks.One 으로 한 건의 데이터를 에밋하는 예제
     */
    private static void sinksOneExample01() {

        // Sinks.One의 구현 객체를 얻음
        Sinks.One<String> sinkOne = Sinks.one();

        // Sinks.One 객체를 구독하기 위해 Mono로 변환
        Mono<String> mono = sinkOne.asMono();

        // 실제 데이터를 emit
        sinkOne.emitValue("Hello Reactor!", FAIL_FAST);

        mono.subscribe(data -> Logger.onNext("Subscriber1 : {}", data));
        mono.subscribe(data -> Logger.onNext("Subscriber2 : {}", data));
    }

    /**
     * Sinks.One 으로 두 건의 데이터를 에밋하는 예제
     */
    private static void sinksOneExample02() {

        // emit된 데이터 중 단 한 개의 데이터만 Subscriber에게 전달. 나머지는 Drop 됨
        Sinks.One<String> sinkOne = Sinks.one();
        Mono<String> mono = sinkOne.asMono();

        // Hi는 Drop된다
        sinkOne.emitValue("Hello Reactor!", FAIL_FAST);
        sinkOne.emitValue("Hi Reactor!", FAIL_FAST);

        mono.subscribe(data -> Logger.onNext("Subscriber1 : {}", data));
        mono.subscribe(data -> Logger.onNext("Subscriber2 : {}", data));
    }
}
