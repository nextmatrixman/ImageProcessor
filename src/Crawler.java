/*
This crawler reads the URLs from the input file and downloads the images for processing,
when an image is downloaded, the URL of the image is added to job queue to be grabbed by one
of the processor threads to be worked on.
*/

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Crawler implements Runnable {
  @Override
  public void run() {
    String inputFilePath = Main.workingDir + Main.inputFile;

    try {
      readFile(inputFilePath);
      download(Main.urls);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // reads unique URLs from file and ignores duplicated URLs
  private static void readFile(String filePath) throws IOException {
    File file = new File(filePath);
    Scanner sc = new Scanner(file);
    Set<String> existing = new HashSet<>();

    while (sc.hasNextLine()) {
      String nextUrl = sc.nextLine();

      if (existing.contains(nextUrl)) {
        continue;
      }

      Main.urls.add(nextUrl);
      existing.add(nextUrl);
    }

    sc.close();
  }

  // downloads image files and adds their URls to job queue to be processed
  private static void download(List<String> urls) throws IOException {
    ReadableByteChannel rbc = null;
    FileOutputStream fos = null;

    for (String url : urls) {
      String imageFilePath = Main.workingDir + Utils.getFilename(url);
      URL imageUrl = new URL(url);
      rbc = Channels.newChannel(imageUrl.openStream());
      fos = new FileOutputStream(imageFilePath);
      fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
      Main.enqueue(url);
    }

    if (rbc != null) {
      rbc.close();
    }

    if (fos != null) {
      fos.close();
    }
  }
}
