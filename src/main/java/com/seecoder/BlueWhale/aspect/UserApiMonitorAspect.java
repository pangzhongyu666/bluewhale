package com.seecoder.BlueWhale.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Aspect
@Component
public class UserApiMonitorAspect {

				// 使用线程安全的Map来存储各个接口的调用统计
				private final Map<String, ApiStats> apiStatsMap = new ConcurrentHashMap<>();

				// 内部类用于存储每个API的统计信息
				private static class ApiStats {
								AtomicLong callCount = new AtomicLong(0);
								AtomicLong totalResponseTime = new AtomicLong(0);
								AtomicLong failureCount = new AtomicLong(0);

								public double getAverageResponseTime() {
												long count = callCount.get();
												return count > 0 ? (double) totalResponseTime.get() / count : 0;
								}
				}

				// 定义切点，监控UserController中的所有方法
				@Pointcut("execution(* com.seecoder.BlueWhale.controller.UserController.*(..))")
				public void userApiMethods() {}

				@Around("userApiMethods()")
				public Object monitorApi(ProceedingJoinPoint joinPoint) throws Throwable {
								String methodName = joinPoint.getSignature().getName();
								ApiStats stats = apiStatsMap.computeIfAbsent(methodName, k -> new ApiStats());

								long startTime = System.currentTimeMillis();
								try {
												Object result = joinPoint.proceed();
												long endTime = System.currentTimeMillis();

												// 更新统计信息
												stats.callCount.incrementAndGet();
												stats.totalResponseTime.addAndGet(endTime - startTime);

												return result;
								} catch (Throwable e) {
												stats.failureCount.incrementAndGet();
												throw e;
								}
				}

				// 获取统计信息的方法
				public Map<String, Map<String, Object>> getApiStats() {
								Map<String, Map<String, Object>> result = new ConcurrentHashMap<>();

								apiStatsMap.forEach((apiName, stats) -> {
												Map<String, Object> apiStats = new ConcurrentHashMap<>();
												apiStats.put("totalCalls",stats.callCount.get() + stats.failureCount.get());
												apiStats.put("SuccessCount", stats.callCount.get());
												apiStats.put("averageResponseTime", stats.getAverageResponseTime());
												apiStats.put("failureCount", stats.failureCount.get());
												apiStats.put("successRate", calculateSuccessRate(stats));

												result.put(apiName, apiStats);
								});

								return result;
				}

				private double calculateSuccessRate(ApiStats stats) {
								long totalCalls = stats.callCount.get();
								if (totalCalls == 0) return 0.0;
								long successCalls = totalCalls - stats.failureCount.get();
								return ((double) successCalls / totalCalls) * 100;
				}
}