package io.bom.section07;


import io.bom.utils.Logger;
import io.bom.utils.TimeUtils;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * parallel()은 ParallelFlux 타입을 리턴한다.
 * ParallelFlux 타입은 runOn(Scheduler) 메서드를 통해 워크로드(작업량)를 분할한다.
 * 분할된 워크로드는 Rail이라는 논리적 작업 단위에서 병렬로 처리된다.
 */
public class ParallelExample {
    public static void main(String[] args) {
        parallelExample01();

    }

    /**
     * parallel() 기본 예제
     * - runOn() Operator를 추가해야만 병렬로 작업이 실행된다.
     * - 기본적으로 CPU 코어 갯수 내에서 worker thread를 할당한다.
     * - 매개변수로 스레드 갯수를 지정할 수 있다.
     */
    private static void parallelExample01() {
        Flux.fromArray(new Integer[]{1, 3, 5, 7, 9, 11, 13, 15, 17, 19})
//            .parallel()
            .parallel(4)        // 매개변수로 병렬 작업을 처리할 Thread의 수를 지정 가능하다.
            .runOn(Schedulers.parallel())       // runOn() Operator를 추가해야만 병렬로 작업이 실행된다.
            .subscribe(Logger::onNext);

        TimeUtils.sleep(100L);

        // 병렬 처리 시 생성되는 스레드 갯수는 CPU의 논리 코어 갯수와 관련이 있다.
    }
}
