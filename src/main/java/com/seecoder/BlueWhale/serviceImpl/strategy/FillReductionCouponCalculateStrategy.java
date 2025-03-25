package com.seecoder.BlueWhale.serviceImpl.strategy;

public class FillReductionCouponCalculateStrategy implements CalculateStrategy{

    Double fill;
    Double reduction;
    public FillReductionCouponCalculateStrategy(Double fill,Double reduction){
        this.fill = fill;
        this.reduction = reduction;
    }
    @Override
    public Double calculate(Double price) {
        return price >= this.fill ? price - this.reduction : price;
    }
}
