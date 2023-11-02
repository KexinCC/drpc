package org.xiaoheshan;


import java.util.concurrent.atomic.LongAdder;

public class IdGenerator {

    //    // 单机版本的线程安全
    //    private static final LongAdder longAdder = new LongAdder();
    //
    //    public static long getId() {
    //        longAdder.increment();
    //        return longAdder.sum();
    //    }

    // 雪花算法 -- 世界上没有一片雪花是相同的

    // 机房号 (数据中心)            5bit
    // 机器号                      5bit
    // 时间戳                      42bit
    // 序列号                      12bit

    // 起始时间
    public static final long START_STAMP = DateUtil.get("2022-01-01").getTime();
    public static final long DATA_CENTER_BIT = 5;
    public static final long MACHINE_BIT = 5;
    public static final long SEQUENCE_BIT = 12;

    // 最大值
    public static final long DATA_CENTER_MAX = ~(-1 << DATA_CENTER_BIT);
    public static final long MACHINE_MAX = ~(-1 << MACHINE_BIT);
    public static final long SEQUENCE_MAX = ~(-1 << SEQUENCE_BIT);

    // 定义左移位数
    // 时间戳 42  机房号 5  序列号 5
    public static final long TIMESTAMP_LEFT = DATA_CENTER_BIT + MACHINE_BIT + SEQUENCE_BIT;
    public static final long DATA_CENTER_LEFT = MACHINE_BIT + SEQUENCE_BIT;
    public static final long MACHINE_LEFT = SEQUENCE_BIT;


    private long dataCenterId;
    private long machineId;
    private LongAdder sequenceId = new LongAdder();    // 时钟回拨的问题
    private long lastTimeStamp = -1L;

    public IdGenerator(long dataCenterId, long machineId) {
        if (dataCenterId > DATA_CENTER_MAX || machineId > MACHINE_MAX) {
            throw new IllegalArgumentException("传入的编号参数不合法");
        }
        this.dataCenterId = dataCenterId;
        this.machineId = machineId;
    }

    public long getId() {
        // 处理时间戳 -- 使用差值可以延长使用时间
        long currentTime = System.currentTimeMillis();
        long timeStamp = currentTime - START_STAMP;

        // 时钟回拨
        if (timeStamp < lastTimeStamp) {
            throw new RuntimeException("服务器进行了时钟回拨");
        }

        // 高并发情况
        if (timeStamp == lastTimeStamp) {
            sequenceId.increment();
            if (sequenceId.sum() >= SEQUENCE_MAX) {
                timeStamp = getNextTimeStamp();
                sequenceId.reset();
            }
        } else {
            sequenceId.reset();
        }
        // 执行结束将时间戳赋值
        lastTimeStamp = timeStamp;

        long sequence = sequenceId.sum();
        return timeStamp << TIMESTAMP_LEFT
                | dataCenterId << DATA_CENTER_LEFT
                | machineId << MACHINE_LEFT
                | sequence;
    }

    private long getNextTimeStamp() {
        long current = System.currentTimeMillis();
        while (current == lastTimeStamp) {
            // 直到下一个时间戳
            current = System.currentTimeMillis();
        }
        return current;
    }


    public static void main(String[] args) throws IllegalAccessException {

        IdGenerator idGenerator = new IdGenerator(1,2);

        for (int i = 0; i < 1000; i++) {
            new Thread(() -> {
                System.out.println(idGenerator.getId());
            }).start();
        }
    }
}
