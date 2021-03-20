package com.scau.chatservcie.util;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.hash.Funnel;
import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import util.RedisUtil;

import javax.annotation.Resource;

/**
 * @program: pdd
 * @description: 布隆过滤器工具类
 * @create: 2020-12-02 22:29
 **/
@Component
public class BloomFilterUtil {

    @Resource
    private RedisUtil redisUtil;

    private static BloomFilterHelper bloomFilterHelper=new BloomFilterHelper();
    /**
     * 根据给定的布隆过滤器添加值
     */
    public  void addByBloomFilter(String key, String value) {
        Preconditions.checkArgument(bloomFilterHelper != null, "bloomFilterHelper不能为空");
        int[] offset = bloomFilterHelper.murmurHashOffset(value);
        for (int i : offset) {
            redisUtil.setBit(key, i);
        }
    }

    /**
     * 根据给定的布隆过滤器判断值是否存在
     */
    public  boolean includeByBloomFilter(String key, String value) {
        Preconditions.checkArgument(bloomFilterHelper != null, "bloomFilterHelper不能为空");
        int[] offset = bloomFilterHelper.murmurHashOffset(value);
        for (int i : offset) {
            if (!redisUtil.getBit(key, i)) {
                return false;
            }
        }

        return true;
    }

    public static class BloomFilterHelper  {

        private int numHashFunctions;

        private int bitSize;

        private Funnel<String> funnel;

        public BloomFilterHelper(){

            this((Funnel<String>) (from, into)
                    -> into.putString(from, Charsets.UTF_8).putString(from, Charsets.UTF_8),
                    1500000, 0.00001);
        }

        public BloomFilterHelper(Funnel<String> funnel, int expectedInsertions, double fpp) {
            Preconditions.checkArgument(funnel != null, "funnel不能为空");
            this.funnel = funnel;
            bitSize = optimalNumOfBits(expectedInsertions, fpp);
            numHashFunctions = optimalNumOfHashFunctions(expectedInsertions, bitSize);
        }

        int[] murmurHashOffset(String value) {
            int[] offset = new int[numHashFunctions];

            long hash64 = Hashing.murmur3_128().hashObject(value, funnel).asLong();
            int hash1 = (int) hash64;
            int hash2 = (int) (hash64 >>> 32);
            for (int i = 1; i <= numHashFunctions; i++) {
                int nextHash = hash1 + i * hash2;
                if (nextHash < 0) {
                    nextHash = ~nextHash;
                }
                offset[i - 1] = nextHash % bitSize;
            }

            return offset;
        }

        /**
         * 计算bit数组长度
         */
        private int optimalNumOfBits(long n, double p) {
            if (p == 0) {
                p = Double.MIN_VALUE;
            }
            return (int) (-n * Math.log(p) / (Math.log(2) * Math.log(2)));
        }

        /**
         * 计算hash方法执行次数
         */
        private int optimalNumOfHashFunctions(long n, long m) {
            return Math.max(1, (int) Math.round((double) m / n * Math.log(2)));
        }

    }

}
