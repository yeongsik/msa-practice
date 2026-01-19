package com.common.util.id;

import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Enumeration;
import lombok.extern.slf4j.Slf4j;

/**
 * Twitter Snowflake Algorithm implementation for distributed ID generation.
 * <p>
 * Structure:
 * 1 bit: Unused (sign bit)
 * 41 bits: Timestamp (milliseconds since custom epoch)
 * 5 bits: Datacenter ID
 * 5 bits: Worker ID
 * 12 bits: Sequence
 * </p>
 */
@Slf4j
public class SnowflakeIdGenerator {

    private static final long UNUSED_BITS = 1L;
    private static final long EPOCH_BITS = 41L;
    private static final long DATACENTER_ID_BITS = 5L;
    private static final long WORKER_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;

    private static final long MAX_DATACENTER_ID = (1L << DATACENTER_ID_BITS) - 1;
    private static final long MAX_WORKER_ID = (1L << WORKER_ID_BITS) - 1;
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;

    // Custom Epoch (e.g., 2024-01-01 00:00:00 UTC)
    private static final long CUSTOM_EPOCH = 1704067200000L;

    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

    private final long datacenterId;
    private final long workerId;
    private long lastTimestamp = -1L;
    private long sequence = 0L;

    /**
     * Constructor with automatic worker/datacenter ID detection.
     */
    public SnowflakeIdGenerator() {
        this.datacenterId = getDatacenterId();
        this.workerId = getMaxWorkerId(datacenterId, MAX_WORKER_ID);
        log.info("SnowflakeIdGenerator initialized with datacenterId: {}, workerId: {}", datacenterId, workerId);
    }

    /**
     * Constructor with manual worker/datacenter ID configuration.
     *
     * @param datacenterId Datacenter ID
     * @param workerId     Worker ID
     */
    public SnowflakeIdGenerator(long datacenterId, long workerId) {
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("Datacenter ID can't be greater than %d or less than 0",
                    MAX_DATACENTER_ID));
        }
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(String.format("Worker ID can't be greater than %d or less than 0",
                    MAX_WORKER_ID));
        }
        this.datacenterId = datacenterId;
        this.workerId = workerId;
        log.info("SnowflakeIdGenerator initialized with datacenterId: {}, workerId: {}", datacenterId, workerId);
    }

    /**
     * Generate the next unique ID.
     *
     * @return Unique ID
     */
    public synchronized long nextId() {
        long currentTimestamp = timestamp();

        if (currentTimestamp < lastTimestamp) {
            throw new IllegalStateException("Clock moved backwards. Refusing to generate id for "
                    + (lastTimestamp - currentTimestamp) + " milliseconds");
        }

        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                currentTimestamp = waitNextMillis(currentTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = currentTimestamp;

        return ((currentTimestamp - CUSTOM_EPOCH) << TIMESTAMP_LEFT_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    private long waitNextMillis(long currentTimestamp) {
        while (currentTimestamp <= lastTimestamp) {
            currentTimestamp = timestamp();
        }
        return currentTimestamp;
    }

    private long timestamp() {
        return Instant.now().toEpochMilli();
    }

    private long getDatacenterId() {
        try {
            return (long) (new SecureRandom().nextInt((int) MAX_DATACENTER_ID + 1));
        } catch (Exception e) {
            return 1L;
        }
    }

    private long getMaxWorkerId(long datacenterId, long maxWorkerId) {
        StringBuilder mac = new StringBuilder();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                byte[] hardwareAddress = networkInterface.getHardwareAddress();
                if (hardwareAddress != null) {
                    for (byte b : hardwareAddress) {
                        mac.append(String.format("%02X", b));
                    }
                }
            }
        } catch (Exception e) {
            // Ignore
        }
        return (long) (mac.toString().hashCode() & 0xFFFF) % (maxWorkerId + 1);
    }
}
