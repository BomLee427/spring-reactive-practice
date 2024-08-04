package io.bom.section05;

import io.bom.utils.Logger;
import io.bom.utils.TimeUtils;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class BackPressureStrategyExample {

    public static void main(String[] args) {
//        bpStrategyExample01();
//        bpStrategyExample02();
//        bpStrategyExample03();
        bpStrategyExample04();
    }

    /**
     * Unbounded Request일 경우, Downstream에 Backpressure ERROR 전략을 적용하는 예제
     *  -> Downstream으로 전달할 데이터가 버퍼에 가득 찰 경우 Exception 발생
     */
    private static void bpStrategyExample01() {
        Flux
            .interval(Duration.ofMillis(1L))
            .onBackpressureError()                     /// Error 전략 적용
            .doOnNext(Logger::doOnNext)                /// Emmit된 데이터 로깅
            .publishOn(Schedulers.parallel())          /// Scheduler: 리액터에서 스레드를 활용하게 해주는 컴포넌트. Pub을 별도 스레드로 분리
            .subscribe(data -> {
                    TimeUtils.sleep(5L);    /// Sub이 Pub보다 처리속도가 느린 상황을 시뮬레이션함
                    Logger.onNext(data);
                },
                error -> Logger.onError(error)
            );
        TimeUtils.sleep(2000L);
    }
    /// 데이터를 255까지만 발행하고 멈추는 이유는 버퍼 기본 사이즈가 256이기 때문에
    /// 참고: https://gksdudrb922.tistory.com/m/319


    /**
     * Unbounded Request일 경우, Downstream에 Backpressure DROP 전략을 적용하는 예제
     *  -> Downstream으로 전달할 데이터가 버퍼에 가득 찰 경우 버퍼 밖에서 대기하는 먼저 Emit된 데이터부터 Drop
     */
    private static void bpStrategyExample02() {
        Flux
            .interval(Duration.ofMillis(1L))
            .onBackpressureDrop(dropped -> log.info("# dropped : {}", dropped))
            .doOnNext(Logger::doOnNext)
            .publishOn(Schedulers.parallel())
            .subscribe(data -> {
                    TimeUtils.sleep(5L);
                    Logger.onNext(data);
                },
                Logger::onError);

        TimeUtils.sleep(2000L);
    }

    /**
     * Unbounded Request일 경우, Downstream에 Backpressure LATEST 전략을 적용하는 예제
     *  -> Downstream으로 전달할 데이터가 버퍼에 가득 찰 경우,
     *     버퍼 밖에서 폐기되지 않고 대기 중인 데이터 중 가장 최근에 Emit된 데이터부터 버퍼에 채우는 전략
     */
    private static void bpStrategyExample03() {
        Flux
            .interval(Duration.ofMillis(1L))
            .onBackpressureLatest()
            .publishOn(Schedulers.parallel())
            .subscribe(data -> {
                    TimeUtils.sleep(5L);
                    Logger.onNext(data);
                },
                Logger::onError
            );

        TimeUtils.sleep(2000L);
    }

    /**
     * DROP 전략과 LATEST 전략의 차이점
     * DROP: 버퍼가 가득 차 있을 때 데이터가 들어오는 그 즉시 폐기
     * LATEST: 버퍼가 가득 차 있을 때 데이터가 들어오면 바로 폐기되지 않고 대기했다가 다음 데이터가 들어오는 시점에 폐기
     */

    /**
     * Unbounded Request일 경우, Downstream에 Backpressure BUFFER 전략을 적용하는 예제
     *
     * DROP_LATEST 전략: Downstream으로 전달할 데이터가 버퍼에 가득 찰 경우,
     *     버퍼 안에 있는 데이터 중 가장 최근에 들어온 데이터부터 Drop시키는 전략
     *
     * DROP_OLDEST 전략: Downstream으로 전달할 데이터가 버퍼에 가득 찰 경우,
     *     버퍼 안에 있는 데이터 중 가장 먼저 들어온 데이터부터 Drop시키는 전략
     */
    private static void bpStrategyExample04() {
        Flux
            .interval(Duration.ofMillis(300L))
            .doOnNext(data -> log.info("# emitted by original Flux : {}", data))
            .onBackpressureBuffer(2,
                dropped -> log.info("# Overflow & dropped : {}", dropped),
//                BufferOverflowStrategy.DROP_LATEST        // DROP_LATEST
                BufferOverflowStrategy.DROP_OLDEST          // DROP_OLDEST
            )
            .doOnNext(data -> log.info("# emitted by Buffer : {}", data))
            .publishOn(Schedulers.parallel(), false, 1) // prefetch: 추가되는 스레드에서 사용하는 일종의 버퍼 같은 개념
            .subscribe(data -> {
                    TimeUtils.sleep(1000L);
                    Logger.onNext(data);
                },
                Logger::onError
            );

        TimeUtils.sleep(3000L);
    }
}
