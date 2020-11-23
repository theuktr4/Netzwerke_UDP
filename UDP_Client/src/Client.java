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

    public Client(String prot, int n, long k, long duration){
        this.n=n;
        this.k = k;
        this.duration = duration;
        if(prot=="UDP"){
            sendUDP();
        }
        else if(prot=="TCP"){
            sendTCP();
        }
    }
    private void sendUDP(){
        long count =0;
        long start =System.currentTimeMillis();
        while(System.currentTimeMillis()<start+this.duration*1000){
            try(DatagramSocket socket = new DatagramSocket()){
                if(count !=0 && count%this.n ==0){
                    Thread.currentThread().sleep(this.k);
                }
                DatagramPacket p = new DatagramPacket(new byte[SIZE],SIZE, InetAddress.getByName(this.ip),this.port);
                socket.send(p);
                //System.out.println("Package sent");
                count++;

            } catch (SocketException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        printDataRate(count*SIZE);
    }

    private void sendTCP(){
        long count =0;
        long start =System.currentTimeMillis();;
        try(Socket s = new Socket(this.ip,this.port)){
            OutputStream out = s.getOutputStream();
            while(System.currentTimeMillis()<start+this.duration*1000){
                if(count !=0 && count%this.n ==0){
                    Thread.currentThread().sleep(this.k);
                }
                out.write(new byte[SIZE]);
                count++;
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        printDataRate(count*SIZE);
    }

    private void printDataRate(long dataInBytes){
        double rate = (dataInBytes*8.0)/(this.duration)/1000.0;
        System.out.printf("Senderate: %3f kbit/s%n",rate);
    }

    public static void main(String[] args) {
        for(int i=0;i<100;i=i+5){
            new Client("TCP",20,100-i,20);
            try {
                Thread.currentThread().sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
