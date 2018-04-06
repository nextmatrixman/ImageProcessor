/*
This program takes 3 parameters:
1. Working directory - where the URL file is located, as well as where the downloaded images and the output file will be
2. Input filename - name of the URL file
3. Output filename - name of the CSV file where the result will be stored
*/

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Main {
  static final int PREVALENT_COLORS = 3; // number of most prevalent colors to fetch
  static String workingDir; // working directory
  static String inputFile; // input filename
  static String outputFile; // output filename
  static final List<String> urls = new ArrayList<>(); // list of URLs read from input file
  private static final Queue<String> tasks = new ArrayDeque<>(); // job queue of image URLs to be processed
  private static int counter = 0; // counter for keeping track of number of images processed

  public static void main(String[] args) {
    if (args == null || args.length != 3) {
      System.err.println("Please enter working directory path, url input filename, and CSV output filename");
      System.exit(1);
    }

    // stores user input of working dir, input filename and output filename
    workingDir = args[0];
    inputFile = args[1];
    outputFile = args[2];

    // starts the crawler thread for reading URLs from input file and downloading images
    (new Thread(new Crawler())).start();

    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    // determines the optimal number of image processor threads for processing the images and getting most prevalent colors
    int processors = Runtime.getRuntime().availableProcessors();

    // create and start image processor threads
    for (int i = 0; i < processors; i++) {
      Thread cur = new Thread(new Processor());
      cur.start();
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
}
