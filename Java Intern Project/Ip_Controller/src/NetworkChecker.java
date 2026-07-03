import java.net.InetSocketAddress;
import java.net.Socket;

public class NetworkChecker{

    public static boolean pingPort(String ip, int port){
        Socket socket=new Socket();
        //we created a default socket. After ve created inetsocketaddress with our information coming from txt file to our function
        try{
            socket.connect(new InetSocketAddress(ip, port), 2000); //timeout for preventing any bugs
            return true;//if the connection is successful we can return to boolean 1
        } catch (Exception e) {
            return false;
        }
    }

    // Double check (GMP standard)
    public static boolean gmpVerifyStatus(ServerInfo server){
        // First check
        boolean firstCheck= pingPort(server.ipAddress, server.port);
        if(firstCheck){return true;} //Server is working

        System.out.println("[WARNING] First check failed for " + server.serverName + ". Retrying in 5 seconds (GMP Protocol)...");
        //first check failed and we are checking again

        try {
            Thread.sleep(5000); //wait for 5 second
        } catch (InterruptedException e) {//new exception type
            System.out.println("GMP Timer interrupted.");
        }
        return pingPort(server.ipAddress, server.port);//last check
    }
}
