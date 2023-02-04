import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class VPNServer {
    private static final String ALGORITHM = "AES";
    private static final String KEY = "ThisIsASecretKey";

    public static void main(String[] args) {
        try {
            Key key = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            try (ServerSocket server = new ServerSocket(8080)) {
				System.out.println("VPN server started on port 8080");
				while (true) {
				    Socket client = server.accept();
				    System.out.println("Received connection from " + client.getInetAddress().getHostAddress());
				    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				    PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));
				    String line;
				    while ((line = in.readLine()) != null) {
				        System.out.println("Received encrypted data: " + line);
				        cipher.init(Cipher.DECRYPT_MODE, key);
				        byte[] decryptedData = cipher.doFinal(line.getBytes());
				        System.out.println("Decrypted data: " + new String(decryptedData));
				        cipher.init(Cipher.ENCRYPT_MODE, key);
				        byte[] encryptedData = cipher.doFinal(decryptedData);
				        out.println(new String(encryptedData));
				        out.flush();
				    }
				    client.close();
				    System.out.println("Connection closed");
				}
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
