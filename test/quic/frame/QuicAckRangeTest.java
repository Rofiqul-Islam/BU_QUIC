package quic.frame;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QuicAckRangeTest {
	
	private QuicAckRange quicAckRangeTest = new QuicAckRange(-1, -1);

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testQuicAckRange() {
		// Gap value can not be negetive
		assertTrue(quicAckRangeTest.getGap() >= 0);
		// ACK Range value can not be negetive
		assertTrue(quicAckRangeTest.getAckRange() >= 0);
		
	
	}

}
