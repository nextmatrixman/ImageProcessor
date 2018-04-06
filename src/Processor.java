import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
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
    while (Main.getCounter() < Main.urls.size()) {
      if (!Main.isEmpty()) {
        try {
          String imageUrl = Main.dequeue();
          String imagePath = Main.workingDir + Utils.getFilename(imageUrl);
          String[] colors = processImage(imagePath, Main.PREVALENT_COLORS);
          StringBuilder sb = new StringBuilder(imageUrl);

          for (String c : colors) {
            sb.append("," + c);
          }

          sb.append("\n");

          delete(imagePath);
          System.out.println(sb.toString());
          Output.writeToCSV(sb.toString());
          Main.getOutput().add(sb.toString());
          Main.setCounter(Main.getCounter() + 1);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

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

  private static String byteToHex(byte r, byte g, byte b) {
    int red = (int) r & 0xff;
    int green = (int) g & 0xff;
    int blue = (int) b & 0xff;
    return String.format("#%02x%02x%02x", red, green, blue);
  }

  private static String[] getPrevalentColors(Map<String, Integer> colorCount, int numOfPrevalentColors) {
    // obtain most prevalent colors
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

  private static void delete(String filePath) {
    File file = new File(filePath);
    file.delete();
  }
}
