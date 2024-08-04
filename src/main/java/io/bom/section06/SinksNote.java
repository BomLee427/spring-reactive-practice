package io.bom.section06;

public class SinksNote {

    /**
     * Sinks란?
     * - Reactive Streams에서 발생할 수 있는 signal을 프로그래밍적으로 push할 수 있는 기능을 가진 Publisher의 일종
     * - Thread-safe 하지 않을 수 있는 Processor보다 더 나은 대안이 된다. (Processor는 Reactor 3.5.0에서 제거)
     * - Sinks.Many 또는 Sinks.One interface를 사용하여 Thread-safe 하게 Signal을 발생시킨다.
     */
}
