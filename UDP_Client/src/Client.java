import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;

public class Client {
    private static final int SIZE = 1400;
    private int n;
    private long k;
    public long duration;
    private int port = 4711;
    private String ip = "localhost";

    public Client(String prot, int n, long k, long duration) {
        this.n = n;
        this.k = k;
        this.duration = duration;
        if (prot == "UDP") {
            sendUDP();
        } else if (prot == "TCP") {
            sendTCP();
        }
    }

    private void sendUDP() {
        long count = 0;
        long start = System.currentTimeMillis();
        try (DatagramSocket socket = new DatagramSocket()) {
            while (System.currentTimeMillis() < start + this.duration * 1000) {
                if (count != 0 && count % this.n == 0) {
                    Thread.sleep(this.k);
                }
                DatagramPacket p = new DatagramPacket(new byte[SIZE], SIZE, InetAddress.getByName(this.ip), this.port);
                socket.send(p);
                //System.out.println("Package sent");
                count++;
            }

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        printDataRate(count * SIZE);
    }

    private void sendTCP() {
        long count = 0;
        long start = System.currentTimeMillis();
        try (Socket s = new Socket(this.ip, this.port)) {
            OutputStream out = s.getOutputStream();
            while (System.currentTimeMillis() < start + this.duration * 1000) {
                if (count != 0 && count % this.n == 0) {
                    Thread.currentThread().sleep(this.k);
                }
                out.write(new byte[SIZE]);
                out.flush();
                count++;
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        printDataRate(count * SIZE);
    }

    private void printDataRate(long dataInBytes) {
        double rate = (dataInBytes * 8.0) / (this.duration) / 1000.0;
        System.out.printf("%3f kbit/s%n", rate);
    }

    public static void main(String[] args) {
        System.out.println("TCP");
        for (int i = 0; i < 20; i++) {
            new Client("TCP", 50, i, 20);
        }
        System.out.println("UDP");
        for (int i = 0; i < 20; i++) {
            new Client("UDP", 50, i, 20);
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
