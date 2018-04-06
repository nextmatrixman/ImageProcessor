/*
This processor contains the logic for processing the images to obtain the most prevalent colors from
each image and write result to the output file. It monitors whether all URLs read have been processed yet,
if not, it will continuously take jobs from the job queue and process the images. The process is:

1. URL of the image is polled from the job queue, then turned into local file path
2. Read the image and extract all pixels as byte array
3. Read the bytes 3 at a time, which correspond to RED, GREEN, BLUE values of each pixel, then turn
into hex format
4. As hex value of each pixel is obtained, add it to a map that keeps a count of how many times each
hex value appeared
5. When the counting of hex values is completed, find the most prevalent colors by using a priority
queue
6. Delete the downloaded image as it is no longer needed
7. Write the most prevalent color result to output file
*/

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import javax.imageio.ImageIO;

public class Processor implements Runnable {
  @Override
  public void run() {
    String csvFile = Main.workingDir + Main.outputFile;
    FileWriter writer = null;

    try {
      writer = new FileWriter(csvFile, true);

      while (Main.getCounter() < Main.urls.size()) {
        if (!Main.isEmpty()) {
          String imageUrl = Main.dequeue();
          String imagePath = Main.workingDir + Utils.getFilename(imageUrl);
          String[] colors = processImage(imagePath, Main.PREVALENT_COLORS);
          StringBuilder sb = new StringBuilder(imageUrl);

          for (String c : colors) {
            sb.append(",").append(c);
          }

          sb.append("\n");

          delete(imagePath);
          System.out.println(sb.toString());
          writer.append(sb.toString());
          writer.flush();
          Main.setCounter(Main.getCounter() + 1);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (writer != null) {
        try {
          writer.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  // obtain hex value of each pixel of the image and count number of times they appeared
  private static String[] processImage(String imagePath, int numOfPrevalentColors) throws IOException {
    BufferedImage img = ImageIO.read(new File(imagePath));
    Map<String, Integer> colorCount = new HashMap<>();

    if (img == null) {
      return new String[]{};
    }

    byte[] pixels = ((DataBufferByte)img.getRaster().getDataBuffer()).getData();
    int pixelCount = img.getWidth() * img.getHeight();

    for (int i = 0; i < pixelCount; i++) {
      int start = i * 3;
      String hexValue = byteToHex(pixels[start], pixels[start + 1], pixels[start + 2]);

      if (colorCount.containsKey(hexValue)) {
        colorCount.put(hexValue, colorCount.get(hexValue) + 1);
      } else {
        colorCount.put(hexValue, 1);
      }
    }

    return getPrevalentColors(colorCount, numOfPrevalentColors);
  }

  // takes RGB values and combine/convert into hex value
  private static String byteToHex(byte r, byte g, byte b) {
    int red = (int) r & 0xff;
    int green = (int) g & 0xff;
    int blue = (int) b & 0xff;
    return String.format("#%02x%02x%02x", red, green, blue);
  }

  // obtain most prevalent colors
  private static String[] getPrevalentColors(Map<String, Integer> colorCount, int numOfPrevalentColors) {
    int size = Math.min(numOfPrevalentColors, colorCount.size());
    String[] result = new String[size];

    Queue<Entry<String, Integer>> pq = new PriorityQueue<>(size, new Comparator<Entry<String, Integer>>() {
      @Override
      public int compare(Entry<String, Integer> e1, Entry<String, Integer> e2) {
        return Integer.compare(e1.getValue(), e2.getValue());
      }
    });

    for (Entry<String, Integer> e : colorCount.entrySet()) {
      if (pq.isEmpty() || pq.size() < size) {
        pq.offer(e);
      } else if (pq.peek().getValue() < e.getValue()) {
        pq.poll();
        pq.offer(e);
      }
    }

    for (int i = size - 1; i >= 0; i--) {
      result[i] = pq.poll().getKey();
    }

    return result;
  }

  // deletes the image file
  private static void delete(String filePath) {
    File file = new File(filePath);
    file.delete();
  }
}
