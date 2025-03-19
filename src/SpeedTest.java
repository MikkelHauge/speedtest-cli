import java.io.*;
import java.net.*;
import java.time.Instant;
import java.net.URI;
import java.util.Objects;

public class SpeedTest {

    public static final String RED = "\033[31m";
    public static final String GREEN = "\033[32m";
    public static final String YELLOW = "\033[33m";
    public static final String BLUE = "\033[34m";
    public static final String RESET = "\033[0m"; // Reset to normal color

    public static void main(String[] args) {
        System.out.println(BLUE + "Running Internet speed test..." + RESET);
        String inputArgument;
        boolean validChoice = false;
        String fileUrl = "";
        if (args.length > 1) {
            System.out.println("Received more than 1 argument. Will only look at the first: " + args[0]);
        }

        if (args.length > 0){
            inputArgument = args[0].toLowerCase();
            if (isValidURL(inputArgument)){
                validChoice = true;
                fileUrl = inputArgument;
            }
            else {
                switch (inputArgument){
                    case "help", "h", "-h":
                        printHelp();
                        return;
                    case "10":
                        fileUrl = "http://ipv4.download.thinkbroadband.com/10MB.zip";
                        validChoice = true;
                        break;
                    case "100":
                        fileUrl = "http://ipv4.download.thinkbroadband.com/100MB.zip";
                        validChoice = true;
                        break;
                    case "500":
                        fileUrl = "http://ipv4.download.thinkbroadband.com/512MB.zip";
                        validChoice = true;
                        break;
                    case "1000":
                        fileUrl = "http://ipv4.download.thinkbroadband.com/1GB.zip";
                        validChoice = true;
                        break;
                    default:
                        System.out.println("No valid argument found - setting default.");
                        fileUrl = "http://ipv4.download.thinkbroadband.com/100MB.zip";
                        validChoice = true;
                        break;
                }
            }
        }
        else {
            // no arguments
            System.out.println("SpeedTest is downloading a 100MB test file.");
            fileUrl = "http://ipv4.download.thinkbroadband.com/100MB.zip";
            validChoice = true;
        }

        if (validChoice) {
            long ping = measurePing();
            double downloadSpeed = measureDownloadSpeed(fileUrl);

            System.out.println(BLUE + "\nSpeed test results:" + RESET);
            System.out.println("Ping: " + YELLOW + ping + " ms." + RESET);
            System.out.println("Download speed: " + YELLOW + String.format("%.2f", downloadSpeed) + RESET + " Mbps");
        } else {
            System.out.println("Error: Invalid argument or URL.");
        }
    }

    private static void printHelp(){
        System.out.println("Valid arguments are:");
        System.out.println("Help: help, h or -h");
        System.out.println("Download size: 10, 100, 500 or 1000 (Size is in MB - just type the number)");
        System.out.println("URL: Any valid URL (You should use an URL to a file though)");
    }

    private static long measurePing(){
        try {

            InetAddress address = InetAddress.getByName("8.8.8.8"); // googles DNS server
            Instant start = Instant.now();
            boolean reachable = address.isReachable(3000); // 3 sec timeout
            Instant end = Instant.now();

            return reachable ? (end.toEpochMilli() - start.toEpochMilli()) : -1;
        } catch (IOException e) {
            System.out.println(RED + "Error measuring ping: " + e.getMessage() + RESET);
            return -1;
        }
    }

    public static boolean isValidURL(String possibleURL) {
        try {
            URI uri = new URI(possibleURL);
            uri.toURL();
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    private static double measureDownloadSpeed(String fileURL) {
        double downloadSpeed = 0.0;

        int graphLength = 50;
        try {
            URI uri = new URI(fileURL);
            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            long totalBytes = connection.getContentLength();

            if (totalBytes == -1) {
                System.out.println(RED + "Unable to determine file size." + RESET);
                return downloadSpeed;
            }

            long startTime = System.nanoTime();
            InputStream inputStream = connection.getInputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            long downloaded = 0;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                downloaded += bytesRead;

                // Percentage of the file downloaded
                int percent = (int) ((downloaded * 100) / totalBytes);

                // 1 # for every 2%
                int numHashes = percent / 2;

                String graph = "#".repeat(Math.max(0, numHashes)) +
                        // Fill spaces - to make it look better!
                        " ".repeat(Math.max(0, graphLength - numHashes));

                System.out.print("\rProgress: " + YELLOW + percent + "% " + RESET + " [" + GREEN + graph + RESET + "]");
            }

            long endTime = System.nanoTime();
            double timeTaken = (endTime - startTime) / 1e9; // seconds
            downloadSpeed = (downloaded * 8) / (timeTaken * 1_000_000); // Convert to Mbps

            inputStream.close();
            connection.disconnect();
        } catch (IOException e) {
            System.out.println(RED + "Error measuring download speed: " + e.getMessage() + RESET);
        } catch (URISyntaxException e) {
            System.out.println(RED + "Error on download URL: " + e.getMessage() + RESET);
        }

        return downloadSpeed;
    }

}
