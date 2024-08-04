package io.bom.section07;

import io.bom.utils.Logger;
import io.bom.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;


@Slf4j
public class SchedulerOperatorExample {
    public static void main(String[] args) {
//        schedulerOperatorExample01();
//        schedulerOperatorExample02();
//        schedulerOperatorExample03();
        schedulerOperatorExample04();
    }

    /**
     * 스케쥴러를 적용하지 않은 상태의 예제
     * !! Operator 체인에서, 최초의 스레드는 subscribe()가 호출되는 "scope"에 있는 스레드이다. !!
     */
    private static void schedulerOperatorExample01() {
        Flux.fromArray(new Integer[] {1, 3, 5, 7})
            .filter(data -> data > 3)
            .map(data -> data * 10)
            .subscribe(Logger::onNext);
    }

    /**
     * publishOn() 예제
     * - Operator 체인에서 publishOn()이 호출되면 publishOn() 이후의 Operator 체인은
     *   다음 publishOn()을 만나기 전까지 이전 publishOn()에서 지정한 스레드에서 실행된다.
     */
    private static void schedulerOperatorExample02() {
        Flux.fromArray(new Integer[] {1, 3, 5, 7})
            .doOnNext(data -> Logger.doOnNext("fromArray", data))
            .publishOn(Schedulers.parallel())       // Downstream : publishOn()을 기준으로 아래쪽에 있는 Operator 체인
            .filter(data -> data > 3)
            .doOnNext(data -> Logger.doOnNext("filter", data))
            .publishOn(Schedulers.parallel())       // Operator 체인이 publishOn()을 만나면 여기서 지정한 스레드에서 Downstream이 실행된다.
            .map(data -> data * 10)
            .doOnNext(data -> Logger.doOnNext("map", data))
            .subscribe(Logger::onNext);

        TimeUtils.sleep(500L);
    }

    /**
     * subscribeOn() 예제
     * - subscribeOn() 은 구독 직후에 실행될 스레드를 지정한다.
     *   즉 원본 Publisher의 실행 스레드를 subscribeOn()에 지정된 스레드로 바꾼다.
     */
    private static void schedulerOperatorExample03() {

        Flux.fromArray(new Integer[] {1, 3, 5, 7})      // 최상위 Upstream, 즉 데이터를 emit하는 데이터 소스
            .subscribeOn(Schedulers.boundedElastic())
            .doOnNext(data -> Logger.doOnNext("fromArray", data))
            .filter(data -> data > 3)
            .doOnNext(data -> Logger.doOnNext("filter", data))
            .map(data -> data * 10)
            .doOnNext(data -> Logger.doOnNext("map", data))
            .subscribe(Logger::onNext);

        TimeUtils.sleep(500L);
    }

    /**
     * subscribeOn() + publishOn() 예제
     * - subscribeOn()이 지정한 스레드에서 최상위 Upstream이 실행되고, publishOn()을 만나는 시점부터 새로운 스레드에서 Downstream이 실행된다.
     * - subscribeOn()은 어디에 위치하든 상관 없이 첫 번째 publishOn()을 만나기 전까지의 최상위 Upstream을 변경한다.
     */
    private static void schedulerOperatorExample04() {

        Flux.fromArray(new Integer[] {1, 3, 5, 7})
            .doOnNext(data -> Logger.doOnNext("fromArray", data))
            .filter(data -> data > 3)
            .doOnNext(data -> Logger.doOnNext("filter", data))
            .publishOn(Schedulers.parallel())       // Operator 체인이 publishOn()을 만나면 여기서 지정한 스레드에서 Downstream이 실행된다.
            .map(data -> data * 10)
            .doOnNext(data -> Logger.doOnNext("map", data))
            .subscribeOn(Schedulers.boundedElastic())   // subscribeOn()은 어디에 위치하든 상관 없이 처음 publishOn()을 만나기 전까지의 최상위 Upstream을 변경한다.
            .subscribe(Logger::onNext);

        TimeUtils.sleep(500L);
    }

}
