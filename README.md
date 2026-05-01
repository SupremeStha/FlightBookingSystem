# Flight Booking System

A Java-based flight booking application with both a CLI and a Swing GUI interface, built for the CMP5332 module at Sunway College Kathmandu (Birmingham City University). Designed around the Command pattern with role-based authentication, full booking lifecycle management, and automatic data backup with rollback support.

---

## Features

- **Dual interface** — interactive CLI for quick operations and a Swing GUI (`MainWindow`) for a visual experience
- **Role-based authentication** — `ADMIN` and `USER` roles with separate permissions; session management via `AuthService`
- **Flight management** — add, list, show, and soft-delete flights while maintaining referential integrity with existing bookings
- **Customer management** — register, view, update, and soft-delete customer records linked to user accounts
- **Booking lifecycle** — supports `ACTIVE`, `CANCELLED`, and `COMPLETED` statuses with a full history trail of all changes
- **One-way and round-trip bookings** — `Booking` model handles both outbound-only and outbound + return flight reservations
- **Dynamic pricing** — fare calculation via `calculateCurrentPrice()` based on flight and booking context
- **Data backup & rollback** — `DataBackup` creates timestamped backups before every save; automatically rolls back on failure and retains the last 10 backups
- **Command pattern** — 20 command classes (`AddBooking`, `CancelBooking`, `VoidBooking`, `UpdateBooking`, `BookingHistory`, etc.) all implement a shared `Command` interface

---

## Architecture

The project is organised into 6 packages:

```
bcu.cmp5332.bookingsystem/
├── auth/         AppUser, AuthService, Role, Session, UserStore
├── commands/     Command interface + 20 implementing classes
├── data/         DataManager, BookingDataManager, FlightDataManager,
│                 CustomerDataManager, FlightBookingSystemData, DataBackup
├── gui/          MainWindow, LoginWindow, RegisterWindow, AddFlightWindow,
│                 BookingHistoryDialog, ModernBackgroundPanel, UIUtil
├── main/         Main (entry point), CommandParser, FlightBookingSystemException
└── model/        FlightBookingSystem, Flight, Customer, Booking, BookingStatus
```

### Command Pattern

Every CLI action implements the `Command` interface:

```java
public interface Command {
    void execute(FlightBookingSystem fbs) throws Exception;
    default boolean changesState() { return true; }
}
```

Commands that mutate state (bookings, flights, customers) return `true` from `changesState()`, triggering an automatic save after execution.

### DataBackup

```java
DataBackup backup = new DataBackup();
try {
    backup.createBackup();           // timestamped snapshot
    FlightBookingSystemData.store(fbs);
    backup.commit();                 // mark save successful
} catch (Exception e) {
    backup.rollback();               // restore previous state
    throw e;
}
```

---

## Getting Started

### Prerequisites

- Java 17+
- Maven or any Java IDE (IntelliJ IDEA, Eclipse)

### Run

```bash
# Clone the repo
git clone https://github.com/SupremeStha/FlightBookingSystem.git
cd FlightBookingSystem

# Compile
javac -d out -sourcepath src src/bcu/cmp5332/bookingsystem/main/Main.java

# Run
java -cp out bcu.cmp5332.bookingsystem.main.Main
```

Or open in your IDE and run `Main.java` directly.

### CLI Commands

| Command | Description |
|---|---|
| `login` | Authenticate as a user or admin |
| `register` | Create a new user account |
| `listflights` | View all available flights |
| `showflight <id>` | View detailed flight info |
| `addbooking <customerID> <flightID>` | Book a flight |
| `cancelbooking <customerID> <bookingID>` | Cancel a booking |
| `updatebooking` | Change flight on an existing booking |
| `mybookings` | View your booking history |
| `loadgui` | Launch the Swing GUI |
| `help` | List all available commands |

---

## Documentation

Full Javadoc is available in the `docs/` folder. Open `docs/index.html` in a browser to browse all classes, methods, and package summaries.

---

## Academic Context

Developed as a group project (Group C5) for the **Object-Oriented Software Development** module (CMP5332) at Sunway College Kathmandu, affiliated with Birmingham City University.

---

## License

For educational purposes.
