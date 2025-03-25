package com.seecoder.BlueWhale.serviceImpl.strategy;

public class Context {
    private final CalculateStrategy calculateStrategy;

    public Context(CalculateStrategy calculateStrategy){
        this.calculateStrategy = calculateStrategy;
    }

    public Double executeStrategy(Double price){
        return calculateStrategy.calculate(price);
    }
}
