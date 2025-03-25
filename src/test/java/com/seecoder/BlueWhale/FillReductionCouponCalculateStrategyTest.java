package com.seecoder.BlueWhale;

import com.seecoder.BlueWhale.serviceImpl.strategy.Context;
import com.seecoder.BlueWhale.serviceImpl.strategy.FillReductionCouponCalculateStrategy;
import com.seecoder.BlueWhale.serviceImpl.strategy.SpecialCouponCalculateStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FillReductionCouponCalculateStrategyTest {

				@Test
				void testCalculate1() {

								Context context = new Context(new FillReductionCouponCalculateStrategy(100.0,10.0));
								Assertions.assertEquals(100.0, context.executeStrategy(110.0));
				}
				@Test
				void testCalculate2() {

								Context context = new Context(new FillReductionCouponCalculateStrategy(200.0,20.0));
								// Test case 1: Price less than threshold
								Double price1 = 150.0;
								Assertions.assertEquals(price1, context.executeStrategy(price1));

								// Test case 2: Price equal to threshold
								Double price2 = 200.0;
								Double expectedPrice2 = 180.0;
								Assertions.assertEquals(expectedPrice2, context.executeStrategy(price2));

								// Test case 3: Price greater than threshold
								Double price3 = 250.0;
								Double expectedPrice3 = 230.0;
								Assertions.assertEquals(expectedPrice3, context.executeStrategy(price3));
				}
}
