package com.seecoder.BlueWhale.util;

public interface ILock {
				// 加锁
				boolean tryLock(long timeoutSec);
				// 释放锁
				void unlock();
}
