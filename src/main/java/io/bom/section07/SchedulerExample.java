package io.bom.section07;

import io.bom.utils.Logger;
import io.bom.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class SchedulerExample {
    public static void main(String[] args) {
//        schedulerImmediateExample01();
//        schedulerImmediateExample02();
//        schedulerSingleExample01();
//        schedulerSingleExample02();
//        schedulerNewBoudnedElasticExample();
        schedulerNewParallelExample();
    }

    /**
     * Schedulers.immediate() 를 적용하기 전의 예제
     * 2개의 parallel 스레드가 생성된다.
     */
    private static void schedulerImmediateExample01() {
        Flux.fromArray(new Integer[] {1, 3, 5, 7})
            .publishOn(Schedulers.parallel())
            .filter(data -> data > 3)
            .doOnNext(data -> Logger.doOnNext("filter", data))
            .publishOn(Schedulers.parallel())
            .map(data -> data * 10)
            .doOnNext(data -> Logger.doOnNext("map", data))
            .subscribe(Logger::onNext);

        TimeUtils.sleep(200L);
    }
    /**
     * Schedulers.immediate() 를 적용한 예제
     * 현재 스레드가 할당된다.
     */
    private static void schedulerImmediateExample02() {
        Flux.fromArray(new Integer[] {1, 3, 5, 7})
            .publishOn(Schedulers.parallel())
            .filter(data -> data > 3)
            .doOnNext(data -> Logger.doOnNext("filter", data))
            .publishOn(Schedulers.immediate())
            .map(data -> data * 10)
            .doOnNext(data -> Logger.doOnNext("map", data))
            .subscribe(Logger::onNext);

        TimeUtils.sleep(200L);
    }

    /**
     * Scheduler.single() 예제
     * 하나의 스레드를 재사용한다.
     */
    private static void schedulerSingleExample01() {
        doTaskSingle("task1")
            .subscribe(Logger::onNext);

        doTaskSingle("task2")
            .subscribe(Logger::onNext);

        TimeUtils.sleep(200L);
    }

    /**
     * Scheduler.newSingle() 예제
     * 첫 번째 구독에서 Scheduler.single()을 적용하고,
     * 두 번째 구독에서 새 single 스레드를 생성해 사용한다.
     */
    private static void schedulerSingleExample02() {
        doTaskNewSingle("task1")
            .subscribe(Logger::onNext);

        doTaskNewSingle("task2")
            .subscribe(Logger::onNext);

        TimeUtils.sleep(200L);
    }

    /**
     * Scheduler.newBoundedElastic() 예제
     */
    private static void schedulerNewBoudnedElasticExample() {
        Scheduler scheduler = Schedulers.newBoundedElastic(2, 2, "I/O Thread");
        Mono<Integer> mono =
            Mono
                .just(1)
                .subscribeOn(scheduler);

        log.info("# start");

        mono.subscribe(
            data -> {
                Logger.onNext("Subscribe 1 doing", data);
                TimeUtils.sleep(3000L);
                Logger.onNext("Subscribe 1 done", data);
            });

        mono.subscribe(
            data -> {
                Logger.onNext("Subscribe 2 doing", data);
                TimeUtils.sleep(3000L);
                Logger.onNext("Subscribe 2 done", data);
            });

        // 앞선 작업이 처리되고 있기 때문에 Queue에 대기하게 된다.
        mono.subscribe(
            data -> {
                Logger.onNext("Subscribe 3 doing", data);
                TimeUtils.sleep(3000L);
                Logger.onNext("Subscribe 3 done", data);
            });

        mono.subscribe(
            data -> Logger.onNext("Subscribe 4 doing", data));

        mono.subscribe(
            data -> Logger.onNext("Subscribe 5 doing", data));

        mono.subscribe(
            data -> Logger.onNext("Subscribe 6 doing", data));

        // Queue가 가득 찼을 때 새 구독이 발생하면 Exception이 발생한다.
//        mono.subscribe(
//            data -> Logger.onNext("Subscribe 7 doing", data));

        // newBoundedElastic()으로 생성한 스레드는 Deamon이 아닌 User thread가 된다.
        // 작업 종료 후 바로 스레드를 종료하고 싶다면 dispose() 메서드를 호출해 스레드를 종료시켜야 한다.
//        TimeUtils.sleep(4000L);
//        scheduler.dispose();
    }

    /**
     * Scheduler.newParallel() 예제
     */
    private static void schedulerNewParallelExample() {
        Mono<Integer> mono =
            Mono
                .just(1)
                .subscribeOn(Schedulers.newParallel("Parallel", 4, true));

        mono.subscribe(
            data -> {
                TimeUtils.sleep(5000L);
                Logger.onNext("Subscribe 1", data);
            });

        mono.subscribe(
            data -> {
                TimeUtils.sleep(4000L);
                Logger.onNext("Subscribe 2", data);
            });

        mono.subscribe(
            data -> {
                TimeUtils.sleep(3000L);
                Logger.onNext("Subscribe 3", data);
            });

        mono.subscribe(
            data -> {
                TimeUtils.sleep(2000L);
                Logger.onNext("Subscribe 4", data);
            });

        TimeUtils.sleep(6000L);
    }




    private static Flux<Integer> doTaskSingle(String taskName) {
        return Flux.fromArray(new Integer[] {1, 3, 5, 7})
            .publishOn(Schedulers.single())
            .filter(data -> data > 3)
            .doOnNext(data -> Logger.doOnNext(taskName, "filter", data))
            .map(data -> data * 10)
            .doOnNext(data -> Logger.doOnNext(taskName, "map", data));
    }

    private static Flux<Integer> doTaskNewSingle(String taskName) {
        return Flux.fromArray(new Integer[] {1, 3, 5, 7})
            .doOnNext(data -> Logger.doOnNext(taskName, "fromArray", data))
            .publishOn(Schedulers.newSingle("new-single", true))
            .filter(data -> data > 3)
            .doOnNext(data -> Logger.doOnNext(taskName, "filter", data))
            .map(data -> data * 10)
            .doOnNext(data -> Logger.doOnNext(taskName, "map", data));
    }
}
