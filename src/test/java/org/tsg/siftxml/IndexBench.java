package org.tsg.siftxml;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class IndexBench {

    @Benchmark
    public Index create() {
        Index index = new Index();
        index.create(User.class);
        return index;
    }

    public class User {
        public String name;
        public String email;
    }
}
