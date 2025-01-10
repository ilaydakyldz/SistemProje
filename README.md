admin.rb:

Sunucuları başlatmak ve yönetmek için komutlar gönderir.
Sunuculardan gelen yanıtları işler ve yönetir.
Her 5 saniyede bir sunuculardan kapasite verilerini talep eder ve plotter.py'ye iletir.

plotter.py:

Sunuculardan alınan kapasite verilerini kullanarak dinamik bir doluluk grafiği oluşturur.
Kapasite bilgilerini 5 saniyede bir güncelleyerek görselleştirir.

Server1.java, Server2.java, Server3.java:

TCP soketleri aracılığıyla birbirleriyle iletişim kurar ve abonelik taleplerini işlerler.
admin.rb tarafından gönderilen kapasite sorgularına yanıt verirler.
Abone olma (SUBS), abonelikten çıkma (DEL) ve güncelleme işlemlerini desteklerler.
dist_subs.conf dosyasından okunan hata tolerans seviyelerine göre çalışırlar.

projemizin kodlarının çalıştırıldığı video linki: https://www.youtube.com/watch?v=wvGNQqAX04U

22060390-Muhammed Murat Kaya
22060329-Ceren Dilay Velioğlu
22060366-İlayda Akyıldız
