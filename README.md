# Lab#9 – Test Double (Stub & Mock)

CP353201 การประกันคุณภาพซอฟต์แวร์ (Software Quality Assurance), วิทยาลัยการคอมพิวเตอร์ มหาวิทยาลัยขอนแก่น
ปีการศึกษา 1/2569 · ผู้สอน: Asst.Prof. Chitsutha Soomlek

## วิธีรัน

```bash
mvn test
```

ต้องใช้ Java 11+ และต้องมีอินเทอร์เน็ตในการรันครั้งแรก (Maven จะดาวน์โหลด
JUnit 5.14.4 และ Mockito 5.14.0 จาก Maven Central)

## โครงสร้างโปรเจกต์

```
src/main/java/
  com/kku/sqa/lab9/playlist/   ข้อ 9.1 – NowPlaying / MovieService / MoviePortal
  sqa/lab/service/             ข้อ 9.2 – คลาส starter ของอาจารย์
                               คัดลอกมาจาก testdouble-mockito-lab แบบไม่แก้ไข:
                               SeatDAO, SeatReservation, TicketCounter, GateCheckin

src/test/java/
  com/kku/sqa/lab9/playlist/   NowPlayingTest + Stub ที่เขียนขึ้นเอง
  sqa/lab/service/             SeatReservationTest, GateCheckinTest (ใช้ Mockito mock)
```

## ข้อ 9.1 – Stub

**การวิเคราะห์ dependency**

```
Class NowPlaying  --->  <<interface>> MovieService  --->  <<external>> MoviePortal
```

`NowPlaying` (System Under Test) พึ่งพาแค่ interface `MovieService` เท่านั้น
ส่วน `MovieServiceImpl` ตัวจริงจะส่งต่อ request ไปยัง `MoviePortal` ภายนอก
(ผู้ให้บริการข้อมูลหนังจากบุคคลที่สาม) ซึ่งเป็นส่วนที่เราไม่สามารถและไม่ควรเรียกจริง
ตอนเทส unit test

**Test Double ที่ใช้:** เขียน **Stub** ขึ้นเอง (`StubMovieService`) ที่ implement
`MovieService` และคืนค่ารายการหนังตายตัว 5 รายการ ครอบคลุมหลายประเภทโรงหนัง
(VIP, IMAX Laser, Standard, 4DX) โดย Stub นี้ไม่มี logic ตรวจสอบ/คาดหวังการเรียกใช้
(verification) ใดๆ มีหน้าที่แค่ส่งข้อมูลสำเร็จรูปให้ logic การกรองของ `NowPlaying`
ทำงานได้อย่างแน่นอน (deterministic)

**สิ่งที่ทดสอบ:** `NowPlayingTest` ตรวจสอบว่า
`getMoviesByCinemaType(location, date, "VIP")` คืนเฉพาะ 2 รายการที่เป็น VIP
จากหนังที่ stub ไว้ทั้งหมด 5 รายการ (พร้อมเคสตรวจตัวพิมพ์เล็ก/ใหญ่ และเคสไม่พบข้อมูล
ที่ต้องคืน list ว่าง)

## ข้อ 9.2 – Mock (Mockito)

คลาส starter ทั้ง 4 คลาสภายใต้ `sqa.lab.service` (`SeatDAO`,
`SeatReservation`, `TicketCounter`, `GateCheckin`) คัดลอกมาแบบเดิมทุกตัวอักษร
จากโปรเจกต์ starter ของอาจารย์
(`ChitsuthaCSKKU/SQA/tree/2026/LabAssignment/Lab9_TestDouble` ซึ่ง mirror ไว้
ในเครื่องที่ `testdouble-mockito-lab`) — ไม่มีการแก้ไขอะไรใน `src/main/java/sqa`
เลย มีการเพิ่มเฉพาะไฟล์เทสเท่านั้น

### (ก) Service: Seat Reservation

```
SeatReservation (SUT)
   .checkSeatAvailability(seatName)
   ---> SeatDAO.fetchAvailableSeats()   [mock ไว้ — ในระบบจริงเป็นการเรียก JDBC จริง
                                          ผ่าน DriverManager
                                          .getConnection("DATABASE_URL")]
```

`SeatReservationTest` mock `SeatDAO` ด้วย `@Mock` และ stub เมธอด
`fetchAvailableSeats()` ให้คืนรายการชื่อที่นั่งตายตัว — นี่คือ response
"หมายเลขที่นั่งที่ว่าง" ตามที่ข้อ 3(ก) กำหนด ครอบคลุม 3 กรณี ได้แก่
ที่นั่งที่ขอมีอยู่ใน list ที่ mock ไว้, ที่นั่งไม่มีอยู่ใน list, และ list ที่ mock ไว้เป็นค่าว่าง

### (ข) Service: GateCheckin

```
GateCheckin (SUT)
   .customerEntry(ticketId) / .customerIsEligible(ticketId)
   ---> TicketCounter.changeTicketStatus(boolean)   [void — ตรวจสอบด้วยการ
                                                       verify การเรียกใช้งาน]
   ---> TicketCounter.getNoCheckinCustomer()        [mock response —
                                                       ตามข้อ 3(ข)]
```

`GateCheckinTest` mock `TicketCounter` ด้วย `@Mock` เนื่องจาก
`changeTicketStatus` เป็นเมธอด void จึงตรวจสอบการเรียกใช้ด้วย Mockito
`verify(...)` (ถูกเรียก 1 ครั้งตอนตั๋วใหม่เข้ามา และต้องไม่ถูกเรียกอีกถ้าสแกน
ตั๋วเดิมซ้ำ) และมีเทสเฉพาะที่ stub เมธอด `getNoCheckinCustomer()` ให้คืนจำนวน
ตายตัว ครอบคลุมข้อ 3(ข) เรื่อง "mock response สำหรับจำนวนผู้เข้าชมที่ผ่าน
gate check-in แล้ว" โดยตรง

## หมายเหตุเกี่ยวกับ pom.xml

เวอร์ชันของ `junit-jupiter-api/engine` (5.14.4) และ `mockito-core` /
`mockito-junit-jupiter` (5.14.0), goal `properties` ของ
`maven-dependency-plugin`, และ argLine
`-javaagent:${org.mockito:mockito-core:jar}` ของ Surefire ทั้งหมดนี้
คัดลอกมาจาก `pom.xml` ของ starter ของอาจารย์ เพื่อให้โปรเจกต์นี้ build/run
ได้เหมือนกัน (inline mock maker ของ Mockito 5 ต้องมีการตั้งค่า javaagent
แบบนี้ชัดเจนบน JDK รุ่นใหม่ๆ)

argLine ยังเพิ่ม `-Dnet.bytebuddy.experimental=true` เข้าไปด้วย ซึ่งจำเป็น
เมื่อรันบน JDK ที่ใหม่กว่าที่ Byte Buddy เวอร์ชันที่ผูกมารองรับอย่างเป็นทางการ
(เช่น JDK 24 ขึ้นไป รวมถึง JDK 26) — ถ้าไม่ตั้งค่านี้ Mockito จะ throw
`Java XX is not supported by the current version of Byte Buddy` ทันทีที่มี
การสร้าง `@Mock` ตัวใดก็ตาม ถ้าเครื่องคุณใช้ JDK รุ่นเก่ากว่า (11–21) flag นี้
ไม่มีผลเสียอะไรและไม่จำเป็นต้องใช้ก็ยังรันได้ปกติ

## หมายเหตุเกี่ยวกับการตรวจสอบความถูกต้อง

รัน `mvn test` บนเครื่องจริงสำเร็จครบทุกขั้นตอนแล้ว (Windows, JDK 26):
compile ไฟล์ main 9 ไฟล์ + test 4 ไฟล์ ผ่านหมด และเทสทั้ง 7 เคสที่ใช้
Mockito (`GateCheckinTest`, `SeatReservationTest`) รวมกับเทส Stub อีก 3
เคสใน `NowPlayingTest` ผ่านหมด — รวม 10/10 เทสเขียว ไม่มี failure

ก่อนหน้านี้ในขั้นตอนพัฒนา โปรเจกต์นี้ยังถูกตรวจทานในแซนด์บ็อกซ์ที่ไม่มี
Maven/JDK/อินเทอร์เน็ต โดยตรวจด้วยมือ (cross-reference method signature
กับจุดที่เรียกใช้ทุกจุด, ตรวจสอบความสมดุลของวงเล็บ/ปีกกาด้วยสคริปต์, และ
ไล่ execution path ของแต่ละเทส Mockito เทียบกับกฎ strict stubbing ของ
`MockitoExtension`) — การตรวจทานนั้นได้รับการยืนยันว่าถูกต้องแล้วจากผลการรัน
`mvn test` จริงข้างต้น
