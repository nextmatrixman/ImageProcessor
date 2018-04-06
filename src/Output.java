import java.io.FileWriter;
import java.io.IOException;

public class Output {
  public static void writeToCSV(String content) throws IOException {
    String csvFile = Main.workingDir + Main.outputFile;
    FileWriter writer = new FileWriter(csvFile, true);
    writer.append(content);
    writer.flush();
  }
}
