package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import domain.Zone;
import domain.FinalZone;

// Tests for the Zone hierarchy, ensuring correct state management and dimension integrity.
class ZoneTest {

    private Zone initialZone;
    private FinalZone finalZone;

    // Initialises Zone instances before each test using a concrete subclass.
    @BeforeEach
    void setUp() {
        initialZone = new FinalZone(10, 10, 50, 50);
        finalZone = new FinalZone(100, 100, 30, 30);
    }

    @Test
    // Verifies that a new zone is not visited by default.
    void shouldInitializeAsNotVisited() {
        assertFalse(initialZone.isVisited());
    }

    @Test
    // Verifies that the visit() method correctly updates the visited status to true.
    void visitShouldChangeStateToTrue() {
        initialZone.visit();
        assertTrue(initialZone.isVisited());
    }

    @Test
    // Verifies that the getters return the correct spatial data assigned in the constructor.
    void shouldReturnCorrectDimensions() {
        assertEquals(10.0, initialZone.getX(), 0.001);
        assertEquals(10.0, initialZone.getY(), 0.001);
        assertEquals(50.0, initialZone.getWidth(), 0.001);
        assertEquals(50.0, initialZone.getHeight(), 0.001);
    }

    @Test
    // Verifies that FinalZone correctly inherits and maintains Zone behavior.
    void finalZoneShouldInheritZoneBehavior() {
        finalZone.visit();
        assertTrue(finalZone.isVisited());
    }
}