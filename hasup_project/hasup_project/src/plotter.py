import socket
import json
import matplotlib.pyplot as plt

def plot_capacity_data(data):
    # Sunucu kapasitelerini al
    server1_status = data.get("server1_status", 0)
    server2_status = data.get("server2_status", 0)
    server3_status = data.get("server3_status", 0)
    timestamp = data.get("timestamp", 0)

    # Sunucular ve kapasiteleri
    servers = ['Server 1', 'Server 2', 'Server 3']
    capacities = [server1_status, server2_status, server3_status]

    # Grafik oluşturuluyor
    plt.figure(figsize=(8, 6))  # Grafik boyutunu ayarla
    bars = plt.bar(servers, capacities, color=['red', 'green', 'blue'])

    # Yüksekliği artırmak için metin ekleniyor
    for i, bar in enumerate(bars):
        plt.text(bar.get_x() + bar.get_width() / 2, bar.get_height() + 10,  # X, Y pozisyonu
                 f'{bars[i].get_height()}', ha='center', va='bottom', fontweight='bold')

    # Grafik etiketleri
    plt.xlabel('Sunucular')
    plt.ylabel('Kapasite')
    plt.title(f'Kapasite Durumu - Timestamp: {timestamp}')
    
    # Grafik gösterimi
    plt.tight_layout()  # Grafik elemanlarının düzenini oturt
    plt.draw()  # Çizimi güncelle
    plt.pause(0.001)  # Grafiklerin görünür olmasını sağlamak için kısa bir süre bekle
    plt.show(block=False)  # Grafik penceresinin hemen kapanmaması için

def start_plotter_server():
    # Plotter için soket sunucusu başlat
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_socket.bind(('localhost', 7002))
    server_socket.listen(1)
    print("Plotter dinliyor localhost:7002...")

    while True:
        client_socket, addr = server_socket.accept()
        print(f"Bağlantı alındı: {addr}")

        data = client_socket.recv(4000)
        if data:
            message = json.loads(data.decode())  # Gelen veriyi çözümle
            plot_capacity_data(message)  # Grafik oluşturma fonksiyonunu çağır

        client_socket.close()

if __name__ == "__main__":
    start_plotter_server()
