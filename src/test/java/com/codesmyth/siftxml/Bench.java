package com.codesmyth.siftxml;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class Bench {
    public static void main(String[] args) throws RunnerException {
        new Runner(new OptionsBuilder()
                .include("SiftXmlBench")
                .warmupIterations(3)
                .measurementIterations(3)
                .forks(1)
                .build()).run();
    }
}
