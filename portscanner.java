import java.net.*;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

 class PortScanner {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String host = "";
        int startPort = 0;
        int endPort = 0;

        try {
            
            System.out.print("Enter the target host (domain or IP address): ");
            host = scanner.nextLine();

           
            InetAddress inetAddress = InetAddress.getByName(host);
            System.out.println("Resolved host: " + inetAddress.getHostAddress());

            
            System.out.print("Enter the starting port : ");
            startPort = scanner.nextInt();
            System.out.print("Enter the ending port : ");
            endPort = scanner.nextInt();

            if (startPort < 1 || endPort > 65535 || startPort > endPort) {
                System.err.println("Invalid port range. Ports must be between 1 and 65535.");
                return;
            }

        } catch (InputMismatchException e) {
            System.err.println("Invalid input. Please enter numeric values for ports.");
            return;
        } catch (UnknownHostException e) {
            System.err.println("Error: Unable to resolve host '" + host + "'.");
            return;
        } finally {
            scanner.close();
        }

        System.out.println("Scanning host " + host + " from port " + startPort + " to " + endPort + "...");

        ExecutorService executor = Executors.newFixedThreadPool(20);

        for (int port = startPort; port <= endPort; port++) {
            Runnable worker = new PortScannerWorker(host, port);
            executor.execute(worker);
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
            
        }

        System.out.println("Scan completed.");
    }

    private static class PortScannerWorker implements Runnable {
        private String host;
        private int port;

        public PortScannerWorker(String host, int port) {
            this.host = host;
            this.port = port;
        }

        @Override
        public void run() {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(host, port), 200);
                System.out.println("Port " + port + " is open");
            } catch (SecurityException e) {
                System.err.println("Security error scanning port " + port + ": " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid timeout/port for port " + port + ": " + e.getMessage());
            } catch (Exception e) {
                
            }
        }
    }
}
