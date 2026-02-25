package bcu.cmp5332.bookingsystem.model;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for the Customer class.
 * Tests all methods, validation logic, booking management, and edge cases.
 * These tests are completely isolated and do NOT modify any data files.
 */
@DisplayName("Customer Class Tests")
class CustomerTest {

    private Customer customer;
    private Flight flight1;
    private Flight flight2;
    private Flight flight3;
    private Flight returnFlight1;
    private Booking booking1;
    private Booking booking2;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();
        customer = new Customer(1, "John Doe", "1234567890", "john@email.com");
        flight1 = new Flight(101, "BA123", "London", "Paris", today.plusDays(10), 100, 150.00);
        flight2 = new Flight(102, "BA456", "Paris", "Rome", today.plusDays(15), 100, 200.00);
        flight3 = new Flight(103, "BA789", "London", "Berlin", today.plusDays(20), 100, 180.00);
        returnFlight1 = new Flight(104, "BA124", "Paris", "London", today.plusDays(17), 100, 150.00);
        booking1 = new Booking(1, customer, flight1, today, 150.00);
        booking2 = new Booking(2, customer, flight2, today, 200.00);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create customer with valid parameters")
        void testValidConstruction() {
            assertNotNull(customer);
            assertEquals(1, customer.getId());
            assertEquals("John Doe", customer.getName());
            assertEquals("1234567890", customer.getPhone());
            assertEquals("john@email.com", customer.getEmail());
            assertFalse(customer.isDeleted());
        }

        @Test
        @DisplayName("Should trim whitespace from input fields")
        void testTrimWhitespace() {
            Customer c = new Customer(2, "  Jane Smith  ", "  9876543210  ", "  jane@email.com  ");
            assertEquals("Jane Smith", c.getName());
            assertEquals("9876543210", c.getPhone());
            assertEquals("jane@email.com", c.getEmail());
        }

        @Test
        @DisplayName("Should handle null name by converting to empty string")
        void testNullName() {
            Customer c = new Customer(3, null, "1234567890", "test@email.com");
            assertEquals("", c.getName());
        }

        @Test
        @DisplayName("Should handle null phone by converting to empty string")
        void testNullPhone() {
            Customer c = new Customer(4, "Test User", null, "test@email.com");
            assertEquals("", c.getPhone());
        }

        @Test
        @DisplayName("Should handle null email by converting to empty string")
        void testNullEmail() {
            Customer c = new Customer(5, "Test User", "1234567890", null);
            assertEquals("", c.getEmail());
        }

        @Test
        @DisplayName("Should throw exception when name contains delimiter")
        void testNameWithDelimiter() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Customer(6, "John::Doe", "1234567890", "john@email.com")
            );
            assertTrue(exception.getMessage().contains("Name cannot contain '::'"));
        }

        @Test
        @DisplayName("Should throw exception when phone contains delimiter")
        void testPhoneWithDelimiter() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Customer(7, "John Doe", "123::456", "john@email.com")
            );
            assertTrue(exception.getMessage().contains("Phone cannot contain '::'"));
        }

        @Test
        @DisplayName("Should throw exception when email contains delimiter")
        void testEmailWithDelimiter() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Customer(8, "John Doe", "1234567890", "john::email.com")
            );
            assertTrue(exception.getMessage().contains("Email cannot contain '::'"));
        }

        @Test
        @DisplayName("Should initialize with empty bookings list")
        void testEmptyBookingsInitialization() {
            List<Booking> bookings = customer.getBookings();
            assertNotNull(bookings);
            assertTrue(bookings.isEmpty());
        }
    }

    @Nested
    @DisplayName("Getter Tests")
    class GetterTests {

        @Test
        @DisplayName("getId should return correct customer ID")
        void testGetId() {
            assertEquals(1, customer.getId());
        }

        @Test
        @DisplayName("getName should return correct name")
        void testGetName() {
            assertEquals("John Doe", customer.getName());
        }

        @Test
        @DisplayName("getPhone should return correct phone")
        void testGetPhone() {
            assertEquals("1234567890", customer.getPhone());
        }

        @Test
        @DisplayName("getEmail should return correct email")
        void testGetEmail() {
            assertEquals("john@email.com", customer.getEmail());
        }

        @Test
        @DisplayName("isDeleted should return false initially")
        void testIsDeletedInitial() {
            assertFalse(customer.isDeleted());
        }

        @Test
        @DisplayName("getBookings should return unmodifiable list")
        void testGetBookingsUnmodifiable() throws FlightBookingSystemException {
            customer.addBooking(booking1);
            List<Booking> bookings = customer.getBookings();
            
            // Attempting to modify should throw exception
            assertThrows(UnsupportedOperationException.class, 
                () -> bookings.add(booking2));
        }
    }

    @Nested
    @DisplayName("Setter Tests")
    class SetterTests {

        @Test
        @DisplayName("setName should update name correctly")
        void testSetNameValid() throws FlightBookingSystemException {
            customer.setName("Jane Smith");
            assertEquals("Jane Smith", customer.getName());
        }

        @Test
        @DisplayName("setName should trim whitespace")
        void testSetNameTrimming() throws FlightBookingSystemException {
            customer.setName("  Bob Wilson  ");
            assertEquals("Bob Wilson", customer.getName());
        }

        @Test
        @DisplayName("setName should handle null by setting empty string")
        void testSetNameNull() throws FlightBookingSystemException {
            customer.setName(null);
            assertEquals("", customer.getName());
        }

        @Test
        @DisplayName("setName should throw exception for delimiter")
        void testSetNameWithDelimiter() {
            FlightBookingSystemException exception = assertThrows(
                FlightBookingSystemException.class,
                () -> customer.setName("Jane::Smith")
            );
            assertTrue(exception.getMessage().contains("Name cannot contain '::'"));
        }

        @Test
        @DisplayName("setPhone should update phone correctly")
        void testSetPhoneValid() throws FlightBookingSystemException {
            customer.setPhone("9876543210");
            assertEquals("9876543210", customer.getPhone());
        }

        @Test
        @DisplayName("setPhone should trim whitespace")
        void testSetPhoneTrimming() throws FlightBookingSystemException {
            customer.setPhone("  5551234567  ");
            assertEquals("5551234567", customer.getPhone());
        }

        @Test
        @DisplayName("setPhone should handle null by setting empty string")
        void testSetPhoneNull() throws FlightBookingSystemException {
            customer.setPhone(null);
            assertEquals("", customer.getPhone());
        }

        @Test
        @DisplayName("setPhone should throw exception for delimiter")
        void testSetPhoneWithDelimiter() {
            FlightBookingSystemException exception = assertThrows(
                FlightBookingSystemException.class,
                () -> customer.setPhone("123::456")
            );
            assertTrue(exception.getMessage().contains("Phone cannot contain '::'"));
        }

        @Test
        @DisplayName("setEmail should update email correctly")
        void testSetEmailValid() throws FlightBookingSystemException {
            customer.setEmail("newemail@test.com");
            assertEquals("newemail@test.com", customer.getEmail());
        }

        @Test
        @DisplayName("setEmail should trim whitespace")
        void testSetEmailTrimming() throws FlightBookingSystemException {
            customer.setEmail("  test@example.com  ");
            assertEquals("test@example.com", customer.getEmail());
        }

        @Test
        @DisplayName("setEmail should handle null by setting empty string")
        void testSetEmailNull() throws FlightBookingSystemException {
            customer.setEmail(null);
            assertEquals("", customer.getEmail());
        }

        @Test
        @DisplayName("setEmail should throw exception for delimiter")
        void testSetEmailWithDelimiter() {
            FlightBookingSystemException exception = assertThrows(
                FlightBookingSystemException.class,
                () -> customer.setEmail("test::email.com")
            );
            assertTrue(exception.getMessage().contains("Email cannot contain '::'"));
        }

        @Test
        @DisplayName("setDeleted should update deleted status")
        void testSetDeleted() {
            customer.setDeleted(true);
            assertTrue(customer.isDeleted());
            customer.setDeleted(false);
            assertFalse(customer.isDeleted());
        }
    }

    @Nested
    @DisplayName("Add Booking Tests")
    class AddBookingTests {

        @Test
        @DisplayName("Should successfully add booking")
        void testAddBookingSuccess() throws FlightBookingSystemException {
            customer.addBooking(booking1);
            assertEquals(1, customer.getBookings().size());
            assertTrue(customer.getBookings().contains(booking1));
        }

        @Test
        @DisplayName("Should add multiple different bookings")
        void testAddMultipleBookings() throws FlightBookingSystemException {
            customer.addBooking(booking1);
            customer.addBooking(booking2);
            assertEquals(2, customer.getBookings().size());
        }

        @Test
        @DisplayName("Should throw exception when adding null booking")
        void testAddNullBooking() {
            FlightBookingSystemException exception = assertThrows(
                FlightBookingSystemException.class,
                () -> customer.addBooking(null)
            );
            assertEquals("Cannot add a null booking.", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception for duplicate one-way trip")
        void testDuplicateOneWayTrip() throws FlightBookingSystemException {
            Booking booking1 = new Booking(1, customer, flight1, today, 150.00);
            Booking duplicateBooking = new Booking(2, customer, flight1, today, 150.00);
            
            customer.addBooking(booking1);
            
            FlightBookingSystemException exception = assertThrows(
                FlightBookingSystemException.class,
                () -> customer.addBooking(duplicateBooking)
            );
            assertTrue(exception.getMessage().contains("already has a booking for the same trip"));
        }

        @Test
        @DisplayName("Should throw exception for duplicate round trip")
        void testDuplicateRoundTrip() throws FlightBookingSystemException {
            Booking roundTrip1 = new Booking(1, customer, flight1, returnFlight1, today, 300.00);
            Booking duplicateRoundTrip = new Booking(2, customer, flight1, returnFlight1, today, 300.00);
            
            customer.addBooking(roundTrip1);
            
            FlightBookingSystemException exception = assertThrows(
                FlightBookingSystemException.class,
                () -> customer.addBooking(duplicateRoundTrip)
            );
            assertTrue(exception.getMessage().contains("already has a booking for the same trip"));
        }

        @Test
        @DisplayName("Should allow same flight after cancellation")
        void testRebookAfterCancellation() throws FlightBookingSystemException {
            Booking booking1 = new Booking(1, customer, flight1, today, 150.00);
            Booking booking2 = new Booking(2, customer, flight1, today, 150.00);
            
            customer.addBooking(booking1);
            booking1.setStatus(BookingStatus.CANCELLED);
            
            // Should not throw exception since first booking is cancelled
            assertDoesNotThrow(() -> customer.addBooking(booking2));
            assertEquals(2, customer.getBookings().size());
        }

        @Test
        @DisplayName("Should allow different flights")
        void testDifferentFlights() throws FlightBookingSystemException {
            Booking booking1 = new Booking(1, customer, flight1, today, 150.00);
            Booking booking2 = new Booking(2, customer, flight2, today, 200.00);
            
            customer.addBooking(booking1);
            customer.addBooking(booking2);
            
            assertEquals(2, customer.getBookings().size());
        }

        @Test
        @DisplayName("Should allow one-way and round-trip on same outbound flight")
        void testOneWayAndRoundTripDifferent() throws FlightBookingSystemException {
            Booking oneWay = new Booking(1, customer, flight1, today, 150.00);
            Booking roundTrip = new Booking(2, customer, flight1, returnFlight1, today, 300.00);
            
            customer.addBooking(oneWay);
            
            // Should allow because one-way and round-trip are different trip types
            assertDoesNotThrow(() -> customer.addBooking(roundTrip));
        }
    }

    @Nested
    @DisplayName("Remove Booking Tests")
    class RemoveBookingTests {

        @Test
        @DisplayName("Should successfully remove existing booking")
        void testRemoveExistingBooking() throws FlightBookingSystemException {
            customer.addBooking(booking1);
            customer.removeBooking(booking1);
            
            assertEquals(0, customer.getBookings().size());
        }

        @Test
        @DisplayName("Should throw exception when removing null booking")
        void testRemoveNullBooking() {
            FlightBookingSystemException exception = assertThrows(
                FlightBookingSystemException.class,
                () -> customer.removeBooking(null)
            );
            assertEquals("Booking not found for this customer.", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when removing non-existent booking")
        void testRemoveNonExistentBooking() {
            FlightBookingSystemException exception = assertThrows(
                FlightBookingSystemException.class,
                () -> customer.removeBooking(booking1)
            );
            assertEquals("Booking not found for this customer.", exception.getMessage());
        }

        @Test
        @DisplayName("Should remove correct booking from multiple bookings")
        void testRemoveFromMultipleBookings() throws FlightBookingSystemException {
            customer.addBooking(booking1);
            customer.addBooking(booking2);
            customer.removeBooking(booking1);
            
            assertEquals(1, customer.getBookings().size());
            assertTrue(customer.getBookings().contains(booking2));
            assertFalse(customer.getBookings().contains(booking1));
        }
    }

    @Nested
    @DisplayName("Find Booking By Flight ID Tests")
    class FindBookingByFlightIdTests {

        @Test
        @DisplayName("Should find booking by outbound flight ID")
        void testFindByOutboundFlightId() throws FlightBookingSystemException {
            customer.addBooking(booking1);
            Booking found = customer.findBookingByFlightId(flight1.getId());
            
            assertNotNull(found);
            assertEquals(booking1, found);
        }

        @Test
        @DisplayName("Should find booking by return flight ID")
        void testFindByReturnFlightId() throws FlightBookingSystemException {
            Booking roundTrip = new Booking(3, customer, flight1, returnFlight1, today, 300.00);
            customer.addBooking(roundTrip);
            
            Booking found = customer.findBookingByFlightId(returnFlight1.getId());
            assertNotNull(found);
            assertEquals(roundTrip, found);
        }

        @Test
        @DisplayName("Should return null when flight ID not found")
        void testFindNonExistentFlightId() throws FlightBookingSystemException {
            customer.addBooking(booking1);
            Booking found = customer.findBookingByFlightId(999);
            
            assertNull(found);
        }

        @Test
        @DisplayName("Should return null for empty bookings")
        void testFindInEmptyBookings() {
            Booking found = customer.findBookingByFlightId(101);
            assertNull(found);
        }

        @Test
        @DisplayName("Should find correct booking among multiple bookings")
        void testFindAmongMultiple() throws FlightBookingSystemException {
            customer.addBooking(booking1);
            customer.addBooking(booking2);
            
            Booking found = customer.findBookingByFlightId(flight2.getId());
            assertNotNull(found);
            assertEquals(booking2, found);
        }
    }

    @Nested
    @DisplayName("Is Already On Flight Tests")
    class IsAlreadyOnFlightTests {

        @Test
        @DisplayName("Should return true when customer is on outbound flight")
        void testIsOnOutboundFlight() throws FlightBookingSystemException {
            customer.addBooking(booking1);
            assertTrue(customer.isAlreadyOnFlight(flight1.getId()));
        }

        @Test
        @DisplayName("Should return true when customer is on return flight")
        void testIsOnReturnFlight() throws FlightBookingSystemException {
            Booking roundTrip = new Booking(3, customer, flight1, returnFlight1, today, 300.00);
            customer.addBooking(roundTrip);
            
            assertTrue(customer.isAlreadyOnFlight(returnFlight1.getId()));
        }

        @Test
        @DisplayName("Should return false when customer is not on flight")
        void testIsNotOnFlight() throws FlightBookingSystemException {
            customer.addBooking(booking1);
            assertFalse(customer.isAlreadyOnFlight(flight2.getId()));
        }

        @Test
        @DisplayName("Should return false for cancelled booking")
        void testCancelledBookingNotCounted() throws FlightBookingSystemException {
            customer.addBooking(booking1);
            booking1.setStatus(BookingStatus.CANCELLED);
            
            assertFalse(customer.isAlreadyOnFlight(flight1.getId()));
        }

        @Test
        @DisplayName("Should return false for completed booking")
        void testCompletedBookingNotCounted() throws FlightBookingSystemException {
            customer.addBooking(booking1);
            booking1.setStatus(BookingStatus.COMPLETED);
            
            assertFalse(customer.isAlreadyOnFlight(flight1.getId()));
        }

        @Test
        @DisplayName("Should only count ACTIVE bookings")
        void testOnlyActiveBookingsCounted() throws FlightBookingSystemException {
            Booking activeBooking = new Booking(3, customer, flight3, today, 180.00);
            customer.addBooking(booking1);
            customer.addBooking(activeBooking);
            booking1.setStatus(BookingStatus.CANCELLED);
            
            assertFalse(customer.isAlreadyOnFlight(flight1.getId()));
            assertTrue(customer.isAlreadyOnFlight(flight3.getId()));
        }
    }

    @Nested
    @DisplayName("String Representation Tests")
    class StringRepresentationTests {

        @Test
        @DisplayName("toString should return correct format")
        void testToString() {
            String expected = "Customer #1 - John Doe";
            assertEquals(expected, customer.toString());
        }

        @Test
        @DisplayName("getDetailsLong should return complete customer information")
        void testGetDetailsLong() {
            String details = customer.getDetailsLong();
            assertTrue(details.contains("Customer #1"));
            assertTrue(details.contains("Name: John Doe"));
            assertTrue(details.contains("Phone: 1234567890"));
            assertTrue(details.contains("Email: john@email.com"));
            assertTrue(details.contains("Deleted: false"));
            assertTrue(details.contains("Bookings: 0"));
        }

        @Test
        @DisplayName("getDetailsLong should show correct booking count")
        void testGetDetailsLongWithBookings() throws FlightBookingSystemException {
            customer.addBooking(booking1);
            customer.addBooking(booking2);
            
            String details = customer.getDetailsLong();
            assertTrue(details.contains("Bookings: 2"));
        }

        @Test
        @DisplayName("getDetailsLong should show deleted status when deleted")
        void testGetDetailsLongWhenDeleted() {
            customer.setDeleted(true);
            String details = customer.getDetailsLong();
            assertTrue(details.contains("Deleted: true"));
        }
    }

    @Nested
    @DisplayName("Edge Cases and Complex Scenarios")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle empty strings in all fields")
        void testEmptyStringFields() {
            Customer c = new Customer(10, "", "", "");
            assertEquals("", c.getName());
            assertEquals("", c.getPhone());
            assertEquals("", c.getEmail());
        }

        @Test
        @DisplayName("Should handle very long field values")
        void testLongFieldValues() {
            String longName = "A".repeat(1000);
            String longPhone = "1".repeat(50);
            String longEmail = "a".repeat(100) + "@email.com";
            
            Customer c = new Customer(11, longName, longPhone, longEmail);
            assertEquals(longName, c.getName());
            assertEquals(longPhone, c.getPhone());
            assertEquals(longEmail, c.getEmail());
        }

        @Test
        @DisplayName("Should handle special characters in name")
        void testSpecialCharactersInName() {
            Customer c = new Customer(12, "José María O'Brien-Smith", "1234567890", "test@email.com");
            assertEquals("José María O'Brien-Smith", c.getName());
        }

        @Test
        @DisplayName("Should handle international phone numbers")
        void testInternationalPhone() {
            Customer c = new Customer(13, "Test User", "+44 20 1234 5678", "test@email.com");
            assertEquals("+44 20 1234 5678", c.getPhone());
        }

        @Test
        @DisplayName("Should handle complex email formats")
        void testComplexEmail() {
            Customer c = new Customer(14, "Test User", "1234567890", "test.name+tag@example.co.uk");
            assertEquals("test.name+tag@example.co.uk", c.getEmail());
        }

        @Test
        @DisplayName("Should handle negative customer ID")
        void testNegativeId() {
            Customer c = new Customer(-1, "Test", "123", "test@email.com");
            assertEquals(-1, c.getId());
        }

        @Test
        @DisplayName("Should handle zero customer ID")
        void testZeroId() {
            Customer c = new Customer(0, "Test", "123", "test@email.com");
            assertEquals(0, c.getId());
        }

        @Test
        @DisplayName("Should handle large customer ID")
        void testLargeId() {
            Customer c = new Customer(999999999, "Test", "123", "test@email.com");
            assertEquals(999999999, c.getId());
        }
    }

    @Nested
    @DisplayName("Complex Booking Scenarios")
    class ComplexBookingScenarios {

        @Test
        @DisplayName("Should handle mixed active and cancelled bookings")
        void testMixedBookingStatuses() throws FlightBookingSystemException {
            Booking active1 = new Booking(1, customer, flight1, today, 150.00);
            Booking cancelled = new Booking(2, customer, flight2, today, 200.00);
            Booking active2 = new Booking(3, customer, flight3, today, 180.00);
            
            customer.addBooking(active1);
            customer.addBooking(cancelled);
            customer.addBooking(active2);
            
            cancelled.setStatus(BookingStatus.CANCELLED);
            
            // Should be able to find all bookings
            assertEquals(3, customer.getBookings().size());
            
            // But isAlreadyOnFlight should only count active ones
            assertTrue(customer.isAlreadyOnFlight(flight1.getId()));
            assertFalse(customer.isAlreadyOnFlight(flight2.getId()));
            assertTrue(customer.isAlreadyOnFlight(flight3.getId()));
        }

        @Test
        @DisplayName("Should allow rebooking same flight after status change")
        void testRebookingAfterStatusChange() throws FlightBookingSystemException {
            Booking booking1 = new Booking(1, customer, flight1, today, 150.00);
            customer.addBooking(booking1);
            
            // Cancel the booking
            booking1.setStatus(BookingStatus.CANCELLED);
            
            // Should now be able to book the same flight again
            Booking booking2 = new Booking(2, customer, flight1, today.plusDays(1), 150.00);
            assertDoesNotThrow(() -> customer.addBooking(booking2));
            
            assertEquals(2, customer.getBookings().size());
        }

        @Test
        @DisplayName("Should handle round trip variations correctly")
        void testRoundTripVariations() throws FlightBookingSystemException {
            Flight return2 = new Flight(105, "BA125", "Paris", "London", today.plusDays(20), 100, 160.00);
            
            // Book round trip with return flight 1
            Booking trip1 = new Booking(1, customer, flight1, returnFlight1, today, 300.00);
            customer.addBooking(trip1);
            
            // Should be able to book same outbound with different return
            Booking trip2 = new Booking(2, customer, flight1, return2, today, 310.00);
            assertDoesNotThrow(() -> customer.addBooking(trip2));
        }

        @Test
        @DisplayName("Should handle null flights in booking gracefully")
        void testNullFlightsInBooking() throws FlightBookingSystemException {
            Booking bookingWithNulls = new Booking(5, customer, null, null, today, 0.0, 0.0, BookingStatus.ACTIVE, today);
            
            // Should not throw exception when adding
            assertDoesNotThrow(() -> customer.addBooking(bookingWithNulls));
            
            // findBookingByFlightId should handle null flights
            assertNull(customer.findBookingByFlightId(101));
        }
    }

    @Nested
    @DisplayName("Immutability Tests")
    class ImmutabilityTests {

        @Test
        @DisplayName("Customer ID should be immutable")
        void testIdImmutable() throws FlightBookingSystemException {
            int originalId = customer.getId();
            customer.setName("New Name");
            customer.addBooking(booking1);
            customer.setDeleted(true);
            assertEquals(originalId, customer.getId());
        }

        @Test
        @DisplayName("Bookings list returned should be unmodifiable")
        void testBookingsListUnmodifiable() throws FlightBookingSystemException {
            customer.addBooking(booking1);
            List<Booking> bookings = customer.getBookings();
            
            assertThrows(UnsupportedOperationException.class, 
                () -> bookings.clear());
            assertThrows(UnsupportedOperationException.class, 
                () -> bookings.remove(0));
            assertThrows(UnsupportedOperationException.class, 
                () -> bookings.add(booking2));
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should handle complete customer lifecycle")
        void testCompleteLifecycle() throws FlightBookingSystemException {
            // Create and modify customer
            Customer c = new Customer(20, "Test User", "1234567890", "test@email.com");
            assertEquals(0, c.getBookings().size());
            
            // Add bookings
            Booking b1 = new Booking(1, c, flight1, today, 150.00);
            Booking b2 = new Booking(2, c, flight2, today, 200.00);
            c.addBooking(b1);
            c.addBooking(b2);
            assertEquals(2, c.getBookings().size());
            
            // Update customer details
            c.setName("Updated Name");
            c.setPhone("9999999999");
            c.setEmail("updated@email.com");
            
            // Cancel one booking
            b1.setStatus(BookingStatus.CANCELLED);
            assertFalse(c.isAlreadyOnFlight(flight1.getId()));
            assertTrue(c.isAlreadyOnFlight(flight2.getId()));
            
            // Remove booking
            c.removeBooking(b1);
            assertEquals(1, c.getBookings().size());
            
            // Mark as deleted
            c.setDeleted(true);
            assertTrue(c.isDeleted());
            
            // Verify final state
            assertEquals("Updated Name", c.getName());
            assertEquals(1, c.getBookings().size());
        }

        @Test
        @DisplayName("Should maintain data integrity across operations")
        void testDataIntegrity() throws FlightBookingSystemException {
            assertEquals(1, customer.getId());
            assertEquals("John Doe", customer.getName());
            
            // Add bookings
            customer.addBooking(booking1);
            customer.addBooking(booking2);
            
            // Update details
            customer.setName("Jane Doe");
            customer.setPhone("5555555555");
            
            // Verify bookings unchanged
            assertEquals(2, customer.getBookings().size());
            
            // Verify ID unchanged
            assertEquals(1, customer.getId());
            
            // Verify updates applied
            assertEquals("Jane Doe", customer.getName());
            assertEquals("5555555555", customer.getPhone());
            assertEquals("john@email.com", customer.getEmail()); // Unchanged
        }
    }
}
