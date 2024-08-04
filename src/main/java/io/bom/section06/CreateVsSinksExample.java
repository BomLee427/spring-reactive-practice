package io.bom.section06;

import io.bom.utils.Logger;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class CreateVsSinksExample {

    public static void main(String[] args) {
        try {
            programmaticCreateExample();
            /**
             20:20:29.740 [boundedElastic-1] INFO io.bom.section06.CreateVsSinksExample -- # create() : task 1 result
             20:20:29.742 [boundedElastic-1] INFO io.bom.section06.CreateVsSinksExample -- # create() : task 2 result
             20:20:29.742 [boundedElastic-1] INFO io.bom.section06.CreateVsSinksExample -- # create() : task 3 result
             20:20:29.742 [boundedElastic-1] INFO io.bom.section06.CreateVsSinksExample -- # create() : task 4 result
             20:20:29.742 [boundedElastic-1] INFO io.bom.section06.CreateVsSinksExample -- # create() : task 5 result
             20:20:29.742 [parallel-2] INFO io.bom.section06.CreateVsSinksExample -- # map() : task 1 result success!
             20:20:29.742 [parallel-2] INFO io.bom.section06.CreateVsSinksExample -- # map() : task 2 result success!
             20:20:29.742 [parallel-2] INFO io.bom.section06.CreateVsSinksExample -- # map() : task 3 result success!
             20:20:29.742 [parallel-2] INFO io.bom.section06.CreateVsSinksExample -- # map() : task 4 result success!
             20:20:29.742 [parallel-2] INFO io.bom.section06.CreateVsSinksExample -- # map() : task 5 result success!
             20:20:29.742 [parallel-1] INFO io.bom.utils.Logger -- # onNext(): task 1 result success!
             20:20:29.743 [parallel-1] INFO io.bom.utils.Logger -- # onNext(): task 2 result success!
             20:20:29.743 [parallel-1] INFO io.bom.utils.Logger -- # onNext(): task 3 result success!
             20:20:29.743 [parallel-1] INFO io.bom.utils.Logger -- # onNext(): task 4 result success!
             20:20:29.743 [parallel-1] INFO io.bom.utils.Logger -- # onNext(): task 5 result success!
             */


//            programmaticSinksExample();
            /**
             20:19:48.313 [Thread-0] INFO io.bom.section06.CreateVsSinksExample -- # emitted : 1
             20:19:48.415 [Thread-1] INFO io.bom.section06.CreateVsSinksExample -- # emitted : 2
             20:19:48.521 [Thread-2] INFO io.bom.section06.CreateVsSinksExample -- # emitted : 3
             20:19:48.624 [Thread-3] INFO io.bom.section06.CreateVsSinksExample -- # emitted : 4
             20:19:48.729 [Thread-4] INFO io.bom.section06.CreateVsSinksExample -- # emitted : 5
             20:19:48.877 [parallel-2] INFO io.bom.section06.CreateVsSinksExample -- # map : task 1 result success!
             20:19:48.877 [parallel-2] INFO io.bom.section06.CreateVsSinksExample -- # map : task 2 result success!
             20:19:48.877 [parallel-2] INFO io.bom.section06.CreateVsSinksExample -- # map : task 3 result success!
             20:19:48.877 [parallel-2] INFO io.bom.section06.CreateVsSinksExample -- # map : task 4 result success!
             20:19:48.877 [parallel-1] INFO io.bom.utils.Logger -- # onNext(): task 1 result success!
             20:19:48.877 [parallel-2] INFO io.bom.section06.CreateVsSinksExample -- # map : task 5 result success!
             20:19:48.877 [parallel-1] INFO io.bom.utils.Logger -- # onNext(): task 2 result success!
             20:19:48.877 [parallel-1] INFO io.bom.utils.Logger -- # onNext(): task 3 result success!
             20:19:48.877 [parallel-1] INFO io.bom.utils.Logger -- # onNext(): task 4 result success!
             20:19:48.877 [parallel-1] INFO io.bom.utils.Logger -- # onNext(): task 5 result success!
             */
        } catch (InterruptedException e) {}
    }


    /**
     * create() Operator를 사용하는 예제
     * - 일반적으로 데이터 생성은 단일 스레드에서 진행한다. (멀티 스레드에서도 가능함)
     * - 데이터 emit은 create() 내부에서 수행한다.
     * - Backpressure를 적용할 수 있다.
     */
    private static void programmaticCreateExample() throws InterruptedException {
        int tasks = 6;

        Flux
            .create((FluxSink<String> sink) -> {        // create() 내부에서만 FluxSink API를 이용해 emit한다.
                IntStream
                    .range(1, tasks)
                    .forEach(n -> sink.next(doTask(n)));
            })
            .subscribeOn(Schedulers.boundedElastic())
            .doOnNext(n -> log.info("# create() : {}", n))
            .publishOn(Schedulers.parallel())
            .map(result -> result + " success!")
            .doOnNext(n -> log.info("# map() : {}", n))
            .publishOn(Schedulers.parallel())
            .subscribe(Logger::onNext);

        Thread.sleep(500L);
    }

    /**
     * Sinks를 사용하는 예제
     * - 멀티 스레드 환경에서 데이터를 발행해도 Thread-safe 하다.
     */
    private static void programmaticSinksExample() throws InterruptedException {
        int tasks = 6;

        // Sinks에서는 여러 형태로 데이터를 emit할 수 있는 스펙을 정의하고 있다.
        // 아래 코드는 Sinks 객체를 생성한다.
        Sinks.Many<String> unicastSink = Sinks.many().unicast().onBackpressureBuffer();

        // Sinks 객체를 Flux로 변환한다.
        Flux<String> fluxView = unicastSink.asFlux();

        // Sinks 객체의 메서드를 호출해 내부/외부 상관 없이 데이터를 emit할 수 있다.
        IntStream
            .range(1, tasks)
            .forEach(n -> {     // 매개변수 n에 다수 스레드에서 접근한다.
                try {
                    new Thread(() -> {
                        unicastSink.emitNext(doTask(n), Sinks.EmitFailureHandler.FAIL_FAST); // Failure Handler 제공
                        log.info("# emitted : {}", n);
                    }).start();
                    Thread.sleep(100L);
                } catch (InterruptedException e) {}
            });

        fluxView
            .publishOn(Schedulers.parallel())
            .map(result -> result + " success!")
            .doOnNext(n -> log.info("# map : {}", n))
            .publishOn(Schedulers.parallel())
            .subscribe(Logger::onNext);

        Thread.sleep(200L);
    }

    private static String doTask(int taskNumber) {
        return "task " + taskNumber + " result";
    }
}
