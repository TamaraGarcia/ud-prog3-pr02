package ud.prog3.pr02;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CocheTest {
	
	private Coche c;

	@Before
	public void setUp() throws Exception {
		c = new Coche();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFuerzaAceleracionAtras() {
		double[] tablaVel = { -300, -250, -200, -100, 0, 125, 250, 500, 1100 };
				double[] tablaFuerza = { 1, 1, 1, 0.65, 0.3, 0.575, 0.85, 0.85,0.85 };
				for (int i=0;i<tablaVel.length;i++) {
				c.setVelocidad( tablaVel[i] );
				assertEquals( "Velocidad " + tablaVel[i], tablaFuerza[i]*Coche.FUERZA_BASE_ATRAS,
				c.fuerzaAceleracionAtras(), 0.0000001 );
				}
	}

}
