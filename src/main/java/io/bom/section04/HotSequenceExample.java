package io.bom.section04;

import io.bom.utils.TimeUtils;
import java.time.Duration;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/// Connectable Sequence: 여러 Sub들이 시퀀스를 구독하려면 Connectable Sequence의 connect()를 호출해야 한다.
/// https://tech.kakao.com/posts/350
/// https://projectreactor.io/docs/core/snapshot/reference/apdx-operatorChoice.html#which.multicasting
@Slf4j
public class HotSequenceExample {
    public static void main(String[] args) {
        Flux<String> concertFlux =
            Flux.fromStream(Stream.of("Singer A", "Singer B", "Singer C", "Singer D", "Singer E"))
                .delayElements(Duration.ofSeconds(1)) /// "Signals are delayed and continue on the parallel default Scheduler"
                .share(); /// share: 하나의 원본 Flux를 여러 Subscriber가 공유한다.
                          /// 즉 Cold -> Hot 변환 오퍼레이터.

        concertFlux.subscribe(singer -> log.info(
            "[" + Thread.currentThread().getName()
                + " / " + Thread.activeCount()
                + "] # Subscriber1 is watching {}'s song.", singer));

        TimeUtils.sleep(2500); /// delayElements()를 통해 다른 스레드에서 데이터 스트림이 emit되고 있기 때문에
                                          /// 스레드.sleep()을 사용해도 발행에 영향을 주지 않는 것.
                                          //TODO: 강의에서는 share()때문에 병렬처리가 된다고 했는데... Hot Sequence의 실제 동작에 대해서 조금 더 알아보자

        concertFlux.subscribe(singer -> log.info(
            "[" + Thread.currentThread().getName()
                + " / " + Thread.activeCount()
                + "] # Subscriber2 is watching {}'s song.", singer));

        TimeUtils.sleep(3000); // 메인 스레드를 살려두기 위한 sleep
        log.info("Sequence fin. Current active thread count is " + Thread.activeCount());

        // TODO: 데이터 발행마다 parallel 스레드가 하나씩 생기는데...관련 내용 정확히 확인하기
    }
}
