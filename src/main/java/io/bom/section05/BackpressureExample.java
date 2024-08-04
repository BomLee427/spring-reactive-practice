package io.bom.section05;

import io.bom.utils.Logger;
import io.bom.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

@Slf4j
public class BackpressureExample {

    /**
     * BaseSubscriber를 구현하여 Subscriber 측에서 데이터 요청 갯수를 제어할 수 있다.
     */
    public static void main(String[] args) {
//        backpressureExample1();
        backpressureExample2();
    }

    /**
     * Subscriber 쪽에서 데이터 요청 갯수를 제어하는 예제
     */
    private static void backpressureExample1() {
        Flux.range(1, 5)
            .doOnNext(Logger::doOnNext) // doOnNext(): 업스트림이 emit한 데이터를 출력
            .doOnRequest(Logger::doOnRequest) // doOnRequest(): 다운스트림에서 요청한 데이터 갯수를 출력
            .subscribe(new BaseSubscriber<Integer>() { // 요청 데이터의 갯수 조절하는 Subscriber
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        request(1);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
                        TimeUtils.sleep(2000L);
                        Logger.onNext("hookOnNext() -> " + value);
                        request(1);
                    }
            });
    }

    /**
     * Subscriber가 처리 가능한 만큼의 request 갯수를 조절하는 Backpressure 예제
     */

    // Subscriber가 처리 완료한 데이터의 갯수
    public static int count = 0;
    private static void backpressureExample2() {
        Flux.range(1, 5)
            .doOnNext(Logger::doOnNext)
            .doOnRequest(Logger::doOnRequest)
            .subscribe(new BaseSubscriber<Integer>() {
                   @Override
                   protected void hookOnSubscribe(Subscription subscription) {
                       request(2);
                   }

                   @Override
                   protected void hookOnNext(Integer value) {
                       count++;
                       Logger.onNext(value);

                       // 처리 능력 상한선에 도달하면
                       if (count == 2) {
                           TimeUtils.sleep(2000L);      // 2초 쉬었다가
                           request(2);      // 다시 2개를 request한다.
                           count = 0;
                       }
                   }
           });
    }
}
