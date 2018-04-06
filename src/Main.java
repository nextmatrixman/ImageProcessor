import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Main {
  static final int PREVALENT_COLORS = 3;
  static final int PROCESSOR_COUNT = 20;
  static String workingDir;
  static String inputFile;
  static String outputFile;
  private static final Queue<String> tasks = new ArrayDeque<>();
  static List<String> urls = new ArrayList<>();
  private static int counter = 0;
  private static List<String> output = new ArrayList<>();

  public static void main(String[] args) throws IOException {
    if (args == null || args.length != 3) {
      System.err.println("Please enter working directory path, url input filename, and CSV output filename");
      System.exit(1);
    }

    workingDir = args[0];
    inputFile = args[1];
    outputFile = args[2];

    (new Thread(new Crawler())).start();

    try {
      for (int i = 0; i < PROCESSOR_COUNT; i++) {
        Thread cur = new Thread(new Processor());
        cur.sleep(200);
        cur.start();
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public synchronized static void enqueue(String url) {
    tasks.offer(url);
  }

  public synchronized static String dequeue() {
    return tasks.poll();
  }

  public synchronized static boolean isEmpty() {
    return tasks.isEmpty();
  }

  public synchronized static int getCounter() {
    return counter;
  }

  public synchronized static void setCounter(int c) {
    counter = c;
  }

  public synchronized static List<String> getOutput() {
    return output;
  }
}
