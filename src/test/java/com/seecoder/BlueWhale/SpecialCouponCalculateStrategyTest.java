package com.seecoder.BlueWhale;

import com.seecoder.BlueWhale.serviceImpl.strategy.Context;
import com.seecoder.BlueWhale.serviceImpl.strategy.SpecialCouponCalculateStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SpecialCouponCalculateStrategyTest {
				@Test
				void testCalculate() {

								Context context = new Context(new SpecialCouponCalculateStrategy());
								// Test case 1: Price less than 100
								Double price1 = 50.0;
								Double expectedPrice1 = 50.0 * 0.95;
								Assertions.assertEquals(expectedPrice1, context.executeStrategy(price1));

								// Test case 2: Price between 100 and 200
								Double price2 = 150.0;
								Double expectedPrice2 = 100.0 * 0.95 + 50.0 * 0.9;
								Assertions.assertEquals(expectedPrice2, context.executeStrategy(price2));

								// Test case 3: Price between 200 and 300
								Double price3 = 250.0;
								Double expectedPrice3 = 100.0 * 0.95 + 100.0 * 0.9 + 50.0 * 0.85;
								Assertions.assertEquals(expectedPrice3,context.executeStrategy(price3));

								// Test case 4: Price between 300 and 400
								Double price4 = 350.0;
								Double expectedPrice4 = 100.0 * 0.95 + 100.0 * 0.9 + 100.0 * 0.85 + 50.0 * 0.8;
								Assertions.assertEquals(expectedPrice4, context.executeStrategy(price4));

								// Test case 5: Price between 400 and 500
								Double price5 = 450.0;
								Double expectedPrice5 = 100.0 * 0.95 + 100.0 * 0.9 + 100.0 * 0.85 + 100.0 * 0.8 + 50.0 * 0.75;
								Assertions.assertEquals(expectedPrice5, context.executeStrategy(price5));

								// Test case 6: Price greater than 500
								Double price6 = 600.0;
								Double expectedPrice6 = 100.0 * 0.95 + 100.0 * 0.9 + 100.0 * 0.85 + 100.0 * 0.8 + 100.0 * 0.75 + 100.0 * 0.7;
								Assertions.assertEquals(expectedPrice6, context.executeStrategy(price6));

				}
}
