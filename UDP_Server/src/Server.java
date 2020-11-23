import java.io.IOException;
import java.io.InputStream;
import java.net.*;

public class Server {
    private static final int SIZE = 1400;
    private final int PORT = 4711;
    private final int timeout;
    private int count = 0;

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
            printDataRate(count * 1400, System.currentTimeMillis() - timeout - start);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveTCP() {
        boolean started = false;
        long dataInByte = 0;
        long start = -1;
        try (ServerSocket server = new ServerSocket(this.PORT)) {
            server.setSoTimeout(timeout);

            while (true) {
                try (Socket client = server.accept(); InputStream in = client.getInputStream();) {
                    if (!started) {
                        start = System.currentTimeMillis();
                        started = true;
                    }

                    dataInByte += in.readAllBytes().length;
                } catch (SocketTimeoutException end) {
                    printDataRate(dataInByte, System.currentTimeMillis() - timeout - start);
                    break;
                } catch (IOException e) {
                    System.out.println("Client disconnected");
                    printDataRate(dataInByte, System.currentTimeMillis() - start);
                    break;
                }
            }

        } catch (SocketTimeoutException end) {
            System.out.println(dataInByte / 1400);
            printDataRate(dataInByte, System.currentTimeMillis() - timeout - start);
        } catch (IOException e) {
            System.out.println("Client disconnected");
        }
    }

    private void printDataRate(long dataInBytes, long time_milis) {
        double rate = (dataInBytes * 8.0) / (time_milis / 1000.0) / 1000;
        System.out.printf("Empfangsrate: %3f kbit/s%n", rate);
    }

    public static void main(String[] args) {
        //new Server(args[0],Integer.valueOf(args[1]));
        for(int i=0;i<20;i++){
            new Server("TCP",10000);
        }

    }
}

