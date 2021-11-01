package com.github.yzl.cita.blockchain;

/**
 * CITA 中的 quota，类似于以太坊中的 gas，表示发送交易消耗的账户金额
 */
public final class Quotas {

  private static final long DEFAULT_QUOTA = 200000L;
  private static final long ESTIMATE_BASE_VALUE = 21000L;
  private static final int SINGLE_BYTE_QUOTA = 200;
  private static final int DEFAULT_MAX_QUOTA = 10000000;
  private static final float DEFAULT_ADD_QUOTA_PERCENT = 0.1f; // 最小Quota相加的比率值。预估Quota = 最小Quota + 最小Quota*10%

  private Quotas() {
  }

  /**
   * 预估 Quota
   *
   * @param byteLength 字节长度
   * @return 预估的 Quota 值
   */
  public static long estimateQuota(int byteLength) {
    if (byteLength <= 0) {
      return DEFAULT_QUOTA;
    }
    long minQuota = ESTIMATE_BASE_VALUE + (byteLength * SINGLE_BYTE_QUOTA);
    long estimate = minQuota + (int)(minQuota * DEFAULT_ADD_QUOTA_PERCENT);
    return estimate < DEFAULT_MAX_QUOTA ? estimate : DEFAULT_MAX_QUOTA;
  }
}
