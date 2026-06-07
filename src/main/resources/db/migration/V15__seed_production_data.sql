CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO users (id, username, display_name, email, password_hash, role, created_at) VALUES
  ('aaaaaaaa-0000-0000-0000-000000000001', 'demo_yomu',       'Demo User',           'demo@yomu.id',    crypt('Yomu2024!', gen_salt('bf', 10)), 'USER',  NOW()),
  ('aaaaaaaa-0000-0000-0000-000000000002', 'rizky_pratama',   'Rizky Pratama',       'rizky@yomu.id',   crypt('Yomu2024!', gen_salt('bf', 10)), 'USER',  NOW()),
  ('aaaaaaaa-0000-0000-0000-000000000003', 'farhan_hakim',    'Farhan Hakim',        'farhan@yomu.id',  crypt('Yomu2024!', gen_salt('bf', 10)), 'USER',  NOW()),
  ('aaaaaaaa-0000-0000-0000-000000000004', 'annisa_fatimah',  'Annisa Fatimah',      'annisa@yomu.id',  crypt('Yomu2024!', gen_salt('bf', 10)), 'USER',  NOW()),
  ('aaaaaaaa-0000-0000-0000-000000000005', 'maya_sari',       'Maya Sari',           'maya@yomu.id',    crypt('Yomu2024!', gen_salt('bf', 10)), 'USER',  NOW()),
  ('aaaaaaaa-0000-0000-0000-000000000006', 'gilang_ramadhan', 'Gilang Ramadhan',     'gilang@yomu.id',  crypt('Yomu2024!', gen_salt('bf', 10)), 'USER',  NOW()),
  ('aaaaaaaa-0000-0000-0000-000000000007', 'bayu_kurniawan',  'Bayu Kurniawan',      'bayu@yomu.id',    crypt('Yomu2024!', gen_salt('bf', 10)), 'USER',  NOW()),
  ('aaaaaaaa-0000-0000-0000-000000000008', 'dinda_putri',     'Dinda Putri',         'dinda@yomu.id',   crypt('Yomu2024!', gen_salt('bf', 10)), 'USER',  NOW()),
  ('aaaaaaaa-0000-0000-0000-000000000009', 'sinta_rahayu',    'Sinta Rahayu',        'sinta@yomu.id',   crypt('Yomu2024!', gen_salt('bf', 10)), 'USER',  NOW()),
  ('aaaaaaaa-0000-0000-0000-000000000010', 'bagas_wicaksono', 'Bagas Wicaksono',     'bagas@yomu.id',   crypt('Yomu2024!', gen_salt('bf', 10)), 'USER',  NOW()),
  ('aaaaaaaa-0000-0000-0000-000000000011', 'reza_fauzi',      'Reza Fauzi',          'reza@yomu.id',    crypt('Yomu2024!', gen_salt('bf', 10)), 'USER',  NOW()),
  ('aaaaaaaa-0000-0000-0000-000000000012', 'hendra_kusuma',   'Hendra Kusuma',       'hendra@yomu.id',  crypt('Yomu2024!', gen_salt('bf', 10)), 'USER',  NOW()),
  ('aaaaaaaa-0000-0000-0000-000000000013', 'laras_indah',     'Laras Indah',         'laras@yomu.id',   crypt('Yomu2024!', gen_salt('bf', 10)), 'USER',  NOW()),
  ('aaaaaaaa-0000-0000-0000-000000000014', 'eko_santoso',     'Eko Santoso',         'eko@yomu.id',     crypt('Yomu2024!', gen_salt('bf', 10)), 'USER',  NOW()),
  ('aaaaaaaa-0000-0000-0000-000000000015', 'wulan_sari',      'Wulan Sari',          'wulan@yomu.id',   crypt('Yomu2024!', gen_salt('bf', 10)), 'USER',  NOW()),
  ('aaaaaaaa-0000-0000-0000-000000000099', 'admin_yomu',      'Administrator Yomu',  'admin@yomu.id',   crypt('Yomu2024!', gen_salt('bf', 10)), 'ADMIN', NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO reading (reading_id, title, content, category, author_id, hidden) VALUES
  (
    'eeeeeeee-0000-0000-0000-000000000001',
    'Arsitektur Perangkat Lunak: Membangun Sistem yang Tangguh',
    'Arsitektur perangkat lunak merupakan fondasi dari setiap sistem perangkat lunak yang baik. Salah satu pendekatan yang semakin banyak diadopsi adalah Hexagonal Architecture, atau yang dikenal juga sebagai Ports and Adapters. Pendekatan ini memisahkan logika bisnis inti dari detail teknis seperti database, framework, dan antarmuka pengguna. Dengan demikian, domain layer dapat diuji secara terpisah tanpa ketergantungan pada infrastruktur eksternal.

Selain arsitektur, design pattern memainkan peran penting dalam pengembangan perangkat lunak. Strategy Pattern memungkinkan algoritma diganti secara dinamis tanpa mengubah kode yang menggunakannya. Decorator Pattern memungkinkan penambahan tanggung jawab pada objek secara dinamis dan dapat ditumpuk. Observer Pattern memfasilitasi komunikasi antar komponen melalui event, sehingga mengurangi ketergantungan langsung antar modul.

Prinsip SOLID menjadi panduan dalam mendesain kelas dan modul yang baik. Single Responsibility Principle memastikan setiap kelas hanya memiliki satu alasan untuk berubah. Open/Closed Principle menyatakan bahwa entitas perangkat lunak harus terbuka untuk ekstensi namun tertutup untuk modifikasi langsung. Dependency Inversion Principle memastikan modul tingkat tinggi tidak bergantung pada modul tingkat rendah, melainkan keduanya bergantung pada abstraksi yang sama.',
    'Teknologi',
    'aaaaaaaa-0000-0000-0000-000000000099',
    false
  ),
  (
    'eeeeeeee-0000-0000-0000-000000000002',
    'Kecerdasan Buatan dan Transformasi Dunia Pendidikan',
    'Kecerdasan buatan telah mengubah lanskap pendidikan secara fundamental. Machine Learning, sebagai cabang dari AI, memungkinkan sistem untuk belajar dari data tanpa pemrograman eksplisit untuk setiap tugas. Dalam konteks pendidikan, ini membuka peluang untuk personalisasi pengalaman belajar yang belum pernah ada sebelumnya bagi setiap pelajar.

Personalized Learning menggunakan algoritma AI untuk menganalisis pola belajar setiap siswa, mengidentifikasi kekuatan dan kelemahan mereka, lalu menyesuaikan materi dan tempo pembelajaran secara real-time. Platform edukasi modern dapat menentukan tingkat kesulitan soal yang tepat untuk setiap individu. Algoritma rekomendasi juga membantu mengarahkan siswa ke konten yang paling relevan dengan kebutuhan spesifik mereka.

Namun integrasi AI dalam pendidikan juga membawa tantangan yang perlu diantisipasi. Ketergantungan berlebihan pada teknologi dapat mengurangi kemampuan berpikir kritis dan kreatif siswa. Privasi data menjadi perhatian utama ketika sistem AI mengumpulkan dan menganalisis perilaku belajar. Kesenjangan akses teknologi juga dapat memperparah ketidaksetaraan pendidikan jika tidak ditangani dengan kebijakan yang tepat dan inklusif.',
    'Teknologi',
    'aaaaaaaa-0000-0000-0000-000000000099',
    false
  ),
  (
    'eeeeeeee-0000-0000-0000-000000000003',
    'Keamanan Siber: Melindungi Aset Digital di Era Modern',
    'Keamanan siber adalah praktik melindungi sistem, jaringan, dan program dari serangan digital yang semakin canggih. SQL Injection adalah salah satu serangan paling umum, di mana penyerang menyisipkan kode SQL berbahaya melalui input pengguna untuk memanipulasi atau mencuri data dari database. Cross-Site Scripting memungkinkan penyerang menyuntikkan skrip berbahaya ke halaman web yang kemudian dieksekusi di browser pengguna lain.

Enkripsi merupakan komponen fundamental dalam keamanan digital modern. HTTPS menggunakan protokol TLS untuk mengenkripsi komunikasi antara klien dan server, memastikan data tidak dapat dibaca oleh pihak yang tidak berwenang di tengah perjalanan. Manajemen kata sandi yang baik mencakup penggunaan kata sandi unik dan kompleks untuk setiap akun, idealnya disimpan menggunakan password manager yang terpercaya dan terenkripsi.

Two-Factor Authentication menambahkan lapisan keamanan ekstra dengan memerlukan dua bentuk verifikasi identitas yang berbeda. Biasanya ini meliputi sesuatu yang diketahui seperti kata sandi, dan sesuatu yang dimiliki seperti kode OTP dari aplikasi autentikasi. Prinsip Least Privilege menyatakan bahwa setiap pengguna atau sistem hanya boleh memiliki hak akses minimum yang benar-benar diperlukan untuk menjalankan fungsinya.',
    'Teknologi',
    'aaaaaaaa-0000-0000-0000-000000000099',
    false
  ),
  (
    'eeeeeeee-0000-0000-0000-000000000004',
    'Metodologi Agile: Pengembangan Perangkat Lunak Adaptif',
    'Metodologi Agile lahir sebagai respons terhadap keterbatasan pendekatan pengembangan perangkat lunak tradisional yang kaku dan sulit beradaptasi. Agile Manifesto menekankan empat nilai utama: individu dan interaksi lebih dari proses dan alat, perangkat lunak yang berfungsi lebih dari dokumentasi komprehensif, kolaborasi pelanggan lebih dari negosiasi kontrak, serta merespons perubahan lebih dari mengikuti rencana yang sudah ditetapkan.

Scrum adalah kerangka kerja Agile yang paling banyak digunakan dalam industri. Dalam Scrum, pekerjaan dibagi menjadi Sprint, yaitu periode waktu tetap selama satu hingga empat minggu untuk menyelesaikan serangkaian tugas yang telah diprioritaskan bersama. Daily Standup Meeting memastikan seluruh tim mengetahui kemajuan, hambatan, dan rencana hari itu. Sprint Review dan Retrospective memungkinkan tim untuk terus belajar dan meningkatkan proses secara berkelanjutan.

Test-Driven Development adalah praktik di mana developer menulis tes terlebih dahulu sebelum mengimplementasikan kode. Proses ini mengikuti siklus merah-hijau-refaktor: tulis tes yang gagal, buat kode yang membuatnya berhasil, lalu perbaiki kode tanpa mengubah perilakunya. Continuous Integration melengkapi TDD dengan mengotomatisasi proses build dan pengujian setiap kali kode baru diunggah ke repositori, memungkinkan deteksi dini masalah integrasi sebelum menjalar lebih jauh.',
    'Teknologi',
    'aaaaaaaa-0000-0000-0000-000000000099',
    false
  ),
  (
    'eeeeeeee-0000-0000-0000-000000000005',
    'Microservices dan Sistem Terdistribusi Masa Kini',
    'Arsitektur microservices adalah pendekatan pengembangan perangkat lunak di mana aplikasi dipecah menjadi layanan-layanan kecil yang independen, masing-masing berjalan dalam prosesnya sendiri dan berkomunikasi melalui API yang terdefinisi dengan baik. Ini berbeda dari arsitektur monolitik di mana semua komponen dikemas dalam satu unit deployment yang besar. Microservices memungkinkan tim yang berbeda untuk mengembangkan, mendeploy, dan menskalakan layanan secara mandiri.

Teorema CAP menyatakan bahwa dalam sistem terdistribusi, tidak mungkin secara bersamaan menjamin ketiga properti berikut: Consistency yaitu semua node melihat data yang sama, Availability yaitu setiap permintaan mendapat respons, dan Partition Tolerance yaitu sistem tetap berfungsi meski ada kegagalan jaringan. Desainer sistem harus memilih kompromi yang tepat berdasarkan kebutuhan bisnis yang ada.

API Gateway bertindak sebagai titik masuk tunggal untuk semua permintaan klien ke dalam sistem microservices. Ia menangani kebutuhan lintas layanan seperti autentikasi, pembatasan laju permintaan, dan pencatatan log, serta merutekan setiap permintaan ke layanan yang tepat. Event-driven architecture memungkinkan layanan berkomunikasi secara asinkron melalui event bus, mengurangi coupling antar layanan dan meningkatkan skalabilitas sistem secara menyeluruh.',
    'Teknologi',
    'aaaaaaaa-0000-0000-0000-000000000099',
    false
  )
ON CONFLICT (reading_id) DO NOTHING;

INSERT INTO question (question_id, reading_id, question_text, correct_answer) VALUES
  ('ffffffff-0101-0000-0000-000000000000', 'eeeeeeee-0000-0000-0000-000000000001', 'Apa nama lain dari Hexagonal Architecture?', 'Ports and Adapters'),
  ('ffffffff-0102-0000-0000-000000000000', 'eeeeeeee-0000-0000-0000-000000000001', 'Prinsip SOLID mana yang menyatakan bahwa kelas harus terbuka untuk ekstensi tetapi tertutup untuk modifikasi?', 'Open/Closed Principle'),
  ('ffffffff-0103-0000-0000-000000000000', 'eeeeeeee-0000-0000-0000-000000000001', 'Design pattern apa yang paling tepat digunakan untuk mengganti algoritma secara dinamis tanpa mengubah kode penggunanya?', 'Strategy Pattern'),
  ('ffffffff-0104-0000-0000-000000000000', 'eeeeeeee-0000-0000-0000-000000000001', 'Apa manfaat utama dari penerapan Dependency Inversion Principle?', 'Modul tingkat tinggi tidak bergantung langsung pada modul tingkat rendah'),

  ('ffffffff-0201-0000-0000-000000000000', 'eeeeeeee-0000-0000-0000-000000000002', 'Apa yang dimaksud dengan Machine Learning dalam konteks pendidikan?', 'Kemampuan sistem untuk belajar dari data tanpa diprogram secara eksplisit untuk setiap tugas'),
  ('ffffffff-0202-0000-0000-000000000000', 'eeeeeeee-0000-0000-0000-000000000002', 'Apa yang dimaksud dengan Personalized Learning berbasis AI?', 'Penyesuaian konten dan tempo belajar berdasarkan kebutuhan dan kemampuan individu'),
  ('ffffffff-0203-0000-0000-000000000000', 'eeeeeeee-0000-0000-0000-000000000002', 'Algoritma rekomendasi dalam platform edukasi biasanya bekerja berdasarkan?', 'Analisis pola perilaku pengguna untuk menyarankan konten yang relevan'),
  ('ffffffff-0204-0000-0000-000000000000', 'eeeeeeee-0000-0000-0000-000000000002', 'Apa risiko utama penggunaan AI yang berlebihan dalam proses pembelajaran?', 'Berkurangnya kemampuan berpikir kritis dan kemandirian belajar siswa'),

  ('ffffffff-0301-0000-0000-000000000000', 'eeeeeeee-0000-0000-0000-000000000003', 'Apa yang dimaksud dengan SQL Injection?', 'Serangan dengan menyisipkan kode SQL berbahaya melalui input pengguna untuk memanipulasi database'),
  ('ffffffff-0302-0000-0000-000000000000', 'eeeeeeee-0000-0000-0000-000000000003', 'Mengapa HTTPS lebih aman dibandingkan HTTP?', 'HTTPS mengenkripsi seluruh data yang dikirim antara klien dan server menggunakan TLS'),
  ('ffffffff-0303-0000-0000-000000000000', 'eeeeeeee-0000-0000-0000-000000000003', 'Apa praktik terbaik dalam manajemen kata sandi?', 'Menggunakan kata sandi unik yang panjang dan berbeda untuk setiap akun'),
  ('ffffffff-0304-0000-0000-000000000000', 'eeeeeeee-0000-0000-0000-000000000003', 'Apa yang dimaksud dengan Two-Factor Authentication?', 'Verifikasi identitas menggunakan dua metode berbeda secara bersamaan'),

  ('ffffffff-0401-0000-0000-000000000000', 'eeeeeeee-0000-0000-0000-000000000004', 'Nilai mana yang paling ditekankan dalam Agile Manifesto?', 'Individu dan interaksi lebih dari proses dan alat'),
  ('ffffffff-0402-0000-0000-000000000000', 'eeeeeeee-0000-0000-0000-000000000004', 'Dalam Scrum, apa yang dimaksud dengan Sprint?', 'Periode waktu tetap untuk menyelesaikan sekumpulan pekerjaan yang telah diprioritaskan'),
  ('ffffffff-0403-0000-0000-000000000000', 'eeeeeeee-0000-0000-0000-000000000004', 'Apa tujuan utama dari Test-Driven Development?', 'Menulis tes terlebih dahulu sebelum mengimplementasikan kode produksi'),
  ('ffffffff-0404-0000-0000-000000000000', 'eeeeeeee-0000-0000-0000-000000000004', 'Apa manfaat utama dari Continuous Integration?', 'Deteksi dini masalah integrasi melalui proses build dan pengujian otomatis'),

  ('ffffffff-0501-0000-0000-000000000000', 'eeeeeeee-0000-0000-0000-000000000005', 'Apa perbedaan utama antara arsitektur monolitik dan microservices?', 'Microservices memecah aplikasi menjadi layanan-layanan kecil yang independen dan dapat di-deploy secara terpisah'),
  ('ffffffff-0502-0000-0000-000000000000', 'eeeeeeee-0000-0000-0000-000000000005', 'Apa yang dinyatakan oleh Teorema CAP dalam sistem terdistribusi?', 'Sistem hanya dapat menjamin dua dari tiga: Consistency, Availability, dan Partition Tolerance'),
  ('ffffffff-0503-0000-0000-000000000000', 'eeeeeeee-0000-0000-0000-000000000005', 'Apa fungsi utama dari API Gateway dalam arsitektur microservices?', 'Bertindak sebagai titik masuk tunggal yang menangani routing, autentikasi, dan cross-cutting concerns'),
  ('ffffffff-0504-0000-0000-000000000000', 'eeeeeeee-0000-0000-0000-000000000005', 'Apa keuntungan utama dari event-driven architecture dalam microservices?', 'Komunikasi antar layanan secara asinkron tanpa ketergantungan langsung antar layanan')
ON CONFLICT (question_id) DO NOTHING;

INSERT INTO question_options (question_question_id, options) VALUES
  ('ffffffff-0101-0000-0000-000000000000', 'Ports and Adapters'),
  ('ffffffff-0101-0000-0000-000000000000', 'Model-View-Controller'),
  ('ffffffff-0101-0000-0000-000000000000', 'Event-Driven Architecture'),
  ('ffffffff-0101-0000-0000-000000000000', 'Layered Architecture'),

  ('ffffffff-0102-0000-0000-000000000000', 'Open/Closed Principle'),
  ('ffffffff-0102-0000-0000-000000000000', 'Single Responsibility Principle'),
  ('ffffffff-0102-0000-0000-000000000000', 'Liskov Substitution Principle'),
  ('ffffffff-0102-0000-0000-000000000000', 'Interface Segregation Principle'),

  ('ffffffff-0103-0000-0000-000000000000', 'Strategy Pattern'),
  ('ffffffff-0103-0000-0000-000000000000', 'Singleton Pattern'),
  ('ffffffff-0103-0000-0000-000000000000', 'Builder Pattern'),
  ('ffffffff-0103-0000-0000-000000000000', 'Observer Pattern'),

  ('ffffffff-0104-0000-0000-000000000000', 'Modul tingkat tinggi tidak bergantung langsung pada modul tingkat rendah'),
  ('ffffffff-0104-0000-0000-000000000000', 'Setiap kelas hanya boleh memiliki satu tanggung jawab'),
  ('ffffffff-0104-0000-0000-000000000000', 'Subkelas dapat menggantikan kelas induknya tanpa mengubah perilaku program'),
  ('ffffffff-0104-0000-0000-000000000000', 'Antarmuka harus spesifik sesuai kebutuhan klien'),

  ('ffffffff-0201-0000-0000-000000000000', 'Kemampuan sistem untuk belajar dari data tanpa diprogram secara eksplisit untuk setiap tugas'),
  ('ffffffff-0201-0000-0000-000000000000', 'Penggunaan mesin fisik canggih di dalam ruang kelas'),
  ('ffffffff-0201-0000-0000-000000000000', 'Program belajar yang dirancang secara manual oleh kurikulum'),
  ('ffffffff-0201-0000-0000-000000000000', 'Teknologi hardware untuk mendukung laboratorium komputer sekolah'),

  ('ffffffff-0202-0000-0000-000000000000', 'Penyesuaian konten dan tempo belajar berdasarkan kebutuhan dan kemampuan individu'),
  ('ffffffff-0202-0000-0000-000000000000', 'Proses belajar mandiri tanpa bantuan guru atau instruktur'),
  ('ffffffff-0202-0000-0000-000000000000', 'Metode belajar privat dengan tutor perseorangan berbayar'),
  ('ffffffff-0202-0000-0000-000000000000', 'Kurikulum yang disesuaikan per sekolah oleh dinas pendidikan'),

  ('ffffffff-0203-0000-0000-000000000000', 'Analisis pola perilaku pengguna untuk menyarankan konten yang relevan'),
  ('ffffffff-0203-0000-0000-000000000000', 'Urutan alfabetis dari judul-judul konten yang tersedia'),
  ('ffffffff-0203-0000-0000-000000000000', 'Daftar konten berdasarkan waktu unggah terbaru'),
  ('ffffffff-0203-0000-0000-000000000000', 'Pilihan konten yang ditentukan langsung oleh kurikulum nasional'),

  ('ffffffff-0204-0000-0000-000000000000', 'Berkurangnya kemampuan berpikir kritis dan kemandirian belajar siswa'),
  ('ffffffff-0204-0000-0000-000000000000', 'Meningkatnya biaya pengadaan buku pelajaran cetak'),
  ('ffffffff-0204-0000-0000-000000000000', 'Kecepatan koneksi internet yang menjadi lebih lambat'),
  ('ffffffff-0204-0000-0000-000000000000', 'Bertambahnya jumlah mata pelajaran dalam kurikulum sekolah'),

  ('ffffffff-0301-0000-0000-000000000000', 'Serangan dengan menyisipkan kode SQL berbahaya melalui input pengguna untuk memanipulasi database'),
  ('ffffffff-0301-0000-0000-000000000000', 'Prosedur standar penyimpanan data terstruktur dalam database relasional'),
  ('ffffffff-0301-0000-0000-000000000000', 'Metode optimasi query untuk mempercepat performa database'),
  ('ffffffff-0301-0000-0000-000000000000', 'Teknik backup database secara otomatis dan terjadwal'),

  ('ffffffff-0302-0000-0000-000000000000', 'HTTPS mengenkripsi seluruh data yang dikirim antara klien dan server menggunakan TLS'),
  ('ffffffff-0302-0000-0000-000000000000', 'HTTPS memiliki kecepatan transfer data yang lebih tinggi dari HTTP'),
  ('ffffffff-0302-0000-0000-000000000000', 'HTTPS tidak memerlukan infrastruktur server untuk beroperasi'),
  ('ffffffff-0302-0000-0000-000000000000', 'HTTPS menggunakan nomor port yang sama persis dengan HTTP'),

  ('ffffffff-0303-0000-0000-000000000000', 'Menggunakan kata sandi unik yang panjang dan berbeda untuk setiap akun'),
  ('ffffffff-0303-0000-0000-000000000000', 'Menggunakan kata sandi yang mudah diingat dan singkat agar tidak lupa'),
  ('ffffffff-0303-0000-0000-000000000000', 'Menyimpan semua kata sandi dalam satu file teks biasa di komputer'),
  ('ffffffff-0303-0000-0000-000000000000', 'Menggunakan tanggal lahir sebagai kata sandi karena mudah diingat'),

  ('ffffffff-0304-0000-0000-000000000000', 'Verifikasi identitas menggunakan dua metode berbeda secara bersamaan'),
  ('ffffffff-0304-0000-0000-000000000000', 'Sistem login yang memerlukan dua kata sandi berbeda sekaligus'),
  ('ffffffff-0304-0000-0000-000000000000', 'Proses enkripsi data sebanyak dua kali sebelum dikirimkan'),
  ('ffffffff-0304-0000-0000-000000000000', 'Pembuatan dua akun pengguna untuk meningkatkan keamanan'),

  ('ffffffff-0401-0000-0000-000000000000', 'Individu dan interaksi lebih dari proses dan alat'),
  ('ffffffff-0401-0000-0000-000000000000', 'Dokumentasi lengkap lebih dari kolaborasi aktif dengan pelanggan'),
  ('ffffffff-0401-0000-0000-000000000000', 'Mengikuti rencana awal lebih dari merespons perubahan yang terjadi'),
  ('ffffffff-0401-0000-0000-000000000000', 'Kontrak negosiasi lebih dari kerja sama yang erat dengan pelanggan'),

  ('ffffffff-0402-0000-0000-000000000000', 'Periode waktu tetap untuk menyelesaikan sekumpulan pekerjaan yang telah diprioritaskan'),
  ('ffffffff-0402-0000-0000-000000000000', 'Rapat harian seluruh anggota tim pengembang di pagi hari'),
  ('ffffffff-0402-0000-0000-000000000000', 'Daftar lengkap semua fitur yang akan dikembangkan selama proyek'),
  ('ffffffff-0402-0000-0000-000000000000', 'Proses pengujian akhir produk sebelum diluncurkan ke pengguna'),

  ('ffffffff-0403-0000-0000-000000000000', 'Menulis tes terlebih dahulu sebelum mengimplementasikan kode produksi'),
  ('ffffffff-0403-0000-0000-000000000000', 'Menguji seluruh aplikasi secara manual setelah semua fitur selesai'),
  ('ffffffff-0403-0000-0000-000000000000', 'Mendelegasikan semua pengujian ke tim Quality Assurance khusus'),
  ('ffffffff-0403-0000-0000-000000000000', 'Menghindari pengujian otomatis untuk menghemat waktu pengembangan'),

  ('ffffffff-0404-0000-0000-000000000000', 'Deteksi dini masalah integrasi melalui proses build dan pengujian otomatis'),
  ('ffffffff-0404-0000-0000-000000000000', 'Penggabungan seluruh kode dilakukan satu kali tepat sebelum rilis produksi'),
  ('ffffffff-0404-0000-0000-000000000000', 'Setiap developer bekerja di cabang terpisah tanpa saling berbagi kode'),
  ('ffffffff-0404-0000-0000-000000000000', 'Pengujian integrasi hanya dilakukan oleh tim QA di akhir proyek'),

  ('ffffffff-0501-0000-0000-000000000000', 'Microservices memecah aplikasi menjadi layanan-layanan kecil yang independen dan dapat di-deploy secara terpisah'),
  ('ffffffff-0501-0000-0000-000000000000', 'Arsitektur monolitik lebih mudah di-scale secara horizontal dibanding microservices'),
  ('ffffffff-0501-0000-0000-000000000000', 'Microservices menggunakan satu database terpusat untuk semua layanannya'),
  ('ffffffff-0501-0000-0000-000000000000', 'Arsitektur monolitik lebih cocok digunakan untuk aplikasi berskala sangat besar'),

  ('ffffffff-0502-0000-0000-000000000000', 'Sistem hanya dapat menjamin dua dari tiga: Consistency, Availability, dan Partition Tolerance'),
  ('ffffffff-0502-0000-0000-000000000000', 'Sistem terdistribusi selalu dapat mencapai ketiganya: Consistency, Availability, dan Performance'),
  ('ffffffff-0502-0000-0000-000000000000', 'Teorema tentang kapasitas, akses paralel, dan performa database terdistribusi'),
  ('ffffffff-0502-0000-0000-000000000000', 'Standar protokol komunikasi resmi yang digunakan antar layanan dalam sistem'),

  ('ffffffff-0503-0000-0000-000000000000', 'Bertindak sebagai titik masuk tunggal yang menangani routing, autentikasi, dan cross-cutting concerns'),
  ('ffffffff-0503-0000-0000-000000000000', 'Menyimpan seluruh data dari setiap microservice dalam satu tempat terpusat'),
  ('ffffffff-0503-0000-0000-000000000000', 'Menggantikan kebutuhan database pada masing-masing layanan microservices'),
  ('ffffffff-0503-0000-0000-000000000000', 'Mengenkripsi semua komunikasi yang terjadi antara setiap microservices'),

  ('ffffffff-0504-0000-0000-000000000000', 'Komunikasi antar layanan secara asinkron tanpa ketergantungan langsung antar layanan'),
  ('ffffffff-0504-0000-0000-000000000000', 'Penggabungan semua microservices menjadi satu layanan monolitik besar'),
  ('ffffffff-0504-0000-0000-000000000000', 'Memperlambat pemrosesan permintaan untuk meningkatkan keandalan sistem'),
  ('ffffffff-0504-0000-0000-000000000000', 'Mengurangi jumlah total event yang diproses agar sistem tidak kelebihan beban');

INSERT INTO achievements (id, name, description, achievement_type, milestone) VALUES
  ('11111111-1111-1111-1111-111111111111', 'Pembaca Setia',   'Selesaikan 5 artikel bacaan di Yomu.',       'READING_COMPLETED', 5),
  ('22222222-2222-2222-2222-222222222222', 'Juara Kuis',      'Selesaikan 10 kuis dengan hasil apapun.',    'QUIZ_COMPLETED',    10),
  ('33333333-3333-3333-3333-333333333333', 'Langkah Pertama', 'Selesaikan bacaan pertama Anda di Yomu.',   'READING_COMPLETED', 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO daily_missions (id, name, description, target_type, milestone) VALUES
  ('44444444-4444-4444-4444-444444444444', 'Baca Hari Ini', 'Selesaikan satu artikel bacaan hari ini.',   'READING_COMPLETED', 1),
  ('55555555-5555-5555-5555-555555555555', 'Kuis Harian',   'Kerjakan satu kuis bacaan hari ini.',        'QUIZ_COMPLETED',    1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO clans (id, name, tier, score, leader_id, created_at) VALUES
  ('cccccccc-0000-0000-0000-000000000001', 'Nusantara Elite',  'DIAMOND', 1375, 'aaaaaaaa-0000-0000-0000-000000000002', NOW()),
  ('cccccccc-0000-0000-0000-000000000002', 'Cahaya Timur',     'DIAMOND', 1350, 'aaaaaaaa-0000-0000-0000-000000000005', NOW()),
  ('cccccccc-0000-0000-0000-000000000003', 'Pena Emas',        'GOLD',     775, 'aaaaaaaa-0000-0000-0000-000000000008', NOW()),
  ('cccccccc-0000-0000-0000-000000000004', 'Garuda Pena',      'GOLD',     675, 'aaaaaaaa-0000-0000-0000-000000000010', NOW()),
  ('cccccccc-0000-0000-0000-000000000005', 'Argenta FC',       'SILVER',   300, 'aaaaaaaa-0000-0000-0000-000000000012', NOW()),
  ('cccccccc-0000-0000-0000-000000000006', 'Langit Biru',      'SILVER',   275, 'aaaaaaaa-0000-0000-0000-000000000013', NOW()),
  ('cccccccc-0000-0000-0000-000000000007', 'Bintang Literasi', 'BRONZE',   125, 'aaaaaaaa-0000-0000-0000-000000000014', NOW()),
  ('cccccccc-0000-0000-0000-000000000008', 'Pemula Sejati',    'BRONZE',   175, 'aaaaaaaa-0000-0000-0000-000000000015', NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO clan_members (id, clan_id, user_id, role, joined_at) VALUES
  (gen_random_uuid(), 'cccccccc-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000002', 'LEADER', NOW()),
  (gen_random_uuid(), 'cccccccc-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000003', 'MEMBER', NOW()),
  (gen_random_uuid(), 'cccccccc-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000004', 'MEMBER', NOW()),
  (gen_random_uuid(), 'cccccccc-0000-0000-0000-000000000002', 'aaaaaaaa-0000-0000-0000-000000000005', 'LEADER', NOW()),
  (gen_random_uuid(), 'cccccccc-0000-0000-0000-000000000002', 'aaaaaaaa-0000-0000-0000-000000000006', 'MEMBER', NOW()),
  (gen_random_uuid(), 'cccccccc-0000-0000-0000-000000000002', 'aaaaaaaa-0000-0000-0000-000000000007', 'MEMBER', NOW()),
  (gen_random_uuid(), 'cccccccc-0000-0000-0000-000000000003', 'aaaaaaaa-0000-0000-0000-000000000008', 'LEADER', NOW()),
  (gen_random_uuid(), 'cccccccc-0000-0000-0000-000000000003', 'aaaaaaaa-0000-0000-0000-000000000009', 'MEMBER', NOW()),
  (gen_random_uuid(), 'cccccccc-0000-0000-0000-000000000004', 'aaaaaaaa-0000-0000-0000-000000000010', 'LEADER', NOW()),
  (gen_random_uuid(), 'cccccccc-0000-0000-0000-000000000004', 'aaaaaaaa-0000-0000-0000-000000000011', 'MEMBER', NOW()),
  (gen_random_uuid(), 'cccccccc-0000-0000-0000-000000000005', 'aaaaaaaa-0000-0000-0000-000000000012', 'LEADER', NOW()),
  (gen_random_uuid(), 'cccccccc-0000-0000-0000-000000000006', 'aaaaaaaa-0000-0000-0000-000000000013', 'LEADER', NOW()),
  (gen_random_uuid(), 'cccccccc-0000-0000-0000-000000000007', 'aaaaaaaa-0000-0000-0000-000000000014', 'LEADER', NOW()),
  (gen_random_uuid(), 'cccccccc-0000-0000-0000-000000000008', 'aaaaaaaa-0000-0000-0000-000000000015', 'LEADER', NOW())
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO quiz_attempt (quiz_attempt_id, student_id, reading_id, score, completed_at) VALUES
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000002', 'eeeeeeee-0000-0000-0000-000000000001', 100, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000002', 'eeeeeeee-0000-0000-0000-000000000002', 100, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000002', 'eeeeeeee-0000-0000-0000-000000000003',  75, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000002', 'eeeeeeee-0000-0000-0000-000000000004', 100, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000002', 'eeeeeeee-0000-0000-0000-000000000005', 100, NOW()),

  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000003', 'eeeeeeee-0000-0000-0000-000000000001', 100, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000003', 'eeeeeeee-0000-0000-0000-000000000002',  75, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000003', 'eeeeeeee-0000-0000-0000-000000000003', 100, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000003', 'eeeeeeee-0000-0000-0000-000000000004',  75, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000003', 'eeeeeeee-0000-0000-0000-000000000005', 100, NOW()),

  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000004', 'eeeeeeee-0000-0000-0000-000000000001',  75, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000004', 'eeeeeeee-0000-0000-0000-000000000002', 100, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000004', 'eeeeeeee-0000-0000-0000-000000000003', 100, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000004', 'eeeeeeee-0000-0000-0000-000000000004', 100, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000004', 'eeeeeeee-0000-0000-0000-000000000005',  75, NOW()),

  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000005', 'eeeeeeee-0000-0000-0000-000000000001', 100, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000005', 'eeeeeeee-0000-0000-0000-000000000002', 100, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000005', 'eeeeeeee-0000-0000-0000-000000000003', 100, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000005', 'eeeeeeee-0000-0000-0000-000000000004',  75, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000005', 'eeeeeeee-0000-0000-0000-000000000005', 100, NOW()),

  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000006', 'eeeeeeee-0000-0000-0000-000000000001',  75, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000006', 'eeeeeeee-0000-0000-0000-000000000002', 100, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000006', 'eeeeeeee-0000-0000-0000-000000000003',  75, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000006', 'eeeeeeee-0000-0000-0000-000000000004', 100, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000006', 'eeeeeeee-0000-0000-0000-000000000005',  75, NOW()),

  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000007', 'eeeeeeee-0000-0000-0000-000000000001', 100, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000007', 'eeeeeeee-0000-0000-0000-000000000002',  75, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000007', 'eeeeeeee-0000-0000-0000-000000000003', 100, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000007', 'eeeeeeee-0000-0000-0000-000000000004',  75, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000007', 'eeeeeeee-0000-0000-0000-000000000005', 100, NOW()),

  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000008', 'eeeeeeee-0000-0000-0000-000000000001', 100, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000008', 'eeeeeeee-0000-0000-0000-000000000002',  75, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000008', 'eeeeeeee-0000-0000-0000-000000000003',  75, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000008', 'eeeeeeee-0000-0000-0000-000000000004', 100, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000008', 'eeeeeeee-0000-0000-0000-000000000005',  50, NOW()),

  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000009', 'eeeeeeee-0000-0000-0000-000000000001',  75, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000009', 'eeeeeeee-0000-0000-0000-000000000002',  50, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000009', 'eeeeeeee-0000-0000-0000-000000000003', 100, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000009', 'eeeeeeee-0000-0000-0000-000000000004',  75, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000009', 'eeeeeeee-0000-0000-0000-000000000005',  75, NOW()),

  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000010', 'eeeeeeee-0000-0000-0000-000000000001',  75, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000010', 'eeeeeeee-0000-0000-0000-000000000002',  75, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000010', 'eeeeeeee-0000-0000-0000-000000000003',  50, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000010', 'eeeeeeee-0000-0000-0000-000000000004',  75, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000010', 'eeeeeeee-0000-0000-0000-000000000005',  75, NOW()),

  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000011', 'eeeeeeee-0000-0000-0000-000000000001',  50, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000011', 'eeeeeeee-0000-0000-0000-000000000002',  75, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000011', 'eeeeeeee-0000-0000-0000-000000000003',  75, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000011', 'eeeeeeee-0000-0000-0000-000000000004',  50, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000011', 'eeeeeeee-0000-0000-0000-000000000005',  75, NOW()),

  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000012', 'eeeeeeee-0000-0000-0000-000000000001',  75, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000012', 'eeeeeeee-0000-0000-0000-000000000002',  50, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000012', 'eeeeeeee-0000-0000-0000-000000000003',  50, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000012', 'eeeeeeee-0000-0000-0000-000000000004',  75, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000012', 'eeeeeeee-0000-0000-0000-000000000005',  50, NOW()),

  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000013', 'eeeeeeee-0000-0000-0000-000000000001',  50, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000013', 'eeeeeeee-0000-0000-0000-000000000002',  50, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000013', 'eeeeeeee-0000-0000-0000-000000000003',  75, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000013', 'eeeeeeee-0000-0000-0000-000000000004',  50, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000013', 'eeeeeeee-0000-0000-0000-000000000005',  50, NOW()),

  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000014', 'eeeeeeee-0000-0000-0000-000000000001',  25, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000014', 'eeeeeeee-0000-0000-0000-000000000002',  25, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000014', 'eeeeeeee-0000-0000-0000-000000000003',  25, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000014', 'eeeeeeee-0000-0000-0000-000000000004',  25, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000014', 'eeeeeeee-0000-0000-0000-000000000005',  25, NOW()),

  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000015', 'eeeeeeee-0000-0000-0000-000000000001',  50, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000015', 'eeeeeeee-0000-0000-0000-000000000002',  25, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000015', 'eeeeeeee-0000-0000-0000-000000000003',  25, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000015', 'eeeeeeee-0000-0000-0000-000000000004',  50, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000015', 'eeeeeeee-0000-0000-0000-000000000005',  25, NOW());

INSERT INTO user_daily_mission_progress (id, user_id, mission_id, date, current_progress, is_completed, completed_at)
VALUES
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000002', '44444444-4444-4444-4444-444444444444', CURRENT_DATE, 1, true, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000003', '44444444-4444-4444-4444-444444444444', CURRENT_DATE, 1, true, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000002', '55555555-5555-5555-5555-555555555555', CURRENT_DATE, 1, true, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000003', '55555555-5555-5555-5555-555555555555', CURRENT_DATE, 1, true, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000005', '44444444-4444-4444-4444-444444444444', CURRENT_DATE, 1, true, NOW()),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000005', '55555555-5555-5555-5555-555555555555', CURRENT_DATE, 1, true, NOW())
ON CONFLICT (user_id, mission_id, date) DO NOTHING;

INSERT INTO user_achievement_progress (id, user_id, achievement_id, current_progress, is_completed, completed_at, is_displayed_on_profile)
VALUES
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000002', 'bbbbbbbb-0000-0000-0000-000000000001', 1, true,  NOW(), true),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000003', 'bbbbbbbb-0000-0000-0000-000000000001', 1, true,  NOW(), true),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000004', 'bbbbbbbb-0000-0000-0000-000000000001', 1, true,  NOW(), true),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000005', 'bbbbbbbb-0000-0000-0000-000000000001', 1, true,  NOW(), true),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000006', 'bbbbbbbb-0000-0000-0000-000000000001', 1, true,  NOW(), true),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000007', 'bbbbbbbb-0000-0000-0000-000000000001', 1, true,  NOW(), true),

  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000002', '11111111-1111-1111-1111-111111111111', 5, true,  NOW(), true),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000005', '11111111-1111-1111-1111-111111111111', 5, true,  NOW(), true),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000003', '11111111-1111-1111-1111-111111111111', 5, true,  NOW(), true),

  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000002', '33333333-3333-3333-3333-333333333333', 1, true,  NOW(), true),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000003', '33333333-3333-3333-3333-333333333333', 1, true,  NOW(), true),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000004', '33333333-3333-3333-3333-333333333333', 1, true,  NOW(), true),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000005', '33333333-3333-3333-3333-333333333333', 1, true,  NOW(), true),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000006', '33333333-3333-3333-3333-333333333333', 1, true,  NOW(), true),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000007', '33333333-3333-3333-3333-333333333333', 1, true,  NOW(), true),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000008', '33333333-3333-3333-3333-333333333333', 1, true,  NOW(), true),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000009', '33333333-3333-3333-3333-333333333333', 1, true,  NOW(), true),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000010', '33333333-3333-3333-3333-333333333333', 1, true,  NOW(), true),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000011', '33333333-3333-3333-3333-333333333333', 1, true,  NOW(), true),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000012', '33333333-3333-3333-3333-333333333333', 1, true,  NOW(), true),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000013', '33333333-3333-3333-3333-333333333333', 1, true,  NOW(), true),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000014', '33333333-3333-3333-3333-333333333333', 1, true,  NOW(), true),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000015', '33333333-3333-3333-3333-333333333333', 1, true,  NOW(), true),

  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000002', '22222222-2222-2222-2222-222222222222', 5, false, null, false),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000005', '22222222-2222-2222-2222-222222222222', 5, false, null, false),
  (gen_random_uuid(), 'aaaaaaaa-0000-0000-0000-000000000008', '22222222-2222-2222-2222-222222222222', 5, false, null, false)
ON CONFLICT (user_id, achievement_id) DO NOTHING;

INSERT INTO league_season (id, started_at)
VALUES (1, NOW() - INTERVAL '30 days')
ON CONFLICT (id) DO NOTHING;

INSERT INTO forum_comments (id, reading_id, author_id, author_name, parent_id, content, deleted, created_at) VALUES
  (gen_random_uuid(), 'eeeeeeee-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000002', 'Rizky Pratama',    null, 'Artikel ini sangat membantu memahami mengapa domain layer harus dipisahkan dari infrastruktur. Saya langsung bisa melihat manfaatnya saat menulis unit test tanpa perlu mock database.', false, NOW() - INTERVAL '2 days'),
  (gen_random_uuid(), 'eeeeeeee-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000005', 'Maya Sari',        null, 'Penjelasan tentang Strategy Pattern dan Decorator Pattern sangat jelas. Saya baru menyadari bahwa kedua pattern ini bisa dikombinasikan untuk membangun sistem yang sangat fleksibel.', false, NOW() - INTERVAL '1 day'),
  (gen_random_uuid(), 'eeeeeeee-0000-0000-0000-000000000002', 'aaaaaaaa-0000-0000-0000-000000000006', 'Gilang Ramadhan',  null, 'Bagian tentang risiko AI dalam pendidikan membuka perspektif baru bagi saya. Kita perlu memastikan teknologi ini digunakan sebagai alat bantu, bukan pengganti kemampuan berpikir.', false, NOW() - INTERVAL '3 days'),
  (gen_random_uuid(), 'eeeeeeee-0000-0000-0000-000000000002', 'aaaaaaaa-0000-0000-0000-000000000008', 'Dinda Putri',      null, 'Saya tertarik dengan konsep Personalized Learning. Apakah ada platform edukasi lokal yang sudah menerapkan adaptive learning system seperti yang dijelaskan di artikel ini?', false, NOW() - INTERVAL '2 days'),
  (gen_random_uuid(), 'eeeeeeee-0000-0000-0000-000000000003', 'aaaaaaaa-0000-0000-0000-000000000003', 'Farhan Hakim',     null, 'Penjelasan SQL Injection di artikel ini ringkas tapi tepat sasaran. Penting banget bagi developer untuk selalu menggunakan parameterized query agar terhindar dari serangan ini.', false, NOW() - INTERVAL '4 days'),
  (gen_random_uuid(), 'eeeeeeee-0000-0000-0000-000000000003', 'aaaaaaaa-0000-0000-0000-000000000010', 'Bagas Wicaksono',  null, 'Two-Factor Authentication memang sudah seharusnya jadi standar di setiap aplikasi yang menyimpan data sensitif. Implementasinya tidak terlalu sulit tapi manfaat keamanannya sangat besar.', false, NOW() - INTERVAL '1 day'),
  (gen_random_uuid(), 'eeeeeeee-0000-0000-0000-000000000004', 'aaaaaaaa-0000-0000-0000-000000000007', 'Bayu Kurniawan',   null, 'Siklus TDD merah-hijau-refaktor awalnya terasa lambat, tapi setelah mencoba sendiri saya sadar bahwa ini justru menghemat waktu debugging secara keseluruhan di jangka panjang.', false, NOW() - INTERVAL '5 days'),
  (gen_random_uuid(), 'eeeeeeee-0000-0000-0000-000000000004', 'aaaaaaaa-0000-0000-0000-000000000012', 'Hendra Kusuma',    null, 'Daily Standup yang efektif memang tidak boleh lebih dari 15 menit. Fokus pada tiga pertanyaan: apa yang sudah dikerjakan, apa yang akan dikerjakan, dan apa hambatannya.', false, NOW() - INTERVAL '2 days'),
  (gen_random_uuid(), 'eeeeeeee-0000-0000-0000-000000000005', 'aaaaaaaa-0000-0000-0000-000000000009', 'Sinta Rahayu',     null, 'Teorema CAP sangat relevan saat merancang sistem terdistribusi. Di sistem perbankan misalnya, Consistency adalah prioritas utama sehingga kita harus rela mengorbankan Availability saat terjadi partisi jaringan.', false, NOW() - INTERVAL '3 days'),
  (gen_random_uuid(), 'eeeeeeee-0000-0000-0000-000000000005', 'aaaaaaaa-0000-0000-0000-000000000013', 'Laras Indah',      null, 'Penjelasan API Gateway dalam artikel ini sangat membantu. Saya sekarang lebih paham mengapa semua permintaan klien harus melewati satu titik masuk terpusat sebelum diteruskan ke masing-masing layanan.', false, NOW() - INTERVAL '1 day');
