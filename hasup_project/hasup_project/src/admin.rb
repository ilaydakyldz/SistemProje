# encoding: utf-8

require 'socket'
require 'json'

class Message
  attr_accessor :demand, :response

  def initialize(demand = nil, response = nil)
    @demand = demand
    @response = response
  end

  def to_json(*args)
    {
      'demand' => @demand,
      'response' => @response
    }.to_json(*args)
  end
end

class Admin
  attr_reader :server1_socket, :server2_socket, :server3_socket, :plotter_socket

  def initialize
    # Sunucu bağlantı noktaları
    @server1_socket = TCPSocket.new('localhost', 5001)
    @server2_socket = TCPSocket.new('localhost', 5002)
    @server3_socket = TCPSocket.new('localhost', 5003)
    # Plotter bağlantısı
    @plotter_socket = TCPSocket.new('localhost', 7002)

    # Konfigürasyon yükleniyor
    load_configuration
  end

  def load_configuration
    # Config dosyasını okuma
    config_file = File.read('dist_subs.conf')
    config_lines = config_file.split("\n")
    fault_tolerance_level = config_lines.find { |line| line.include?('fault_tolerance_level') }
    @fault_tolerance_level = fault_tolerance_level.split('=').last.strip.to_i

    puts "Fault tolerance level: #{@fault_tolerance_level}"

    # Configuration nesnesi oluşturuluyor
    @configuration = {
      fault_tolerance_level: @fault_tolerance_level,
      method: 'STRT'
    }

    puts "Configuration: #{@configuration}"
  end

  def send_start_command_to_servers
    # Sunuculara STRT komutu gönderiliyor
    message = Message.new("STRT", nil)

    [@server1_socket, @server2_socket, @server3_socket].each do |socket|
      socket.puts message.to_json
      puts "STRT komutu gönderildi: #{socket.peeraddr[2]}"
    end
  end

  def send_capacity_request_to_servers
    # Kapasite sorgusu gönderiliyor
    message = Message.new("CPCTY", nil)  # Sadece istek göndereceğiz, response boş
  
    [@server1_socket, @server2_socket, @server3_socket].each do |socket|
      socket.puts message.to_json
      puts "Kapasite sorgusu gönderildi: #{message.demand}"
    end
  end

  def handle_capacity_responses
    # Sunuculardan gelen kapasite yanıtları işleniyor
    [@server1_socket, @server2_socket, @server3_socket].each do |socket|
      begin
        message = socket.gets.chomp
        response = JSON.parse(message)
        puts "Sunucu #{socket.peeraddr[2]} yaniti: #{response}"

        # Yanıtları plotter'a gönderme
        send_to_plotter(response)
      rescue => e
        puts "Hata: #{e.message}"
      end
    end
  end
  
  def send_to_plotter(response)
    # Plotter'a veri gönderiliyor
    @plotter_socket.puts response.to_json
    puts "Plotter'a veri gönderildi: #{response}"
  end

  def close_connections
    # Bağlantılar kapatılıyor
    [@server1_socket, @server2_socket, @server3_socket, @plotter_socket].each do |socket|
      socket.close
    end
    puts "Bağlantılar kapatıldı."
  end
end

# Admin başlatma
admin = Admin.new
admin.send_start_command_to_servers
admin.send_capacity_request_to_servers

# Döngüde sürekli olarak yanıt bekleme
loop do
  admin.handle_capacity_responses
  sleep(5)  # 5 saniye bekleyip tekrar kapasite sorgusu gönderiyor
end
