package ud.prog3.pr02;

import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

/** "Mundo" del juego del coche.
 * Incluye las f�sicas para el movimiento y los choques de objetos.
 * Representa un espacio 2D en el que se mueven el coche y los objetos de puntuaci�n.
 * @author Andoni Egu�luz Mor�n
 * Facultad de Ingenier�a - Universidad de Deusto
 */
public class MundoJuego {
	private JPanel panel;  // panel visual del juego
	CocheJuego miCoche;    // Coche del juego

	
	//ArrayList con las estrellas que se van creando
	private ArrayList<JLabelEstrella> estrellas = new ArrayList<JLabelEstrella>(); 
	private long ultimaEstrella;
	
	/** Construye un mundo de juego
	 * @param panel	Panel visual del juego
	 */
	public MundoJuego( JPanel panel ) {
		this.panel = panel;
	}

	/** Crea un coche nuevo y lo a�ade al mundo y al panel visual
	 * @param posX	Posici�n X de pixel del nuevo coche
	 * @param posY	Posici�n Y de p�xel del nuevo coche
	 */
	public void creaCoche( int posX, int posY ) {
		// Crear y a�adir el coche a la ventana
		miCoche = new CocheJuego();
		miCoche.setPosicion( posX, posY );
		panel.add( miCoche.getGrafico() );  // A�ade al panel visual
		miCoche.getGrafico().repaint();  // Refresca el dibujado del coche
	}
	
	/** Devuelve el coche del mundo
	 * @return	Coche en el mundo. Si no lo hay, devuelve null
	 */
	public CocheJuego getCoche() {
		return miCoche;
	}

	/** Calcula si hay choque en horizontal con los l�mites del mundo
	 * @param coche	Coche cuyo choque se comprueba con su posici�n actual
	 * @return	true si hay choque horizontal, false si no lo hay
	 */
	public boolean hayChoqueHorizontal( CocheJuego coche ) {
		return (coche.getPosX() < JLabelCoche.RADIO_ESFERA_COCHE-JLabelCoche.TAMANYO_COCHE/2 
				|| coche.getPosX()>panel.getWidth()-JLabelCoche.TAMANYO_COCHE/2-JLabelCoche.RADIO_ESFERA_COCHE );
	}
	
	/** Calcula si hay choque en vertical con los l�mites del mundo
	 * @param coche	Coche cuyo choque se comprueba con su posici�n actual
	 * @return	true si hay choque vertical, false si no lo hay
	 */
	public boolean hayChoqueVertical( CocheJuego coche ) {
		return (coche.getPosY() < JLabelCoche.RADIO_ESFERA_COCHE-JLabelCoche.TAMANYO_COCHE/2 
				|| coche.getPosY()>panel.getHeight()-JLabelCoche.TAMANYO_COCHE/2-JLabelCoche.RADIO_ESFERA_COCHE );
	}

	/** Realiza un rebote en horizontal del objeto de juego indicado
	 * @param coche	Objeto que rebota en horizontal
	 */
	public void rebotaHorizontal( CocheJuego coche ) {
		// System.out.println( "Choca X");
		double dir = coche.getDireccionActual();
		dir = 180-dir;   // Rebote espejo sobre OY (complementario de 180)
		if (dir < 0) dir = 360+dir;  // Correcci�n para mantenerlo en [0,360)
		coche.setDireccionActual( dir );
	}
	
	/** Realiza un rebote en vertical del objeto de juego indicado
	 * @param coche	Objeto que rebota en vertical
	 */
	public void rebotaVertical( CocheJuego coche ) {
		// System.out.println( "Choca Y");
		double dir = miCoche.getDireccionActual();
		dir = 360 - dir;  // Rebote espejo sobre OX (complementario de 360)
		miCoche.setDireccionActual( dir );
	}
	
	/** Calcula y devuelve la posici�n X de un movimiento
	 * @param vel    	Velocidad del movimiento (en p�xels por segundo)
	 * @param dir    	Direcci�n del movimiento en grados (0� = eje OX positivo. Sentido antihorario)
	 * @param tiempo	Tiempo del movimiento (en segundos)
	 * @return
	 */
	public static double calcMovtoX( double vel, double dir, double tiempo ) {
		return vel * Math.cos(dir/180.0*Math.PI) * tiempo;
	}
	
	/** Calcula y devuelve la posici�n X de un movimiento
	 * @param vel    	Velocidad del movimiento (en p�xels por segundo)
	 * @param dir    	Direcci�n del movimiento en grados (0� = eje OX positivo. Sentido antihorario)
	 * @param tiempo	Tiempo del movimiento (en segundos)
	 * @return
	 */
	public static double calcMovtoY( double vel, double dir, double tiempo ) {
		return vel * -Math.sin(dir/180.0*Math.PI) * tiempo;
		// el negativo es porque en pantalla la Y crece hacia abajo y no hacia arriba
	}
	
	/** Calcula el cambio de velocidad en funci�n de la aceleraci�n
	 * @param vel		Velocidad original
	 * @param acel		Aceleraci�n aplicada (puede ser negativa) en pixels/sg2
	 * @param tiempo	Tiempo transcurrido en segundos
	 * @return	Nueva velocidad
	 */
	public static double calcVelocidadConAceleracion( double vel, double acel, double tiempo ) {
		return vel + (acel*tiempo);
	}
	
	public static double calcFuerzaRozamiento( double masa, double coefRozSuelo, double coefRozAire, double vel ) {
			double fuerzaRozamientoAire = coefRozAire * (-vel); // En contra del movimiento
			double fuerzaRozamientoSuelo = masa * coefRozSuelo * ((vel>0)?(-1):1); // Contra mvto
			return fuerzaRozamientoAire + fuerzaRozamientoSuelo;
	}
	
	public static double calcAceleracionConFuerza( double fuerza, double masa ) {
		// 2� ley de Newton: F = m*a ---> a = F/m
		return fuerza/masa;
	}
	
	public static void aplicarFuerza( double fuerza, Coche coche ) {
		double fuerzaRozamiento = calcFuerzaRozamiento( Coche.MASA ,
		Coche.COEF_RZTO_SUELO, Coche.COEF_RZTO_AIRE, coche.getVelocidad() );
		double aceleracion = calcAceleracionConFuerza( fuerza+fuerzaRozamiento, Coche.MASA );
		if (fuerza==0) {
			// No hay fuerza, solo se aplica el rozamiento
			double velAntigua = coche.getVelocidad();
			coche.acelera( aceleracion, 0.04 );
			if (velAntigua>=0 && coche.getVelocidad()<0
			|| velAntigua<=0 && coche.getVelocidad()>0) {
			coche.setVelocidad(0); // Si se est� frenando, se para (no anda al rev�s)
			}
			} else {
			coche.acelera( aceleracion, 0.04 );
			}
	}
	
	/** Sii han pasado m�s de 1,2 segundos desde la �ltima,
	* crea una estrella nueva en una posici�n aleatoria y la a�ade al mundo y al panel visual */
	public void creaEstrella(){
		if (estrellas.size() == 0){
			a�adirEstrella();
		}
		else if (System.currentTimeMillis() - ultimaEstrella >= 1200){
			a�adirEstrella();
		}
		
	}
	
	public void a�adirEstrella(){
		JLabelEstrella miEstrella = new JLabelEstrella();
		int posX = (int) Math.floor(Math.random()*900);
		int posY = (int) Math.floor(Math.random()*750);
		
		miEstrella.setLocation(posX, posY);
		estrellas.add(miEstrella);
		ultimaEstrella = miEstrella.getMilisegundos();
		panel.add(miEstrella);  // A�ade al panel visual
		miEstrella.repaint();  // Refresca el dibujado del coche
	}
	
	/** Quita todas las estrellas que lleven en pantalla demasiado tiempo
	* y rota 10 grados las que sigan estando
	* @param maxTiempo Tiempo m�ximo para que se mantengan las estrellas (msegs)
	* @return N�mero de estrellas quitadas */
	public int quitaYRotaEstrellas ( long maxTiempo ){
		int borradas = 0;
		for(int i=0; i<estrellas.size(); i++){
			if (System.currentTimeMillis() - estrellas.get(i).getMilisegundos() >= maxTiempo ){
				panel.remove(estrellas.get(i));
				estrellas.remove(estrellas.get(i));
				panel.updateUI();
				borradas++;
			}else{
				estrellas.get(i).setGiro(10);
				Graphics g = estrellas.get(i).getGraphics();
				estrellas.get(i).paintComponent(g);
				estrellas.get(i).repaint();
			}
				
		}
		return borradas;
	}
	
	/** Calcula si hay choques del coche con alguna estrella (o varias). Se considera el choque si
	* se tocan las esferas l�gicas del coche y la estrella. Si es as�, las elimina.
	* @return N�mero de estrellas eliminadas
	*/
	public int choquesConEstrellas(){
		int numEstrellasEliminadas = 0;
		
		for (int i=0; i<estrellas.size(); i++){
			
			double distCentros = Math.sqrt( Math.pow(estrellas.get(i).getX() - miCoche.getPosX(), 2) +
									Math.pow(estrellas.get(i).getY() - miCoche.getPosY(), 2));
			
			if (distCentros < (JLabelEstrella.RADIO_ESFERA_ESTRELLA + JLabelCoche.RADIO_ESFERA_COCHE)){
				//Hay choque
				panel.remove(estrellas.get(i));
				estrellas.remove(estrellas.get(i));
				panel.repaint();
				numEstrellasEliminadas++;
			}
		}
		
		return numEstrellasEliminadas;
	}
	
}
