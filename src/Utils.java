public class Utils {
  public static String getFilename(String url) {
    if (url == null) {
      return null;
    }

    String[] segments = url.split("/");

    return segments[segments.length - 1];
  }
}
