# NESTORA — Apartment Management System (Console Edition)

কোনো MySQL, JavaFX বা Maven লাগবে না। শুধু Java (JDK) থাকলেই চলবে।
সব ডেটা `data/` ফোল্ডারের ভেতর সাধারণ `.csv` ফাইলে সেভ হয়।

---

## ১. যা লাগবে

শুধু **JDK (Java Development Kit) 17 বা তার বেশি**। JRE (শুধু run করার জন্য) দিয়ে হবে না —
কম্পাইল করতে `javac` লাগবে, যেটা শুধু JDK-তে থাকে।

চেক করুন আপনার কম্পিউটারে JDK ঠিকঠাক আছে কিনা:

```
javac -version
java -version
```

দুটো কমান্ডই যদি ভার্সন নাম্বার দেখায়, আপনি রেডি। যদি `javac` চিনতে না পারে (`is not recognized`),
তাহলে [https://adoptium.net](https://adoptium.net) থেকে JDK 21 (Temurin) ডাউনলোড করে ইনস্টল করুন
— ইনস্টলের সময় **"Add to PATH"** অপশনটা টিক দিয়ে দেবেন।

---

## ২. ফোল্ডার স্ট্রাকচার

```
nestora-console/
├── run.bat                 <- Windows-এ ডাবল-ক্লিক করলেই কম্পাইল + রান হয়ে যাবে
├── run.sh                  <- Mac/Linux-এর জন্য
├── data/                   <- অ্যাপ প্রথমবার চালালে এখানে .csv ফাইল অটো তৈরি হবে
└── src/com/nestora/
    ├── Main.java            <- এখান থেকেই অ্যাপ শুরু হয়
    ├── model/                <- User, Apartment, Complaint, Notice, Payment
    ├── dao/                  <- প্রতিটা .csv ফাইল পড়া/লেখার কোড
    ├── service/              <- AuthService (রেজিস্ট্রেশন/লগইন লজিক)
    └── util/                 <- CsvUtil, PasswordUtil, ConsoleUtil
```

---

## ৩. কীভাবে রান করবেন (Windows)

`nestora-console` ফোল্ডারে গিয়ে **`run.bat`** ফাইলে ডাবল-ক্লিক করুন।

অথবা Command Prompt/PowerShell দিয়ে:

```
cd D:\nestora-console
run.bat
```

এটা নিজে থেকেই সব `.java` ফাইল কম্পাইল করে `out` ফোল্ডারে রাখবে, তারপর অ্যাপ চালু করবে।

## কীভাবে রান করবেন (Mac/Linux)

```bash
cd nestora-console
./run.sh
```

## ম্যানুয়ালি রান করতে চাইলে (যেকোনো OS)

```bash
javac -d out $(find src -name "*.java")     # Windows-এ PowerShell হলে নিচের কমান্ড ব্যবহার করুন
java -cp out com.nestora.Main
```

Windows CMD-এ (find কমান্ড ছাড়া) — শুধু `run.bat` ব্যবহার করাই সবচেয়ে সহজ।

---

## ৪. প্রথমবার চালালে কী হবে

কনসোলে একটা মেনু আসবে:

```
[1] Register as Resident
[2] Login as Resident
[3] Login as Admin
[4] Exit
```

- **Admin দিয়ে ঢুকতে**: option 3 চাপুন। ডিফল্ট অ্যাডমিন লগইন —
  - Email: `admin@nestora.com`
  - Password: `admin123`
  (এটা প্রথমবার রান করলেই অটো তৈরি হয়ে যায়।)

- **Resident হিসেবে ঢুকতে**: প্রথমে option 1 দিয়ে রেজিস্টার করুন। রেজিস্ট্রেশনের পর অ্যাকাউন্ট
  "Pending Verification" অবস্থায় থাকবে — Admin প্যানেল থেকে **"Verify a Resident"** দিয়ে approve
  না করা পর্যন্ত সেই resident লগইন করতে পারবে না (ঠিক যেমন realistic অ্যাপে হয়)।

---

## ৫. Admin হিসেবে কী কী করা যাবে

- Dashboard Summary (মোট resident, apartment, revenue ইত্যাদি)
- সব Resident দেখা + Verify করা
- Apartment যোগ করা / দেখা
- সব Complaint দেখা + status আপডেট করা (PENDING → IN_PROGRESS → RESOLVED)
- Notice পোস্ট করা
- সব Payment দেখা

## Resident হিসেবে কী কী করা যাবে

- প্রোফাইল দেখা
- নিজের Apartment-এর তথ্য দেখা
- Complaint সাবমিট করা + নিজের Complaint-এর status দেখা
- Notice Board দেখা
- Payment করা (simulate — সত্যিকারের payment gateway না) + Payment History দেখা
- Emergency contact দেখা

---

## ৬. ডেটা কোথায় সেভ হয়?

`data/` ফোল্ডারে এই ফাইলগুলো অটো তৈরি হবে:

- `users.csv` — সব resident + admin
- `apartments.csv`
- `complaints.csv`
- `notices.csv`
- `payments.csv`

চাইলে এগুলো Notepad/Excel দিয়ে খুলে দেখতে পারেন (কলাম আলাদা করা আছে `|` চিহ্ন দিয়ে, কমা দিয়ে না —
কারণ description-এর মতো লেখায় কমা থাকতে পারে)। পাসওয়ার্ড কখনোই plain text-এ সেভ হয় না,
সবসময় hash করে রাখা হয়।

**ডেটা রিসেট করতে চাইলে** শুধু `data` ফোল্ডারটা ডিলিট করে দিন — পরের বার রান করলে আবার নতুন করে
ফাইল তৈরি হয়ে যাবে (এবং default admin-ও আবার তৈরি হবে)।

---

## ৭. এরপর কী?

এই ভার্সনে আছে: Login/Registration, Resident Verification, Apartment Management,
Complaint/Help Desk, Notice Board, Payment (simulated), Admin Dashboard Summary।

চাইলে পরের ধাপে যোগ করা যায়: Parking Management, Visitor Pass + QR Code, Amenity Booking,
Rating & Feedback, Reports/Export ইত্যাদি — বলুন কোনটা আগে দরকার, সেটাই বানিয়ে দেব।
