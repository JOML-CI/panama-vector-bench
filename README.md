# How to run

With JAVA_HOME and PATH pointing to a Panama vectorIntrinsics build, run:

```
./mvnw package && java --add-modules jdk.incubator.vector -jar target/bench.jar
```

Without having a local Panama vectorIntrinsics build, run:
```
./ci.sh
```
This will shallow-clone the [GitHub mirror of the Panama vectorIntrinsics branch](https://github.com/openjdk/panama-vector/tree/vectorIntrinsics), build the JDK and execute the benchmarks using it. Make sure your system fulfills the [OpenJDK build requirements](https://github.com/openjdk/panama-vector/blob/vectorIntrinsics/doc/building.md). See the section "Clean Ubuntu Setup" below for a clean Ubuntu setup.
The space requirements for such a cloned and fully built JDK is ~5.6GB, which will reside inside of the panama-vector directory.
In addition, the hsdis utility library is built and installed into the JDK's lib directory.

## Clean Ubuntu Setup (tested on Ubuntu 20.04)

```
sudo apt install -y openjdk-14-jdk-headless \
                    libasound2-dev \
                    libfontconfig1-dev \
                    libcups2-dev \
                    libx11-dev \
                    libxext-dev \
                    libxrender-dev \
                    libxrandr-dev \
                    libxtst-dev \
                    libxt-dev \
                    git \
                    zip \
                    unzip \
                    automake \
                    autoconf \
                    build-essential
```

## Seeing the disassembly

In order to see the x86 code generated by the JIT compiler for all methods, run:
```
java --add-modules jdk.incubator.vector -XX:+UnlockDiagnosticVMOptions -XX:CompileCommand=print,*Matrix*.* -cp target/bench.jar bench.C2
```
The x86 code is then printed to stdout. This requires the hsdis utility library available in the $JAVA_HOME/lib directory, as is provided by `./ci.sh`.

# Results

## Intel Xeon E-2176M
### With Default Bounds Checks
```
Benchmark                Mode  Cnt   Score   Error  Units
Bench.mul128LoopArr      avgt    5  32.460 ± 0.374  ns/op
Bench.mul128LoopBB       avgt    5  41.958 ± 0.500  ns/op
Bench.mul128UnrolledArr  avgt    5  29.327 ± 0.328  ns/op
Bench.mul128UnrolledBB   avgt    5  37.489 ± 0.718  ns/op
Bench.mul256Arr          avgt    5  26.298 ± 0.079  ns/op
Bench.mul256BB           avgt    5  29.945 ± 0.063  ns/op
Bench.mulJniAVX          avgt    5  13.552 ± 0.031  ns/op
Bench.mulJniSSE          avgt    5  14.050 ± 0.089  ns/op
Bench.mulScalar          avgt    5  18.729 ± 0.075  ns/op
Bench.mulScalarFma       avgt    5  14.567 ± 0.254  ns/op
Bench.noopJni            avgt    5  10.811 ± 0.037  ns/op
```
### With -Djdk.incubator.vector.VECTOR_ACCESS_OOB_CHECK=0
```
Benchmark                Mode  Cnt   Score   Error  Units
Bench.mul128LoopArr      avgt    5  21.213 ± 0.153  ns/op
Bench.mul128LoopBB       avgt    5  26.547 ± 0.087  ns/op
Bench.mul128UnrolledArr  avgt    5  20.644 ± 0.040  ns/op
Bench.mul128UnrolledBB   avgt    5  29.801 ± 0.725  ns/op
Bench.mul256Arr          avgt    5  33.160 ± 0.266  ns/op
Bench.mul256BB           avgt    5  26.643 ± 0.271  ns/op
Bench.mulJniAVX          avgt    5  12.957 ± 0.074  ns/op
Bench.mulJniSSE          avgt    5  14.316 ± 0.105  ns/op
Bench.mulScalar          avgt    5  19.798 ± 0.133  ns/op
Bench.mulScalarFma       avgt    5  15.421 ± 1.361  ns/op
Bench.noopJni            avgt    5  10.744 ± 0.103  ns/op
```
### With -Djdk.incubator.vector.VECTOR_ACCESS_OOB_CHECK=0 and AbstractShuffle.checkIndexes_Use_VECTOR_ACCESS_OOB_CHECK.patch
See: https://mail.openjdk.java.net/pipermail/panama-dev/2020-May/009302.html
```
Benchmark                     Mode  Cnt  Score   Error  Units
Bench.Matrix4f_storeU         avgt    5   2.614 ± 0.023  ns/op
Bench.Matrix4fvArr_storeU     avgt    5   2.797 ± 0.020  ns/op
Bench.Matrix4fvArr_storeV256  avgt    5   2.257 ± 0.042  ns/op
Bench.mul128LoopArr           avgt    5   8.584 ± 0.136  ns/op
Bench.mul128LoopBB            avgt    5  18.107 ± 0.148  ns/op
Bench.mul128UnrolledArr       avgt    5   9.158 ± 0.114  ns/op
Bench.mul128UnrolledBB        avgt    5  16.129 ± 0.250  ns/op
Bench.mul256Arr               avgt    5   8.519 ± 0.043  ns/op
Bench.mul256BB                avgt    5  10.390 ± 0.056  ns/op
Bench.mulAffineScalarFma      avgt    5  11.271 ± 0.188  ns/op
Bench.mulJniAVX               avgt    5  13.562 ± 0.044  ns/op
Bench.mulJniSSE               avgt    5  14.509 ± 0.641  ns/op
Bench.mulScalar               avgt    5  19.276 ± 0.755  ns/op
Bench.mulScalarFma            avgt    5  15.644 ± 0.083  ns/op
Bench.noopJni                 avgt    5  10.702 ± 0.028  ns/op
```

## Intel Xeon Platinum 8124M
### With Default Bounds Checks
```
Benchmark                Mode  Cnt   Score   Error  Units
Bench.mul128LoopArr      avgt    5  33.849 ± 1.017  ns/op
Bench.mul128LoopBB       avgt    5  39.956 ± 0.686  ns/op
Bench.mul128UnrolledArr  avgt    5  32.195 ± 1.176  ns/op
Bench.mul128UnrolledBB   avgt    5  40.582 ± 0.736  ns/op
Bench.mul256Arr          avgt    5  35.740 ± 0.614  ns/op
Bench.mul256BB           avgt    5  33.070 ± 0.042  ns/op
Bench.mulJniAVX          avgt    5  14.457 ± 0.004  ns/op
Bench.mulJniSSE          avgt    5  14.460 ± 0.025  ns/op
Bench.mulScalar          avgt    5  22.179 ± 0.104  ns/op
Bench.mulScalarFma       avgt    5  16.444 ± 0.024  ns/op
Bench.noopJni            avgt    5  12.394 ± 0.044  ns/op
```
### With -Djdk.incubator.vector.VECTOR_ACCESS_OOB_CHECK=0 and AbstractShuffle.checkIndexes_Use_VECTOR_ACCESS_OOB_CHECK.patch
See: https://mail.openjdk.java.net/pipermail/panama-dev/2020-May/009302.html
```
Benchmark                     Mode  Cnt   Score   Error  Units
Bench.Matrix4f_storeU         avgt    5   3.208 ± 0.006  ns/op
Bench.Matrix4fvArr_storeU     avgt    5   3.435 ± 0.003  ns/op
Bench.Matrix4fvArr_storeV256  avgt    5   2.554 ± 0.467  ns/op
Bench.Matrix4fvArr_storeV512  avgt    5  37.813 ± 0.245  ns/op
Bench.mul128LoopArr           avgt    5   9.560 ± 0.003  ns/op
Bench.mul128LoopBB            avgt    5  18.107 ± 0.050  ns/op
Bench.mul128UnrolledArr       avgt    5   9.410 ± 0.015  ns/op
Bench.mul128UnrolledBB        avgt    5  17.423 ± 0.084  ns/op
Bench.mul256Arr               avgt    5   9.511 ± 0.007  ns/op
Bench.mul256BB                avgt    5  12.237 ± 0.012  ns/op
Bench.mulAffineScalarFma      avgt    5  12.428 ± 0.059  ns/op
Bench.mulJniAVX               avgt    5  14.754 ± 0.006  ns/op
Bench.mulJniSSE               avgt    5  14.458 ± 0.001  ns/op
Bench.mulScalar               avgt    5  22.421 ± 0.036  ns/op
Bench.mulScalarFma            avgt    5  16.734 ± 0.009  ns/op
Bench.noopJni                 avgt    5  12.393 ± 0.004  ns/op
```
