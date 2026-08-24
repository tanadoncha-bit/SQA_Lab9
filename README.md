# Lab#9 – Test Double (Stub & Mock)

CP353201 Software Quality Assurance, College of Computing, Khon Kaen University
ปีการศึกษา 1/2569 · Instructor: Asst.Prof. Chitsutha Soomlek

## How to run

```bash
mvn test
```

Requires Java 11+ and internet access the first time (Maven downloads
JUnit 5.14.4 and Mockito 5.14.0 from Maven Central).

## Project layout

```
src/main/java/
  com/kku/sqa/lab9/playlist/   Activity 9.1 – NowPlaying / MovieService / MoviePortal
  sqa/lab/service/             Activity 9.2 – the professor's starter classes,
                               copied as-is from testdouble-mockito-lab:
                               SeatDAO, SeatReservation, TicketCounter, GateCheckin

src/test/java/
  com/kku/sqa/lab9/playlist/   NowPlayingTest + a hand-written Stub
  sqa/lab/service/             SeatReservationTest, GateCheckinTest (Mockito mocks)
```

## Activity 9.1 – Stub

**Dependency analysis**

```
Class NowPlaying  --->  <<interface>> MovieService  --->  <<external>> MoviePortal
```

`NowPlaying` (the SUT) only depends on the `MovieService` interface. The
real `MovieServiceImpl` forwards requests to the external `MoviePortal`
(a third-party movie-listing provider), which is the part we cannot and
should not call from a unit test.

**Test Double used:** a hand-written **Stub** (`StubMovieService`) that
implements `MovieService` and always returns a fixed list of five movies
across several cinema types (VIP, IMAX Laser, Standard, 4DX). It has no
verification/expectation logic — it just supplies canned data so
`NowPlaying`'s own filtering logic can be exercised deterministically.

**What's tested:** `NowPlayingTest` verifies that
`getMoviesByCinemaType(location, date, "VIP")` returns only the two VIP
entries out of the five stubbed movies (plus a case-insensitivity check
and a no-match-returns-empty-list check).

## Activity 9.2 – Mocks (Mockito)

The four starter classes under `sqa.lab.service` (`SeatDAO`,
`SeatReservation`, `TicketCounter`, `GateCheckin`) are copied verbatim
from the professor's starter project
(`ChitsuthaCSKKU/SQA/tree/2026/LabAssignment/Lab9_TestDouble`, mirrored
locally in `testdouble-mockito-lab`) — nothing in `src/main/java/sqa`
was modified, only tests were added.

### (a) Service: Seat Reservation

```
SeatReservation (SUT)
   .checkSeatAvailability(seatName)
   ---> SeatDAO.fetchAvailableSeats()   [mocked — this is a real JDBC call
                                          in production: DriverManager
                                          .getConnection("DATABASE_URL")]
```

`SeatReservationTest` mocks `SeatDAO` with `@Mock` and stubs
`fetchAvailableSeats()` to return a fixed list of seat names — this is
the "available seat numbers" response required by instruction 3(a).
Three cases are covered: the requested seat is in the mocked list, the
seat is not in the list, and the mocked list is empty.

### (b) Service: GateCheckin

```
GateCheckin (SUT)
   .customerEntry(ticketId) / .customerIsEligible(ticketId)
   ---> TicketCounter.changeTicketStatus(boolean)   [void — verified as
                                                       an interaction]
   ---> TicketCounter.getNoCheckinCustomer()        [mocked response —
                                                       instruction 3(b)]
```

`GateCheckinTest` mocks `TicketCounter` with `@Mock`. Because
`changeTicketStatus` is a void method, its call is asserted with
Mockito's `verify(...)` (called once when a new ticket checks in, never
called again if the same ticket id is scanned twice). A dedicated test
stubs `getNoCheckinCustomer()` to return a fixed number, directly
covering instruction 3(b)'s "mocked response for the count of visitors
who have already passed gate check-in."

## pom.xml notes

`junit-jupiter-api/engine` (5.14.4) and `mockito-core` /
`mockito-junit-jupiter` (5.14.0) versions, the `maven-dependency-plugin`
`properties` goal, and the Surefire `-javaagent:${org.mockito:mockito-core:jar}`
argLine were all carried over from the professor's starter `pom.xml` so
this project builds/runs the same way (Mockito 5's inline mock maker
needs that javaagent wired up explicitly on newer JDKs).

The argLine also adds `-Dnet.bytebuddy.experimental=true`. This is
needed when running on a JDK newer than what the bundled Byte Buddy
officially recognizes (e.g. JDK 24+, including JDK 26) — without it
Mockito throws `Java XX is not supported by the current version of
Byte Buddy` the moment any `@Mock` is created. If your machine runs an
older JDK (11–21) this flag is harmless and not required.

## Note on verification

`mvn test` has been run successfully end-to-end on a real machine
(Windows, JDK 26): compilation of all 9 main + 4 test source files
succeeds, and all 7 Mockito-based tests (`GateCheckinTest`,
`SeatReservationTest`) plus all 3 `NowPlayingTest` stub tests pass —
10/10 tests green, 0 failures.

Earlier in development this project was also reviewed in an offline
sandbox without Maven/JDK/internet access, where the code was
desk-checked by hand (method signatures cross-referenced against every
call site, brace/parenthesis balance checked programmatically, and each
Mockito test's execution path traced against `MockitoExtension`'s
strict-stubbing rule) — that review is now confirmed correct by the
actual `mvn test` run above.
