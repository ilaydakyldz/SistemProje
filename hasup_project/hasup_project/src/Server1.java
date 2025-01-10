import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;

public class Server1 {
    private static final int SERVER_PORT = 5001;
    private static final String SERVER2_HOST = "localhost";
    private static final int SERVER2_PORT = 5002;
    private static final String SERVER3_HOST = "localhost";
    private static final int SERVER3_PORT = 5003;

    public static void main(String[] args) {
        // Sunucu başlatılıyor
        new Thread(Server1::startServer).start();

        // Diğer sunuculara bağlanılıyor
        new Thread(() -> connectToServer(SERVER2_HOST, SERVER2_PORT)).start();
        new Thread(() -> connectToServer(SERVER3_HOST, SERVER3_PORT)).start();
    }

    // Dinleyici sunucu
    private static void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Server1 dinlemede: " + SERVER_PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Server1 baglanti alindi: " + socket.getInetAddress().getHostAddress());

                // Yeni bir iş parçacığı başlatıyoruz, gelen mesajları dinleyecek
                new Thread(() -> handleClientMessage(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Client mesajını dinle
    private static void handleClientMessage(Socket socket) {
        try {
            // Mesajı alıyoruz
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println("Server1'den gelen mesaj: " + message);

                // STRT komutunu kontrol et
                if (message.equals("STRT")) {
                    // Burada "STRT" komutuna yanıt olarak YEP veya NOP göndereceğiz
                    sendMessage(socket, "STRT");

                    // Bağlantı başarılıysa, YEP gönderiyoruz (başarı durumu)
                    sendMessage(socket, "YEP");
                }
                // Diğer mesajlar
                else if (message.equals("ping")) {
                    sendMessage(socket, "Server1: Mesaj alindi!");
                } else if (message.equals("capacity_request")) {
                    // Kapasite sorgusu yapılabilir
                    sendCapacityInfo(socket);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Kapasite bilgisi gönderme
    private static void sendCapacityInfo(Socket socket) {
        // Sunucunun kapasite bilgisi
        int server1Status = 1000;  // Örnek kapasite durumu

        // Zaman damgası (timestamp)
        long timestamp = Instant.now().getEpochSecond();

        // JSON formatında kapasite mesajı
        String responseMessage = String.format("{\"demand\": \"CPCTY\", \"server1_status\": %d, \"timestamp\": %d}", server1Status, timestamp);

        // Mesajı gönder
        sendMessage(socket, responseMessage);
    }

    // Mesaj göndermek için yardımcı metot
    private static void sendMessage(Socket socket, String message) {
        try {
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Diğer sunuculara bağlanma
    private static void connectToServer(String host, int port) {
        while (true) {
            try {
                Socket socket = new Socket(host, port);
                System.out.println("Server1 -> " + host + ":" + port + " baglantisi basarili");
                break;  // Baglanti saglandiyse donguden cik
            } catch (IOException e) {
                System.out.println("Server1 -> " + host + ":" + port + " baglanti bekleniyor...");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }
    }
}
