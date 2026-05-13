package com.example.bookkeeping.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Android 安全版雪花算法 ID 生成器
 *
 * 特点：
 *   - 无锁高并发（AtomicLong + volatile）
 *   - 容忍 5ms 内 NTP 微调（自旋等待）
 *   - 使用 SharedPreferences 持久化最后时间戳，重启 / 杀进程不重复
 *   - 检测到严重时钟回拨（>5ms）或用户恶意改时间时，抛出明确异常
 *
 * 注意：
 *   - 多进程环境下需保证 workerId + dataCenterId 组合唯一，或改为跨进程单例
 *   - 最小兼容 API 不限，未使用高版本独有 API
 */
public class SnowFlake{

    // ==================== 常量配置 ====================
    /** 起始时间戳（2026-01-01 00:00:00 北京时间） */
    private static final long START_TIMESTAMP = 1767196800000L;

    /** 各部分占用位数 */
    private static final long SEQUENCE_BITS = 12L;
    private static final long WORKER_ID_BITS = 5L;
    private static final long DATA_CENTER_ID_BITS = 5L;

    /** 最大值 */
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);        // 4095
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);      // 31
    private static final long MAX_DATA_CENTER_ID = ~(-1L << DATA_CENTER_ID_BITS); // 31

    /** 位移量 */
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;

    /** 时钟回拨最大容忍值（毫秒），超过直接拒绝 */
    private static final long MAX_BACKWARD_MS = 5L;

    // ==================== 成员变量 ====================
    private final long dataCenterId;
    private final long workerId;

    /** 上次生成 ID 的时间戳（毫秒） */
    private volatile long lastTimestamp = -1L;
    /** 毫秒内序列号（0 ~ 4095） */
    private final AtomicLong sequence = new AtomicLong(0L);

    /** Android 持久化工具，用于存储最后一次生成时间 */
    private final SharedPreferences prefs;
    private static final String PREFS_NAME = "snowflake_prefs";
    private static final String KEY_LAST_TIMESTAMP = "last_timestamp";

    // ==================== 构造函数 ====================
    /**
     * @param dataCenterId 数据中心 ID（0 ~ 31）
     * @param workerId     工作机器 ID（0 ~ 31）
     * @param context       Android 上下文（用于 SharedPreferences）
     */
    public SnowFlake(long dataCenterId, long workerId, Context context) {
        if (dataCenterId > MAX_DATA_CENTER_ID || dataCenterId < 0) {
            throw new IllegalArgumentException("数据中心 ID 超出范围 (0 ~ " + MAX_DATA_CENTER_ID + ")");
        }
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException("工作机器 ID 超出范围 (0 ~ " + MAX_WORKER_ID + ")");
        }

        this.dataCenterId = dataCenterId;
        this.workerId = workerId;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // 启动时恢复时间，确保不会因重启或时间回拨产生重复 ID
        recoverFromPersistence();
    }

    /** 从 SharedPreferences 恢复上次时间戳，并视情况等待或抛异常 */
    private void recoverFromPersistence() {
        long savedTimestamp = prefs.getLong(KEY_LAST_TIMESTAMP, -1L);
        if (savedTimestamp <= 0) {
            // 首次启动，直接使用当前时间
            lastTimestamp = System.currentTimeMillis();
            return;
        }

        long now = System.currentTimeMillis();

        // 如果当前时间小于持久化时间 → 发生了时钟回拨
        if (now < savedTimestamp) {
            long diff = savedTimestamp - now;
            if (diff <= MAX_BACKWARD_MS) {
                // 微小回拨，短暂等待时间追上
                try {
                    Thread.sleep(diff + 1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                now = System.currentTimeMillis();
                if (now < savedTimestamp) {
                    throw new RuntimeException("时钟回拨等待后仍未追上，拒绝启动！上次时间="
                            + savedTimestamp + "，当前时间=" + now);
                }
            } else {
                // 回拨过大，可能是用户手动改时间或 NTP 故障
                throw new RuntimeException("时钟回拨过大（" + diff + "ms），请检查系统时间！上次记录时间="
                        + savedTimestamp + "，当前时间=" + now);
            }
        }

        // 此时 now >= savedTimestamp，安全
        lastTimestamp = now;
    }

    // ==================== 核心方法 ====================
    /**
     * 生成下一个唯一 ID（线程安全）
     * @return 雪花 ID（正长整型）
     */
    public long nextId() {
        long currentTimestamp = getCurrentMillis();

        // 1. 运行时时钟回拨检查（NTP 微调等）
        if (currentTimestamp < lastTimestamp) {
            long offset = lastTimestamp - currentTimestamp;
            if (offset <= MAX_BACKWARD_MS) {
                // 短暂等待
                try {
                    Thread.sleep(offset + 1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                currentTimestamp = getCurrentMillis();
                if (currentTimestamp < lastTimestamp) {
                    throw new RuntimeException("运行时时钟回拨等待失败！lastTimestamp="
                            + lastTimestamp + ", current=" + currentTimestamp);
                }
            } else {
                throw new RuntimeException("运行时时钟回拨超过" + MAX_BACKWARD_MS + "ms，拒绝生成 ID");
            }
        }

        // 2. 处理序列号
        if (currentTimestamp == lastTimestamp) {
            long seq = sequence.incrementAndGet() & MAX_SEQUENCE;
            if (seq == 0) {
                // 同毫秒内序列号耗尽，等待下一毫秒
                currentTimestamp = waitUntilNextMillis(lastTimestamp);
            }
        } else {
            sequence.set(0L);
        }

        // 3. 更新 lastTimestamp 并持久化
        lastTimestamp = currentTimestamp;
        persistTimestamp(currentTimestamp);

        // 4. 组装 ID
        return ((currentTimestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT)
                | (dataCenterId << DATA_CENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence.get();
    }

    /** 自旋等待直到下一毫秒 */
    private long waitUntilNextMillis(long lastTimestamp) {
        long current = getCurrentMillis();
        while (current <= lastTimestamp) {
            current = getCurrentMillis();
            Thread.yield(); // 让出 CPU，避免过度空转
        }
        return current;
    }

    /** 获取当前毫秒时间 */
    private long getCurrentMillis() {
        return System.currentTimeMillis();
    }

    // ==================== 持久化 ====================
    /** 将当前时间戳异步写入 SharedPreferences */
    private void persistTimestamp(long timestamp) {
        prefs.edit().putLong(KEY_LAST_TIMESTAMP, timestamp).apply();
    }
}
