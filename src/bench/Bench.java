package bench;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static java.nio.ByteBuffer.allocateDirect;
import static java.nio.ByteOrder.nativeOrder;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;
import static org.openjdk.jmh.annotations.Scope.Benchmark;

@State(Benchmark)
@OutputTimeUnit(NANOSECONDS)
@Warmup(iterations = 5, time = 1000, timeUnit = MILLISECONDS)
@Measurement(iterations = 5, time = 1000, timeUnit = MILLISECONDS)
@BenchmarkMode(AverageTime)
@Fork(value = 1, jvmArgsAppend = {"-XX:UseAVX=3", "-Djdk.incubator.vector.VECTOR_ACCESS_OOB_CHECK=0"})
public class Bench {
    private final Matrix4f m4 = new Matrix4f();
    private final Matrix4fvBB m4vbb = new Matrix4fvBB();
    private final Matrix4fvArr m4varr = new Matrix4fvArr();
    private final ByteBuffer bb = allocateDirect(16<<2).order(nativeOrder());
    private final FloatBuffer fb = bb.asFloatBuffer();

    @Benchmark
    public void Matrix4f_invert() {
        m4.invert(m4);
    }

    @Benchmark
    public void Matrix4fvArr_invert128() {
        m4varr.invert128(m4varr);
    }

    @Benchmark
    public void Matrix4f_storePutFB() {
        m4.storePutFB(fb);
    }

    @Benchmark
    public void Matrix4f_storePutBB() {
        m4.storePutBB(bb);
    }

    @Benchmark
    public void Matrix4fvArr_storePutFB() {
        m4varr.storePut(fb);
    }

    @Benchmark
    public void Matrix4fvArr_storeU() {
        m4varr.storeU(bb);
    }

    @Benchmark
    public void Matrix4fvArr_storeV256() {
        m4varr.storeV256(bb);
    }

    @Benchmark
    public void Matrix4fvArr_storeV512() {
        m4varr.storeV512(bb);
    }

    @Benchmark
    public void Matrix4f_storeU() {
        m4.storeU(bb);
    }

    @Benchmark
    public Object mulScalar() {
        return m4.mul(m4);
    }

    @Benchmark
    public Object mulScalarFma() {
        return m4.mulFma(m4);
    }

    @Benchmark
    public Object mulAffineScalarFma() {
        return m4.mulAffineFma(m4);
    }

    @Benchmark
    public Object mul256Arr() {
        return m4varr.mul256(m4varr);
    }

    @Benchmark
    public Object mul128UnrolledArr() {
        return m4varr.mul128Unrolled(m4varr);
    }

    @Benchmark
    public Object mul128LoopArr() {
        return m4varr.mul128Loop(m4varr);
    }

    @Benchmark
    public Object mul256BB() {
        return m4vbb.mul256(m4vbb);
    }

    @Benchmark
    public Object mul128UnrolledBB() {
        return m4vbb.mul128Unrolled(m4vbb);
    }

    @Benchmark
    public Object mul128LoopBB() {
        return m4vbb.mul128Loop(m4vbb);
    }

    public static void main(String[] args) throws Exception {
        new Runner(new OptionsBuilder()
            .include(Bench.class.getName())
            //.addProfiler(LinuxPerfProfiler.class)
            .build()
        ).run();
    }
}
