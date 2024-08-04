package io.bom.section05;

public class BackPressureStrategyNote {

    /**
     * 리액트에서의 Backpressure 전략
     */

    /**
     * 1. IGNORE
     * Backpressure를 아예 적용하지 않음
     */

    /**
     * 2. ERROR 전략
     * Downstream으로 전달할 데이터가 버퍼에 가득 찰 경우 Exception을 발생
     */

    /**
     * 3. DROP
     * Downstream으로 전달할 데이터가 버퍼에 가득 찰 경우 버퍼 밖에서 대기하는 가장 먼저 Emit된 데이터부터 Drop시키는 전략
     */

    /**
     * 4. LATEST
     * Downstream으로 전달할 데이터가 버퍼에 가득 찰 경우 Emit되는 데이터는 버퍼가 비워질 때까지 Drop되다가,
     * 버퍼가 비워지면 버퍼 밖에서 대기하는 가장 나중에 Emit된 데이터부터 버퍼에 채우는 전략
     */

    /**
     * 5. BUFFER
     * Downstream으로 전달할 데이터가 버퍼에 가득 찰 경우 버퍼 안에 있는 데이터를 Drop시키는 전략
     * 어떤 데이터를 Drop시킬지에 따라 다시 여러 가지로 나뉜다.
     *
     * 5-1. DROP-LATEST
     * 버퍼가 가득 찼을 때, 가장 최근에 버퍼에 들어온 데이터부터 Drop시키는 전략
     *
     * 5-2. DROP-OLDEST
     * 버퍼가 가득 찼을 때, 가장 먼저 버퍼에 들어온 데이터부터 Drop시키는 전략
     */
}
