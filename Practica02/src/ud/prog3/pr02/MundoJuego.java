package ud.prog3.pr02;

import java.util.ArrayList;

import javax.swing.JPanel;

/** "Mundo" del juego del coche.
 * Incluye las físicas para el movimiento y los choques de objetos.
 * Representa un espacio 2D en el que se mueven el coche y los objetos de puntuación.
 * @author Andoni Eguíluz Morán
 * Facultad de Ingeniería - Universidad de Deusto
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

	/** Crea un coche nuevo y lo añade al mundo y al panel visual
	 * @param posX	Posición X de pixel del nuevo coche
	 * @param posY	Posición Y de píxel del nuevo coche
	 */
	public void creaCoche( int posX, int posY ) {
		// Crear y añadir el coche a la ventana
		miCoche = new CocheJuego();
		miCoche.setPosicion( posX, posY );
		panel.add( miCoche.getGrafico() );  // Añade al panel visual
		miCoche.getGrafico().repaint();  // Refresca el dibujado del coche
	}
	
	/** Devuelve el coche del mundo
	 * @return	Coche en el mundo. Si no lo hay, devuelve null
	 */
	public CocheJuego getCoche() {
		return miCoche;
	}

	/** Calcula si hay choque en horizontal con los límites del mundo
	 * @param coche	Coche cuyo choque se comprueba con su posición actual
	 * @return	true si hay choque horizontal, false si no lo hay
	 */
	public boolean hayChoqueHorizontal( CocheJuego coche ) {
		return (coche.getPosX() < JLabelCoche.RADIO_ESFERA_COCHE-JLabelCoche.TAMANYO_COCHE/2 
				|| coche.getPosX()>panel.getWidth()-JLabelCoche.TAMANYO_COCHE/2-JLabelCoche.RADIO_ESFERA_COCHE );
	}
	
	/** Calcula si hay choque en vertical con los límites del mundo
	 * @param coche	Coche cuyo choque se comprueba con su posición actual
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
		if (dir < 0) dir = 360+dir;  // Corrección para mantenerlo en [0,360)
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
	
	/** Calcula y devuelve la posición X de un movimiento
	 * @param vel    	Velocidad del movimiento (en píxels por segundo)
	 * @param dir    	Dirección del movimiento en grados (0º = eje OX positivo. Sentido antihorario)
	 * @param tiempo	Tiempo del movimiento (en segundos)
	 * @return
	 */
	public static double calcMovtoX( double vel, double dir, double tiempo ) {
		return vel * Math.cos(dir/180.0*Math.PI) * tiempo;
	}
	
	/** Calcula y devuelve la posición X de un movimiento
	 * @param vel    	Velocidad del movimiento (en píxels por segundo)
	 * @param dir    	Dirección del movimiento en grados (0º = eje OX positivo. Sentido antihorario)
	 * @param tiempo	Tiempo del movimiento (en segundos)
	 * @return
	 */
	public static double calcMovtoY( double vel, double dir, double tiempo ) {
		return vel * -Math.sin(dir/180.0*Math.PI) * tiempo;
		// el negativo es porque en pantalla la Y crece hacia abajo y no hacia arriba
	}
	
	/** Calcula el cambio de velocidad en función de la aceleración
	 * @param vel		Velocidad original
	 * @param acel		Aceleración aplicada (puede ser negativa) en pixels/sg2
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
		// 2ª ley de Newton: F = m*a ---> a = F/m
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
			coche.setVelocidad(0); // Si se está frenando, se para (no anda al revés)
			}
			} else {
			coche.acelera( aceleracion, 0.04 );
			}
	}
	
	/** Sii han pasado más de 1,2 segundos desde la última,
	* crea una estrella nueva en una posición aleatoria y la añade al mundo y al panel visual */
	public void creaEstrella(){
		if (estrellas.size() == 0){
			añadirEstrella();
		}
		else if (System.currentTimeMillis() - ultimaEstrella >= 1200){
			añadirEstrella();
		}
		
	}
	
	public void añadirEstrella(){
		JLabelEstrella miEstrella = new JLabelEstrella();
		int posX = (int) Math.floor(Math.random()*900);
		int posY = (int) Math.floor(Math.random()*750);
		
		miEstrella.setLocation(posX, posY);
		estrellas.add(miEstrella);
		ultimaEstrella = miEstrella.getMilisegundos();
		panel.add(miEstrella);  // Añade al panel visual
		miEstrella.repaint();  // Refresca el dibujado del coche
	}
	
	/** Quita todas las estrellas que lleven en pantalla demasiado tiempo
	* y rota 10 grados las que sigan estando
	* @param maxTiempo Tiempo máximo para que se mantengan las estrellas (msegs)
	* @return Número de estrellas quitadas */
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
				estrellas.get(i).repaint();
			}
				
		}
		return borradas;
	}
	
}
