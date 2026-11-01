import java.util.Random;

public class Z {

  private static final int HAYSTACK_LENGTH = 100_000;
  private static final int WARMUP_ITERATIONS = 10_000;
  private static final int MEASURE_ITERATIONS = 5_000;
  private static final int MEASURE_ROUNDS = 5;

  private static final char TARGET_CHAR = '~';
  private static final String TARGET_STR = "~";

  public static void main(String[] args) {
    Random rng = new Random(42);
    char[] chars = new char[HAYSTACK_LENGTH];
    for (int i = 0; i < HAYSTACK_LENGTH - 1; i++) {
      char c = (char) ('a' + rng.nextInt(25));
      chars[i] = c;
    }
    chars[HAYSTACK_LENGTH - 1] = TARGET_CHAR;
    String haystack = new String(chars);

    warmup(haystack);

    double totalCharNsPerOp = 0;
    double totalStrNsPerOp = 0;

    for (int round = 1; round <= MEASURE_ROUNDS; round++) {
      // Benchmark indexOf(char)
      long startChar = System.nanoTime();
      long checksumChar = benchIndexOfChar(haystack, MEASURE_ITERATIONS);
      long elapsedChar = System.nanoTime() - startChar;
      double charNsPerOp = (double) elapsedChar / MEASURE_ITERATIONS;

      long startStr = System.nanoTime();
      long checksumStr = benchIndexOfString(haystack, MEASURE_ITERATIONS);
      long elapsedStr = System.nanoTime() - startStr;
      double strNsPerOp = (double) elapsedStr / MEASURE_ITERATIONS;

      totalCharNsPerOp += charNsPerOp;
      totalStrNsPerOp += strNsPerOp;
      System.err.printf(
          "Round %d, checksumChar %d, checksumStr %d\n", round, checksumStr, checksumChar);
    }

    double avgCharNs = totalCharNsPerOp / MEASURE_ROUNDS;
    double avgStrNs = totalStrNsPerOp / MEASURE_ROUNDS;
    double avgRatio = avgStrNs / avgCharNs;
    System.err.println("average char ns: " + avgCharNs);
    System.err.println("average String ns: " + avgStrNs);
    System.err.println("average ratio: " + avgRatio);
  }

  private static void warmup(String haystack) {
    long sink = 0;
    for (int i = 0; i < WARMUP_ITERATIONS; i++) {
      sink += haystack.indexOf(TARGET_CHAR);
      sink += haystack.indexOf(TARGET_STR);
    }
    if (sink == 0) {
      System.out.println("unreachable sink: " + sink);
    }
  }

  private static long benchIndexOfChar(String haystack, int iters) {
    long sink = 0;
    for (int i = 0; i < iters; i++) {
      sink += haystack.indexOf(TARGET_CHAR);
    }
    return sink;
  }

  private static long benchIndexOfString(String haystack, int iters) {
    long sink = 0;
    for (int i = 0; i < iters; i++) {
      sink += haystack.indexOf(TARGET_STR);
    }
    return sink;
  }
}
