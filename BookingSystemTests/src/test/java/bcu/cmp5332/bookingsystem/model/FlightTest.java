package bcu.cmp5332.bookingsystem.model;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for the Flight class.
 * Tests all methods, edge cases, and business logic.
 * These tests are completely isolated and do NOT modify any data files.
 */
@DisplayName("Flight Class Tests")
class FlightTest {

    private Flight flight;
    private Customer customer1;
    private Customer customer2;
    private Customer customer3;
    private LocalDate departureDate;

    @BeforeEach
    void setUp() {
        departureDate = LocalDate.of(2026, 6, 15);
        flight = new Flight(1, "BA123", "London", "Paris", departureDate, 2, 150.00);
        customer1 = new Customer(101, "John Doe", "1234567890", "john@email.com");
        customer2 = new Customer(102, "Jane Smith", "0987654321", "jane@email.com");
        customer3 = new Customer(103, "Bob Wilson", "5551234567", "bob@email.com");
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create flight with valid parameters")
        void testValidConstruction() {
            assertNotNull(flight);
            assertEquals(1, flight.getId());
            assertEquals("BA123", flight.getFlightNumber());
            assertEquals("London", flight.getOrigin());
            assertEquals("Paris", flight.getDestination());
            assertEquals(departureDate, flight.getDepartureDate());
            assertEquals(2, flight.getCapacity());
            assertEquals(150.00, flight.getBasePrice(), 0.001);
        }

        @Test
        @DisplayName("Should initialize with deleted as false by default")
        void testDefaultDeletedState() {
            assertFalse(flight.isDeleted());
        }

        @Test
        @DisplayName("Should create flight with zero capacity")
        void testZeroCapacity() {
            Flight zeroCapFlight = new Flight(2, "BA456", "NYC", "LA", departureDate, 0, 200.00);
            assertEquals(0, zeroCapFlight.getCapacity());
            assertTrue(zeroCapFlight.isFull());
        }

        @Test
        @DisplayName("Should create flight with large capacity")
        void testLargeCapacity() {
            Flight largeFlight = new Flight(3, "BA789", "Tokyo", "Sydney", departureDate, 500, 800.00);
            assertEquals(500, largeFlight.getCapacity());
            assertEquals(500, largeFlight.getSeatsLeft());
        }

        @Test
        @DisplayName("Should create flight with zero base price")
        void testZeroPrice() {
            Flight freeFlight = new Flight(4, "FR001", "Berlin", "Rome", departureDate, 100, 0.00);
            assertEquals(0.00, freeFlight.getBasePrice(), 0.001);
        }

        @Test
        @DisplayName("Should create flight with high base price")
        void testHighPrice() {
            Flight expensiveFlight = new Flight(5, "LX999", "Geneva", "Dubai", departureDate, 50, 5000.50);
            assertEquals(5000.50, expensiveFlight.getBasePrice(), 0.001);
        }
    }

    @Nested
    @DisplayName("Getter Tests")
    class GetterTests {

        @Test
        @DisplayName("getId should return correct flight ID")
        void testGetId() {
            assertEquals(1, flight.getId());
        }

        @Test
        @DisplayName("getFlightNumber should return correct flight number")
        void testGetFlightNumber() {
            assertEquals("BA123", flight.getFlightNumber());
        }

        @Test
        @DisplayName("getOrigin should return correct origin")
        void testGetOrigin() {
            assertEquals("London", flight.getOrigin());
        }

        @Test
        @DisplayName("getDestination should return correct destination")
        void testGetDestination() {
            assertEquals("Paris", flight.getDestination());
        }

        @Test
        @DisplayName("getDepartureDate should return correct date")
        void testGetDepartureDate() {
            assertEquals(departureDate, flight.getDepartureDate());
        }

        @Test
        @DisplayName("getCapacity should return correct capacity")
        void testGetCapacity() {
            assertEquals(2, flight.getCapacity());
        }

        @Test
        @DisplayName("getBasePrice should return correct price")
        void testGetBasePrice() {
            assertEquals(150.00, flight.getBasePrice(), 0.001);
        }

        @Test
        @DisplayName("getPassengers should return empty set initially")
        void testGetPassengersInitial() {
            Set<Customer> passengers = flight.getPassengers();
            assertNotNull(passengers);
            assertTrue(passengers.isEmpty());
        }

        @Test
        @DisplayName("getPassengers should return set with added passengers")
        void testGetPassengersAfterAdd() throws FlightBookingSystemException {
            flight.addPassenger(customer1);
            Set<Customer> passengers = flight.getPassengers();
            assertEquals(1, passengers.size());
            assertTrue(passengers.contains(customer1));
        }
    }

    @Nested
    @DisplayName("Deleted State Tests")
    class DeletedStateTests {

        @Test
        @DisplayName("Should set deleted state to true")
        void testSetDeletedTrue() {
            flight.setDeleted(true);
            assertTrue(flight.isDeleted());
        }

        @Test
        @DisplayName("Should set deleted state to false")
        void testSetDeletedFalse() {
            flight.setDeleted(true);
            flight.setDeleted(false);
            assertFalse(flight.isDeleted());
        }

        @Test
        @DisplayName("Should toggle deleted state multiple times")
        void testToggleDeleted() {
            assertFalse(flight.isDeleted());
            flight.setDeleted(true);
            assertTrue(flight.isDeleted());
            flight.setDeleted(false);
            assertFalse(flight.isDeleted());
            flight.setDeleted(true);
            assertTrue(flight.isDeleted());
        }
    }

    @Nested
    @DisplayName("Seats Availability Tests")
    class SeatsAvailabilityTests {

        @Test
        @DisplayName("getSeatsLeft should return full capacity initially")
        void testSeatsLeftInitial() {
            assertEquals(2, flight.getSeatsLeft());
        }

        @Test
        @DisplayName("getSeatsLeft should decrease when passenger added")
        void testSeatsLeftAfterAddingPassenger() throws FlightBookingSystemException {
            flight.addPassenger(customer1);
            assertEquals(1, flight.getSeatsLeft());
        }

        @Test
        @DisplayName("getSeatsLeft should be zero when flight is full")
        void testSeatsLeftWhenFull() throws FlightBookingSystemException {
            flight.addPassenger(customer1);
            flight.addPassenger(customer2);
            assertEquals(0, flight.getSeatsLeft());
        }

        @Test
        @DisplayName("getSeatsLeft should increase when passenger removed")
        void testSeatsLeftAfterRemovingPassenger() throws FlightBookingSystemException {
            flight.addPassenger(customer1);
            flight.addPassenger(customer2);
            flight.removePassenger(customer1);
            assertEquals(1, flight.getSeatsLeft());
        }

        @Test
        @DisplayName("isFull should return false when seats available")
        void testIsFullWhenSeatsAvailable() {
            assertFalse(flight.isFull());
        }

        @Test
        @DisplayName("isFull should return true when all seats taken")
        void testIsFullWhenAllSeatsTaken() throws FlightBookingSystemException {
            flight.addPassenger(customer1);
            flight.addPassenger(customer2);
            assertTrue(flight.isFull());
        }

        @Test
        @DisplayName("isFull should return false after removing passenger")
        void testIsFullAfterRemoval() throws FlightBookingSystemException {
            flight.addPassenger(customer1);
            flight.addPassenger(customer2);
            assertTrue(flight.isFull());
            flight.removePassenger(customer1);
            assertFalse(flight.isFull());
        }
    }

    @Nested
    @DisplayName("Add Passenger Tests")
    class AddPassengerTests {

        @Test
        @DisplayName("Should successfully add passenger to empty flight")
        void testAddPassengerToEmptyFlight() throws FlightBookingSystemException {
            flight.addPassenger(customer1);
            assertEquals(1, flight.getPassengers().size());
            assertTrue(flight.getPassengers().contains(customer1));
        }

        @Test
        @DisplayName("Should successfully add multiple passengers")
        void testAddMultiplePassengers() throws FlightBookingSystemException {
            flight.addPassenger(customer1);
            flight.addPassenger(customer2);
            assertEquals(2, flight.getPassengers().size());
            assertTrue(flight.getPassengers().contains(customer1));
            assertTrue(flight.getPassengers().contains(customer2));
        }

        @Test
        @DisplayName("Should throw exception when adding passenger to full flight")
        void testAddPassengerToFullFlight() throws FlightBookingSystemException {
            flight.addPassenger(customer1);
            flight.addPassenger(customer2);
            
            FlightBookingSystemException exception = assertThrows(
                FlightBookingSystemException.class,
                () -> flight.addPassenger(customer3)
            );
            assertEquals("Flight is full.", exception.getMessage());
        }

        @Test
        @DisplayName("Should not add duplicate passenger (HashSet behavior)")
        void testAddDuplicatePassenger() throws FlightBookingSystemException {
            flight.addPassenger(customer1);
            flight.addPassenger(customer1); // Adding same passenger again
            assertEquals(1, flight.getPassengers().size());
        }

        @Test
        @DisplayName("Should handle adding passenger after removing one")
        void testAddAfterRemove() throws FlightBookingSystemException {
            flight.addPassenger(customer1);
            flight.addPassenger(customer2);
            flight.removePassenger(customer1);
            flight.addPassenger(customer3);
            
            assertEquals(2, flight.getPassengers().size());
            assertTrue(flight.getPassengers().contains(customer2));
            assertTrue(flight.getPassengers().contains(customer3));
            assertFalse(flight.getPassengers().contains(customer1));
        }
    }

    @Nested
    @DisplayName("Remove Passenger Tests")
    class RemovePassengerTests {

        @Test
        @DisplayName("Should successfully remove existing passenger")
        void testRemoveExistingPassenger() throws FlightBookingSystemException {
            flight.addPassenger(customer1);
            flight.addPassenger(customer2);
            flight.removePassenger(customer1);
            
            assertEquals(1, flight.getPassengers().size());
            assertFalse(flight.getPassengers().contains(customer1));
            assertTrue(flight.getPassengers().contains(customer2));
        }

        @Test
        @DisplayName("Should handle removing non-existent passenger gracefully")
        void testRemoveNonExistentPassenger() {
            // Should not throw exception, just do nothing
            assertDoesNotThrow(() -> flight.removePassenger(customer1));
            assertEquals(0, flight.getPassengers().size());
        }

        @Test
        @DisplayName("Should handle removing from empty flight")
        void testRemoveFromEmptyFlight() {
            assertDoesNotThrow(() -> flight.removePassenger(customer1));
            assertEquals(0, flight.getPassengers().size());
        }

        @Test
        @DisplayName("Should remove all passengers successfully")
        void testRemoveAllPassengers() throws FlightBookingSystemException {
            flight.addPassenger(customer1);
            flight.addPassenger(customer2);
            flight.removePassenger(customer1);
            flight.removePassenger(customer2);
            
            assertEquals(0, flight.getPassengers().size());
            assertEquals(2, flight.getSeatsLeft());
        }

        @Test
        @DisplayName("Should update seats left after removal")
        void testSeatsLeftAfterRemoval() throws FlightBookingSystemException {
            flight.addPassenger(customer1);
            assertEquals(1, flight.getSeatsLeft());
            flight.removePassenger(customer1);
            assertEquals(2, flight.getSeatsLeft());
        }
    }

    @Nested
    @DisplayName("String Representation Tests")
    class StringRepresentationTests {

        @Test
        @DisplayName("toString should return correct format")
        void testToString() {
            String expected = "Flight #1 (BA123) London → Paris | " + departureDate;
            assertEquals(expected, flight.toString());
        }

        @Test
        @DisplayName("getDetailsShort should return correct short details")
        void testGetDetailsShort() {
            String details = flight.getDetailsShort();
            assertTrue(details.contains("Flight #1"));
            assertTrue(details.contains("BA123"));
            assertTrue(details.contains("London"));
            assertTrue(details.contains("Paris"));
            assertTrue(details.contains("15/06/2026"));
        }

        @Test
        @DisplayName("getDetailsLong should return complete flight information")
        void testGetDetailsLong() {
            String details = flight.getDetailsLong();
            assertTrue(details.contains("Flight #1"));
            assertTrue(details.contains("BA123"));
            assertTrue(details.contains("London"));
            assertTrue(details.contains("Paris"));
            assertTrue(details.contains("Capacity: 2"));
            assertTrue(details.contains("Seats left: 2"));
            assertTrue(details.contains("Base price: 150.0"));
            assertTrue(details.contains("Deleted: false"));
        }

        @Test
        @DisplayName("getDetailsLong should reflect current seats left")
        void testGetDetailsLongWithPassengers() throws FlightBookingSystemException {
            flight.addPassenger(customer1);
            String details = flight.getDetailsLong();
            assertTrue(details.contains("Seats left: 1"));
        }

        @Test
        @DisplayName("getDetailsLong should show deleted status when deleted")
        void testGetDetailsLongWhenDeleted() {
            flight.setDeleted(true);
            String details = flight.getDetailsLong();
            assertTrue(details.contains("Deleted: true"));
        }
    }

    @Nested
    @DisplayName("Edge Cases and Boundary Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle flight with capacity of 1")
        void testSingleSeatFlight() throws FlightBookingSystemException {
            Flight singleSeat = new Flight(10, "SS001", "A", "B", departureDate, 1, 100.00);
            assertFalse(singleSeat.isFull());
            singleSeat.addPassenger(customer1);
            assertTrue(singleSeat.isFull());
            assertEquals(0, singleSeat.getSeatsLeft());
        }

        @Test
        @DisplayName("Should handle very long flight number")
        void testLongFlightNumber() {
            String longNumber = "VERYLONGFLIGHTNUMBER123456789";
            Flight f = new Flight(20, longNumber, "Origin", "Dest", departureDate, 100, 200.00);
            assertEquals(longNumber, f.getFlightNumber());
        }

        @Test
        @DisplayName("Should handle empty flight number")
        void testEmptyFlightNumber() {
            Flight f = new Flight(21, "", "Origin", "Dest", departureDate, 100, 200.00);
            assertEquals("", f.getFlightNumber());
        }

        @Test
        @DisplayName("Should handle special characters in locations")
        void testSpecialCharactersInLocations() {
            Flight f = new Flight(22, "FL001", "São Paulo", "Zürich", departureDate, 100, 300.00);
            assertEquals("São Paulo", f.getOrigin());
            assertEquals("Zürich", f.getDestination());
        }

        @Test
        @DisplayName("Should handle same origin and destination")
        void testSameOriginDestination() {
            Flight f = new Flight(23, "FL002", "London", "London", departureDate, 100, 50.00);
            assertEquals("London", f.getOrigin());
            assertEquals("London", f.getDestination());
        }

        @Test
        @DisplayName("Should handle past departure date")
        void testPastDepartureDate() {
            LocalDate pastDate = LocalDate.of(2020, 1, 1);
            Flight pastFlight = new Flight(24, "OLD001", "NYC", "LA", pastDate, 100, 150.00);
            assertEquals(pastDate, pastFlight.getDepartureDate());
        }

        @Test
        @DisplayName("Should handle far future departure date")
        void testFutureDepartureDate() {
            LocalDate futureDate = LocalDate.of(2030, 12, 31);
            Flight futureFlight = new Flight(25, "FUT001", "Tokyo", "Paris", futureDate, 100, 500.00);
            assertEquals(futureDate, futureFlight.getDepartureDate());
        }

        @Test
        @DisplayName("Should handle negative ID")
        void testNegativeId() {
            Flight f = new Flight(-1, "NEG001", "A", "B", departureDate, 100, 100.00);
            assertEquals(-1, f.getId());
        }

        @Test
        @DisplayName("Should handle decimal base price")
        void testDecimalBasePrice() {
            Flight f = new Flight(26, "DEC001", "A", "B", departureDate, 100, 123.456);
            assertEquals(123.456, f.getBasePrice(), 0.0001);
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should handle complete passenger lifecycle")
        void testCompletePassengerLifecycle() throws FlightBookingSystemException {
            // Add passengers
            flight.addPassenger(customer1);
            assertEquals(1, flight.getSeatsLeft());
            assertFalse(flight.isFull());
            
            flight.addPassenger(customer2);
            assertEquals(0, flight.getSeatsLeft());
            assertTrue(flight.isFull());
            
            // Try to add when full
            assertThrows(FlightBookingSystemException.class, 
                () -> flight.addPassenger(customer3));
            
            // Remove and add again
            flight.removePassenger(customer1);
            assertEquals(1, flight.getSeatsLeft());
            assertFalse(flight.isFull());
            
            flight.addPassenger(customer3);
            assertTrue(flight.isFull());
            
            // Verify final state
            assertTrue(flight.getPassengers().contains(customer2));
            assertTrue(flight.getPassengers().contains(customer3));
            assertFalse(flight.getPassengers().contains(customer1));
        }

        @Test
        @DisplayName("Should maintain data integrity across operations")
        void testDataIntegrity() throws FlightBookingSystemException {
            // Initial state
            assertEquals(2, flight.getCapacity());
            assertEquals(2, flight.getSeatsLeft());
            assertFalse(flight.isDeleted());
            
            // Add passengers
            flight.addPassenger(customer1);
            assertEquals(1, flight.getSeatsLeft());
            assertEquals(2, flight.getCapacity()); // Capacity unchanged
            
            // Mark as deleted
            flight.setDeleted(true);
            assertTrue(flight.isDeleted());
            assertEquals(1, flight.getSeatsLeft()); // Seats calculation unchanged
            
            // Passengers still there
            assertTrue(flight.getPassengers().contains(customer1));
            
            // Can still remove passengers even if deleted
            flight.removePassenger(customer1);
            assertEquals(2, flight.getSeatsLeft());
        }

        @Test
        @DisplayName("Should handle multiple add/remove cycles")
        void testMultipleCycles() throws FlightBookingSystemException {
            for (int i = 0; i < 5; i++) {
                flight.addPassenger(customer1);
                flight.addPassenger(customer2);
                assertEquals(0, flight.getSeatsLeft());
                assertTrue(flight.isFull());
                
                flight.removePassenger(customer1);
                flight.removePassenger(customer2);
                assertEquals(2, flight.getSeatsLeft());
                assertFalse(flight.isFull());
            }
        }
    }

    @Nested
    @DisplayName("Immutability Tests")
    class ImmutabilityTests {

        @Test
        @DisplayName("Flight ID should be immutable")
        void testIdImmutable() {
            int originalId = flight.getId();
            flight.setDeleted(true);
            assertEquals(originalId, flight.getId());
        }

        @Test
        @DisplayName("Flight number should be immutable")
        void testFlightNumberImmutable() {
            String originalNumber = flight.getFlightNumber();
            flight.setDeleted(true);
            assertEquals(originalNumber, flight.getFlightNumber());
        }

        @Test
        @DisplayName("Origin should be immutable")
        void testOriginImmutable() throws FlightBookingSystemException {
            String originalOrigin = flight.getOrigin();
            flight.addPassenger(customer1);
            assertEquals(originalOrigin, flight.getOrigin());
        }

        @Test
        @DisplayName("Destination should be immutable")
        void testDestinationImmutable() throws FlightBookingSystemException {
            String originalDest = flight.getDestination();
            flight.addPassenger(customer1);
            assertEquals(originalDest, flight.getDestination());
        }

        @Test
        @DisplayName("Departure date should be immutable")
        void testDepartureDateImmutable() throws FlightBookingSystemException {
            LocalDate originalDate = flight.getDepartureDate();
            flight.addPassenger(customer1);
            assertEquals(originalDate, flight.getDepartureDate());
        }

        @Test
        @DisplayName("Capacity should be immutable")
        void testCapacityImmutable() throws FlightBookingSystemException {
            int originalCapacity = flight.getCapacity();
            flight.addPassenger(customer1);
            flight.removePassenger(customer1);
            assertEquals(originalCapacity, flight.getCapacity());
        }

        @Test
        @DisplayName("Base price should be immutable")
        void testBasePriceImmutable() throws FlightBookingSystemException {
            double originalPrice = flight.getBasePrice();
            flight.addPassenger(customer1);
            assertEquals(originalPrice, flight.getBasePrice(), 0.001);
        }
    }
}
