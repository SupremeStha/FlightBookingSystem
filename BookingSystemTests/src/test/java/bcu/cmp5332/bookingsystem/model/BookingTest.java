package bcu.cmp5332.bookingsystem.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Booking class.
 * These tests are completely isolated and do NOT modify any data files.
 */
@DisplayName("Booking Class Tests")
class BookingTest {

    private Customer customer;
    private Flight outboundFlight;
    private Flight returnFlight;
    private Booking oneWayBooking;
    private Booking roundTripBooking;
    private LocalDate bookingDate;

    @BeforeEach
    void setUp() {
        bookingDate = LocalDate.now();
        customer = new Customer(1, "John Doe", "1234567890", "john@email.com");
        outboundFlight = new Flight(101, "BA123", "London", "Paris", bookingDate.plusDays(10), 100, 150.00);
        returnFlight = new Flight(102, "BA124", "Paris", "London", bookingDate.plusDays(17), 100, 150.00);
        
        oneWayBooking = new Booking(1, customer, outboundFlight, bookingDate, 150.00);
        roundTripBooking = new Booking(2, customer, outboundFlight, returnFlight, bookingDate, 300.00);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create one-way booking correctly")
        void testOneWayBookingConstruction() {
            assertNotNull(oneWayBooking);
            assertEquals(1, oneWayBooking.getId());
            assertEquals(customer, oneWayBooking.getCustomer());
            assertEquals(outboundFlight, oneWayBooking.getOutboundFlight());
            assertNull(oneWayBooking.getReturnFlight());
            assertEquals(bookingDate, oneWayBooking.getBookingDate());
            assertEquals(150.00, oneWayBooking.getPricePaid(), 0.001);
            assertEquals(BookingStatus.ACTIVE, oneWayBooking.getStatus());
        }

        @Test
        @DisplayName("Should create round-trip booking correctly")
        void testRoundTripBookingConstruction() {
            assertNotNull(roundTripBooking);
            assertEquals(2, roundTripBooking.getId());
            assertEquals(customer, roundTripBooking.getCustomer());
            assertEquals(outboundFlight, roundTripBooking.getOutboundFlight());
            assertEquals(returnFlight, roundTripBooking.getReturnFlight());
            assertEquals(bookingDate, roundTripBooking.getBookingDate());
            assertEquals(300.00, roundTripBooking.getPricePaid(), 0.001);
        }

        @Test
        @DisplayName("Should initialize with default fee of 0.0")
        void testDefaultFee() {
            assertEquals(0.0, oneWayBooking.getFeeCharged(), 0.001);
        }

        @Test
        @DisplayName("Should initialize with ACTIVE status")
        void testDefaultStatus() {
            assertEquals(BookingStatus.ACTIVE, oneWayBooking.getStatus());
        }

        @Test
        @DisplayName("Should initialize with default history")
        void testDefaultHistory() {
            List<String> history = oneWayBooking.getHistory();
            assertEquals(1, history.size());
            assertEquals("BOOKED", history.get(0));
        }

        @Test
        @DisplayName("Should set lastUpdated to bookingDate when null")
        void testLastUpdatedDefault() {
            assertEquals(bookingDate, oneWayBooking.getLastUpdated());
        }

        @Test
        @DisplayName("Should create booking with full parameters")
        void testFullParameterConstruction() {
            LocalDate lastUpdated = bookingDate.plusDays(5);
            Booking fullBooking = new Booking(
                3, customer, outboundFlight, returnFlight,
                bookingDate, 300.00, 25.00,
                BookingStatus.CANCELLED, lastUpdated
            );
            
            assertEquals(25.00, fullBooking.getFeeCharged(), 0.001);
            assertEquals(BookingStatus.CANCELLED, fullBooking.getStatus());
            assertEquals(lastUpdated, fullBooking.getLastUpdated());
        }
    }

    @Nested
    @DisplayName("Getter Tests")
    class GetterTests {

        @Test
        @DisplayName("getId should return correct booking ID")
        void testGetId() {
            assertEquals(1, oneWayBooking.getId());
        }

        @Test
        @DisplayName("getCustomer should return correct customer")
        void testGetCustomer() {
            assertEquals(customer, oneWayBooking.getCustomer());
        }

        @Test
        @DisplayName("getOutboundFlight should return correct outbound flight")
        void testGetOutboundFlight() {
            assertEquals(outboundFlight, oneWayBooking.getOutboundFlight());
        }

        @Test
        @DisplayName("getReturnFlight should return null for one-way")
        void testGetReturnFlightOneWay() {
            assertNull(oneWayBooking.getReturnFlight());
        }

        @Test
        @DisplayName("getReturnFlight should return correct return flight for round-trip")
        void testGetReturnFlightRoundTrip() {
            assertEquals(returnFlight, roundTripBooking.getReturnFlight());
        }

        @Test
        @DisplayName("getFlight should return outbound flight (deprecated)")
        void testGetFlightDeprecated() {
            assertEquals(outboundFlight, oneWayBooking.getFlight());
        }

        @Test
        @DisplayName("isRoundTrip should return false for one-way")
        void testIsRoundTripOneWay() {
            assertFalse(oneWayBooking.isRoundTrip());
        }

        @Test
        @DisplayName("isRoundTrip should return true for round-trip")
        void testIsRoundTripRoundTrip() {
            assertTrue(roundTripBooking.isRoundTrip());
        }

        @Test
        @DisplayName("getBookingDate should return correct date")
        void testGetBookingDate() {
            assertEquals(bookingDate, oneWayBooking.getBookingDate());
        }

        @Test
        @DisplayName("getStatus should return correct status")
        void testGetStatus() {
            assertEquals(BookingStatus.ACTIVE, oneWayBooking.getStatus());
        }

        @Test
        @DisplayName("getPricePaid should return correct price")
        void testGetPricePaid() {
            assertEquals(150.00, oneWayBooking.getPricePaid(), 0.001);
        }

        @Test
        @DisplayName("getFeeCharged should return correct fee")
        void testGetFeeCharged() {
            assertEquals(0.0, oneWayBooking.getFeeCharged(), 0.001);
        }

        @Test
        @DisplayName("getLastUpdated should return correct date")
        void testGetLastUpdated() {
            assertEquals(bookingDate, oneWayBooking.getLastUpdated());
        }
    }

    @Nested
    @DisplayName("Setter Tests")
    class SetterTests {

        @Test
        @DisplayName("setOutboundFlight should update outbound flight")
        void testSetOutboundFlight() {
            Flight newFlight = new Flight(103, "BA999", "NYC", "LA", bookingDate.plusDays(5), 100, 500.00);
            oneWayBooking.setOutboundFlight(newFlight);
            assertEquals(newFlight, oneWayBooking.getOutboundFlight());
        }

        @Test
        @DisplayName("setReturnFlight should update return flight")
        void testSetReturnFlight() {
            Flight newReturn = new Flight(104, "BA888", "LA", "NYC", bookingDate.plusDays(12), 100, 500.00);
            oneWayBooking.setReturnFlight(newReturn);
            assertEquals(newReturn, oneWayBooking.getReturnFlight());
            assertTrue(oneWayBooking.isRoundTrip());
        }

        @Test
        @DisplayName("setFlight should update outbound flight (deprecated)")
        void testSetFlightDeprecated() {
            Flight newFlight = new Flight(105, "BA777", "Tokyo", "Sydney", bookingDate.plusDays(3), 100, 800.00);
            oneWayBooking.setFlight(newFlight);
            assertEquals(newFlight, oneWayBooking.getOutboundFlight());
        }

        @Test
        @DisplayName("setStatus should update booking status")
        void testSetStatus() {
            oneWayBooking.setStatus(BookingStatus.CANCELLED);
            assertEquals(BookingStatus.CANCELLED, oneWayBooking.getStatus());
        }

        @Test
        @DisplayName("setFeeCharged should update fee")
        void testSetFeeCharged() {
            oneWayBooking.setFeeCharged(50.00);
            assertEquals(50.00, oneWayBooking.getFeeCharged(), 0.001);
        }

        @Test
        @DisplayName("touch should update lastUpdated date")
        void testTouch() {
            LocalDate newDate = bookingDate.plusDays(10);
            oneWayBooking.touch(newDate);
            assertEquals(newDate, oneWayBooking.getLastUpdated());
        }

        @Test
        @DisplayName("touch should not update when date is null")
        void testTouchWithNull() {
            LocalDate originalDate = oneWayBooking.getLastUpdated();
            oneWayBooking.touch(null);
            assertEquals(originalDate, oneWayBooking.getLastUpdated());
        }
    }

    @Nested
    @DisplayName("History Management Tests")
    class HistoryManagementTests {

        @Test
        @DisplayName("addHistory should add event to history")
        void testAddHistory() {
            oneWayBooking.addHistory("UPDATED");
            List<String> history = oneWayBooking.getHistory();
            assertEquals(2, history.size());
            assertEquals("BOOKED", history.get(0));
            assertEquals("UPDATED", history.get(1));
        }

        @Test
        @DisplayName("addHistory should trim event string")
        void testAddHistoryTrimming() {
            oneWayBooking.addHistory("  CANCELLED  ");
            List<String> history = oneWayBooking.getHistory();
            assertTrue(history.contains("CANCELLED"));
        }

        @Test
        @DisplayName("addHistory should ignore null events")
        void testAddHistoryNull() {
            oneWayBooking.addHistory(null);
            List<String> history = oneWayBooking.getHistory();
            assertEquals(1, history.size());
        }

        @Test
        @DisplayName("addHistory should ignore empty events")
        void testAddHistoryEmpty() {
            oneWayBooking.addHistory("");
            oneWayBooking.addHistory("   ");
            List<String> history = oneWayBooking.getHistory();
            assertEquals(1, history.size());
        }

        @Test
        @DisplayName("getHistory should return unmodifiable list")
        void testGetHistoryUnmodifiable() {
            List<String> history = oneWayBooking.getHistory();
            assertThrows(UnsupportedOperationException.class, 
                () -> history.add("NEW_EVENT"));
        }

        @Test
        @DisplayName("getHistoryString should return joined history")
        void testGetHistoryString() {
            oneWayBooking.addHistory("UPDATED");
            oneWayBooking.addHistory("CANCELLED");
            String historyString = oneWayBooking.getHistoryString();
            assertEquals("BOOKED -> UPDATED -> CANCELLED", historyString);
        }

        @Test
        @DisplayName("replaceHistory should replace all history")
        void testReplaceHistory() {
            List<String> newHistory = List.of("EVENT1", "EVENT2", "EVENT3");
            oneWayBooking.replaceHistory(newHistory);
            
            List<String> history = oneWayBooking.getHistory();
            assertEquals(3, history.size());
            assertEquals("EVENT1", history.get(0));
            assertEquals("EVENT2", history.get(1));
            assertEquals("EVENT3", history.get(2));
        }

        @Test
        @DisplayName("replaceHistory should add default BOOKED when null")
        void testReplaceHistoryNull() {
            oneWayBooking.replaceHistory(null);
            List<String> history = oneWayBooking.getHistory();
            assertEquals(1, history.size());
            assertEquals("BOOKED", history.get(0));
        }

        @Test
        @DisplayName("replaceHistory should add default BOOKED when empty")
        void testReplaceHistoryEmpty() {
            oneWayBooking.replaceHistory(List.of());
            List<String> history = oneWayBooking.getHistory();
            assertEquals(1, history.size());
            assertEquals("BOOKED", history.get(0));
        }
    }

    @Nested
    @DisplayName("String Representation Tests")
    class StringRepresentationTests {

        @Test
        @DisplayName("getDetails should show complete booking info for one-way")
        void testGetDetailsOneWay() {
            String details = oneWayBooking.getDetails();
            assertTrue(details.contains("Booking #1"));
            assertTrue(details.contains("customer #1"));
            assertTrue(details.contains("outbound flight #101"));
            assertTrue(details.contains("(one-way)"));
            assertTrue(details.contains("status: ACTIVE"));
            assertTrue(details.contains("paid: 150.00"));
            assertTrue(details.contains("BOOKED"));
        }

        @Test
        @DisplayName("getDetails should show complete booking info for round-trip")
        void testGetDetailsRoundTrip() {
            String details = roundTripBooking.getDetails();
            assertTrue(details.contains("Booking #2"));
            assertTrue(details.contains("outbound flight #101"));
            assertTrue(details.contains("return flight #102"));
            assertFalse(details.contains("(one-way)"));
        }

        @Test
        @DisplayName("getDetails should format price and fee correctly")
        void testGetDetailsFormatting() {
            oneWayBooking.setFeeCharged(25.50);
            String details = oneWayBooking.getDetails();
            assertTrue(details.contains("paid: 150.00"));
            assertTrue(details.contains("fee: 25.50"));
        }
    }

    @Nested
    @DisplayName("Status Transition Tests")
    class StatusTransitionTests {

        @Test
        @DisplayName("Should transition from ACTIVE to CANCELLED")
        void testActiveToCancelled() {
            assertEquals(BookingStatus.ACTIVE, oneWayBooking.getStatus());
            oneWayBooking.setStatus(BookingStatus.CANCELLED);
            assertEquals(BookingStatus.CANCELLED, oneWayBooking.getStatus());
        }

        @Test
        @DisplayName("Should transition from ACTIVE to COMPLETED")
        void testActiveToCompleted() {
            oneWayBooking.setStatus(BookingStatus.COMPLETED);
            assertEquals(BookingStatus.COMPLETED, oneWayBooking.getStatus());
        }

        @Test
        @DisplayName("Should allow multiple status changes")
        void testMultipleStatusChanges() {
            oneWayBooking.setStatus(BookingStatus.CANCELLED);
            assertEquals(BookingStatus.CANCELLED, oneWayBooking.getStatus());
            
            oneWayBooking.setStatus(BookingStatus.ACTIVE);
            assertEquals(BookingStatus.ACTIVE, oneWayBooking.getStatus());
            
            oneWayBooking.setStatus(BookingStatus.COMPLETED);
            assertEquals(BookingStatus.COMPLETED, oneWayBooking.getStatus());
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle zero price")
        void testZeroPrice() {
            Booking freeBooking = new Booking(10, customer, outboundFlight, bookingDate, 0.00);
            assertEquals(0.00, freeBooking.getPricePaid(), 0.001);
        }

        @Test
        @DisplayName("Should handle negative fee (refund)")
        void testNegativeFee() {
            oneWayBooking.setFeeCharged(-50.00);
            assertEquals(-50.00, oneWayBooking.getFeeCharged(), 0.001);
        }

        @Test
        @DisplayName("Should handle very high price")
        void testHighPrice() {
            Booking expensiveBooking = new Booking(11, customer, outboundFlight, bookingDate, 99999.99);
            assertEquals(99999.99, expensiveBooking.getPricePaid(), 0.001);
        }

        @Test
        @DisplayName("Should handle booking on same day as flight")
        void testSameDayBooking() {
            LocalDate flightDate = bookingDate.plusDays(1);
            Flight sameDayFlight = new Flight(106, "BA111", "A", "B", flightDate, 100, 100.00);
            Booking sameDayBooking = new Booking(12, customer, sameDayFlight, flightDate, 100.00);
            
            assertEquals(flightDate, sameDayBooking.getBookingDate());
        }

        @Test
        @DisplayName("Should handle negative booking ID")
        void testNegativeId() {
            Booking negativeIdBooking = new Booking(-1, customer, outboundFlight, bookingDate, 150.00);
            assertEquals(-1, negativeIdBooking.getId());
        }

        @Test
        @DisplayName("Should handle very long history")
        void testLongHistory() {
            for (int i = 0; i < 100; i++) {
                oneWayBooking.addHistory("EVENT_" + i);
            }
            List<String> history = oneWayBooking.getHistory();
            assertEquals(101, history.size()); // 100 + initial BOOKED
        }
    }

    @Nested
    @DisplayName("Immutability Tests")
    class ImmutabilityTests {

        @Test
        @DisplayName("Booking ID should be immutable")
        void testIdImmutable() {
            int originalId = oneWayBooking.getId();
            oneWayBooking.setStatus(BookingStatus.CANCELLED);
            oneWayBooking.setFeeCharged(100.00);
            assertEquals(originalId, oneWayBooking.getId());
        }

        @Test
        @DisplayName("Customer should be immutable")
        void testCustomerImmutable() {
            Customer originalCustomer = oneWayBooking.getCustomer();
            oneWayBooking.setStatus(BookingStatus.CANCELLED);
            assertEquals(originalCustomer, oneWayBooking.getCustomer());
        }

        @Test
        @DisplayName("Booking date should be immutable")
        void testBookingDateImmutable() {
            LocalDate originalDate = oneWayBooking.getBookingDate();
            oneWayBooking.touch(LocalDate.now().plusDays(5));
            assertEquals(originalDate, oneWayBooking.getBookingDate());
        }

        @Test
        @DisplayName("Price paid should be immutable")
        void testPricePaidImmutable() {
            double originalPrice = oneWayBooking.getPricePaid();
            oneWayBooking.setFeeCharged(50.00);
            oneWayBooking.setStatus(BookingStatus.CANCELLED);
            assertEquals(originalPrice, oneWayBooking.getPricePaid(), 0.001);
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should handle complete booking lifecycle")
        void testCompleteLifecycle() {
            // Create booking
            Booking booking = new Booking(20, customer, outboundFlight, bookingDate, 150.00);
            assertEquals(BookingStatus.ACTIVE, booking.getStatus());
            
            // Add history events
            booking.addHistory("PAYMENT_CONFIRMED");
            booking.addHistory("EMAIL_SENT");
            
            // Update timestamp
            LocalDate updateDate = bookingDate.plusDays(1);
            booking.touch(updateDate);
            assertEquals(updateDate, booking.getLastUpdated());
            
            // Complete the booking
            booking.setStatus(BookingStatus.COMPLETED);
            booking.addHistory("COMPLETED");
            
            // Verify final state
            assertEquals(BookingStatus.COMPLETED, booking.getStatus());
            List<String> history = booking.getHistory();
            assertEquals(4, history.size());
            assertTrue(history.contains("COMPLETED"));
        }

        @Test
        @DisplayName("Should handle cancellation with fee")
        void testCancellationWithFee() {
            oneWayBooking.setStatus(BookingStatus.CANCELLED);
            oneWayBooking.setFeeCharged(25.00);
            oneWayBooking.addHistory("CANCELLED_WITH_FEE");
            
            LocalDate cancelDate = bookingDate.plusDays(2);
            oneWayBooking.touch(cancelDate);
            
            assertEquals(BookingStatus.CANCELLED, oneWayBooking.getStatus());
            assertEquals(25.00, oneWayBooking.getFeeCharged(), 0.001);
            assertEquals(cancelDate, oneWayBooking.getLastUpdated());
        }

        @Test
        @DisplayName("Should convert one-way to round-trip")
        void testConvertToRoundTrip() {
            assertFalse(oneWayBooking.isRoundTrip());
            
            oneWayBooking.setReturnFlight(returnFlight);
            
            assertTrue(oneWayBooking.isRoundTrip());
            assertEquals(returnFlight, oneWayBooking.getReturnFlight());
        }
    }



    @Nested
    @DisplayName("Mutator methods")
    class MutatorTests {

        private Booking oneWay;

        @BeforeEach
        void setup() {
            Customer c = new Customer(1, "Test Customer", "1234567890", "test@example.com");
            Flight f = new Flight(10, "FL001", "AAA", "BBB", LocalDate.now().plusDays(10), 10, 100.0);
            oneWay = new Booking(100, c, f, LocalDate.now(), 120.0);
        }

        @Test
        @DisplayName("touch(date) should update lastUpdated when date is non-null")
        void testTouchUpdatesLastUpdated() {
            LocalDate newDate = LocalDate.now().plusDays(1);
            oneWay.touch(newDate);
            assertEquals(newDate, oneWay.getLastUpdated());
        }

        @Test
        @DisplayName("touch(null) should not change lastUpdated")
        void testTouchNullDoesNotChange() {
            LocalDate before = oneWay.getLastUpdated();
            oneWay.touch(null);
            assertEquals(before, oneWay.getLastUpdated());
        }

        @Test
        @DisplayName("setFeeCharged should update feeCharged")
        void testSetFeeCharged() {
            assertEquals(0.0, oneWay.getFeeCharged(), 0.0001);
            oneWay.setFeeCharged(15.5);
            assertEquals(15.5, oneWay.getFeeCharged(), 0.0001);
        }

        @Test
        @DisplayName("setStatus should update booking status")
        void testSetStatus() {
            assertEquals(BookingStatus.ACTIVE, oneWay.getStatus());
            oneWay.setStatus(BookingStatus.CANCELLED);
            assertEquals(BookingStatus.CANCELLED, oneWay.getStatus());
        }

        @Test
        @DisplayName("Adding a return flight should make booking round-trip")
        void testSetReturnFlightMakesRoundTrip() {
            assertFalse(oneWay.isRoundTrip());
            Flight ret = new Flight(11, "FL002", "BBB", "AAA", LocalDate.now().plusDays(20), 10, 110.0);
            oneWay.setReturnFlight(ret);
            assertTrue(oneWay.isRoundTrip());
            assertNotNull(oneWay.getReturnFlight());
            assertEquals(11, oneWay.getReturnFlight().getId());
        }

        @Test
        @DisplayName("Removing return flight should make booking one-way")
        void testClearReturnFlightMakesOneWay() {
            Flight ret = new Flight(11, "FL002", "BBB", "AAA", LocalDate.now().plusDays(20), 10, 110.0);
            oneWay.setReturnFlight(ret);
            assertTrue(oneWay.isRoundTrip());

            oneWay.setReturnFlight(null);
            assertFalse(oneWay.isRoundTrip());
            assertNull(oneWay.getReturnFlight());
        }
    }

}
