package test;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import domain.SolidWall;

public class SolidWallTest {
	private SolidWall wall;
	@BeforeEach
    public void setUp() {
        wall = new SolidWall(50,50,10,10, "yellow");
    }
	
	@Test
	public void IsBlockingShouldReturnTrue() {
		assertTrue(wall.isBlocking());
	}
	
	

}
