/*
Small utility functions
*/

public class Utils {
  // strips filename out of the url
  public static String getFilename(String url) {
    if (url == null) {
      return null;
    }

    String[] segments = url.split("/");

    return segments[segments.length - 1];
  }
}
