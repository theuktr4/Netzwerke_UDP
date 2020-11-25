import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.stream.Stream;

public class Server {
    private static final int SIZE = 1400;
    private final int PORT = 4711;
    private final int timeout;
    private long count = 0;

    public Server(String protocol, int timeout) {
        this.timeout = timeout;
        if (protocol == "UDP") {
            receiveUDP();
        } else if (protocol == "TCP") {
            receiveTCP();
        }

    }

    private void receiveUDP() {
        boolean started = false;
        long start = -1;
        count = 0;
        try (DatagramSocket datagramSocket = new DatagramSocket(this.PORT)) {
            datagramSocket.setSoTimeout(timeout);
            while (true) {
                DatagramPacket p = new DatagramPacket(new byte[SIZE], SIZE);
                datagramSocket.receive(p);
                if (!started) {
                    start = System.currentTimeMillis();
                    started = true;
                }
                count++;
            }

        } catch (SocketTimeoutException end) {
            printDataRate(count * SIZE, System.currentTimeMillis() - timeout - start);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveTCP() {
        boolean started = false;
        long dataInByte = 0;
        long start = -1;
        try (ServerSocket server = new ServerSocket(this.PORT)) {
            try (Socket client = server.accept(); InputStream in = client.getInputStream();) {
                byte[] buffer = new byte[SIZE];
                while (in.read(buffer) != -1) {
                    dataInByte += SIZE;
                    if (!started) {
                        start = System.currentTimeMillis();
                        started = true;
                    }
                }
                printDataRate(dataInByte, System.currentTimeMillis() - start);
            } catch (IOException e) {
                System.out.println("Client disconnected");
                printDataRate(dataInByte, System.currentTimeMillis() - start);
            }


        } catch (SocketTimeoutException end) {

        } catch (IOException e) {
            System.out.println("Client disconnected");
        }
    }

    private void printDataRate(long dataInBytes, long time_milis) {
        double rate = (dataInBytes * 8.0) / (time_milis/ 1000.0) / 1000.0;
        System.out.printf("%3f kbit/s%n", rate);
    }

    public static void main(String[] args) {
        System.out.println("TCP");
        for (int i = 0; i < 20; i++) {
            new Server("TCP", 10000);
        }
        System.out.println("UDP");
        for (int i = 0; i < 20; i++) {
            new Server("UDP", 10000);
        }

    }
}

