import java.io.File;

import javax.swing.JTextArea;

import org.joone.engine.DirectSynapse;
import org.joone.engine.FullSynapse;
import org.joone.engine.LinearLayer;
import org.joone.engine.Monitor;
import org.joone.engine.NeuralNetEvent;
import org.joone.engine.NeuralNetListener;
import org.joone.engine.Pattern;
import org.joone.engine.SigmoidLayer;
import org.joone.engine.learning.TeachingSynapse;
import org.joone.io.MemoryInputSynapse;
import org.joone.net.NeuralNet;

/**
 * Reconocimiento de triangulos, cuadrados y circulos regulares
 * basado en ejemplo XORMemory.java e ImmediateEmbeddedXOR.java
 * ambos programas se pueden encontrar en el joone-engine
 * en el directorio samples/engine/xor
 */
public class GeometricFigureRecognizer implements NeuralNetListener {
		
	private long mills;
	private NeuralNet red;
	private LinearLayer	entrada;
	private SigmoidLayer	oculta;
	private SigmoidLayer	salida;
	private Monitor monitor = new Monitor();
	private int cantidad_ArchivosImagenes=0;
	private ProcesadorDeImagenes procesadorImagenes;
	private boolean LOGGER = false;
	
	public GeometricFigureRecognizer()
	{	
		this.procesadorImagenes = new ProcesadorDeImagenes();
		
	}
	
	public void inicializarReconocimiento(JTextArea jep) {
						
		// vectorEntrada para el caso q tengo 15 archivos para entrenar
		double vectorEntrada[][]={	{0.0 ,0.0,0.0,0.0,0.0,0.0},{0.0 ,0.0,0.0,0.0,0.0,0.0},{0.0 ,0.0,0.0,0.0,0.0,0.0},
									{0.0 ,0.0,0.0,0.0,0.0,0.0},{0.0 ,0.0,0.0,0.0,0.0,0.0},{0.0 ,0.0,0.0,0.0,0.0,0.0},
									{0.0 ,0.0,0.0,0.0,0.0,0.0},{0.0 ,0.0,0.0,0.0,0.0,0.0},{0.0 ,0.0,0.0,0.0,0.0,0.0},
									{0.0 ,0.0,0.0,0.0,0.0,0.0},{0.0 ,0.0,0.0,0.0,0.0,0.0},{0.0 ,0.0,0.0,0.0,0.0,0.0},
									{0.0 ,0.0,0.0,0.0,0.0,0.0},{0.0 ,0.0,0.0,0.0,0.0,0.0},{0.0 ,0.0,0.0,0.0,0.0,0.0},
									{0.0 ,0.0,0.0,0.0,0.0,0.0},{0.0 ,0.0,0.0,0.0,0.0,0.0},{0.0 ,0.0,0.0,0.0,0.0,0.0},
									{0.0 ,0.0,0.0,0.0,0.0,0.0},{0.0 ,0.0,0.0,0.0,0.0,0.0},{0.0 ,0.0,0.0,0.0,0.0,0.0}
								};
		
		String path = "Figuras/Entrenamiento/";
		File directorio = new File(path);
		String [] ficheros = directorio.list();
		int index=0;
		
		// Armo  la red
		this.iniciar();
					
		if(LOGGER) System.out.println("***************** Entrenamiento *****************");
		jep.append("***************** Entrenamiento *****************\n");
		// Recorro los archivos que hay en la carpeta Figuras, todos los que
		// sean ".jpg"
		for (int s = 0; s < ficheros.length; s++) 
		{
			if (ficheros[s].contains(".jpg"))
			{
				cantidad_ArchivosImagenes++;
				if(LOGGER) System.out.println("Procesando archivo: " +ficheros[s]);
				jep.append("Procesando archivo: " +ficheros[s]+"\n");
				//System.out.print(ficheros[s] + ";");
				//  cargo la imagen desde el archivo
				procesadorImagenes.cargarImagen(path+ficheros[s]);
				// paso a blanco y negro la imagen 
				procesadorImagenes.procesarImagen();
				//delimito la imagen
				procesadorImagenes.recortarImagen();
				// obtengo los porcentajes segun en las 3 zonas en que se divide 
				double porcentajes[]= procesadorImagenes.getPorcentajes();
				for (int i = 0; i < porcentajes.length; i++) 
				{
					vectorEntrada[index][i]=porcentajes[i];					
					if(LOGGER) System.out.println("proporcion["+i+"]="+porcentajes[i]);
					jep.append("proporcion["+i+"]="+porcentajes[i]+"\n");
				}	
							
				//Según la figura que sea, asigno 1.0. El orden es triangulo-circulo-cuadrado
				vectorEntrada[index][3]=ficheros[s].toLowerCase().contains("triangulo")?1.0:0.0;
				vectorEntrada[index][4]=ficheros[s].toLowerCase().contains("circulo")?1.0:0.0;
				vectorEntrada[index][5]=ficheros[s].toLowerCase().contains("cuadrado")?1.0:0.0;
				//indice para controlar el llenado del vector de Entrada
				index++;
			}
		}

		jep.append("Entrenando.........\n");
		this.entrenar(vectorEntrada);		
	}
	
	/**
	 * Method declaration
	 */
	private void iniciar() {
		// El objeto net encapsula toda la red
		red = new NeuralNet();
	
		// Creamos las tres capas
		entrada = new LinearLayer();
		oculta = new SigmoidLayer();
		salida = new SigmoidLayer();
		
		// Les asignamos nombres
		entrada.setLayerName("Entrada");
		oculta.setLayerName("Oculta");
		salida.setLayerName("Salida");
		
		// Asignamos las cantidad de neuronas en cada capa
		entrada.setRows(3);
		oculta.setRows(3);
		salida.setRows(3);
		
		//adiciona las capas a la red
		red.addLayer(entrada);
		red.addLayer(oculta);
		red.addLayer(salida);
		
		// Now create the two Synapses
		FullSynapse conexionEntradaOculta = new FullSynapse();	/* input -> hidden conn. */
		FullSynapse conexionOcultaSalida = new FullSynapse();	/* hidden -> output conn. */
		
		conexionEntradaOculta.setName("EO");
		conexionOcultaSalida.setName("OS");
		
		// Conectamos la capa de entrada con la de salida
		entrada.addOutputSynapse(conexionEntradaOculta);
		oculta.addInputSynapse(conexionEntradaOculta);
		
		// Conectamos la capa oculta con la de salida
		oculta.addOutputSynapse(conexionOcultaSalida);
		salida.addInputSynapse(conexionOcultaSalida);
	}
	
	private void entrenar(double[][] vectorEntrada){
		// Create the Monitor object and set the learning parameters
		monitor = new Monitor();
		
		monitor.setLearningRate(0.1);
		monitor.setMomentum(0.9);
		
		// Passe the Monitor to all components
		red.setMonitor(monitor);
		// The application registers itself as monitor's listener so it can receive
		// the notifications of termination from the net.
		monitor.addNeuralNetListener(this);
		
		MemoryInputSynapse  inputStream = new MemoryInputSynapse();
		
		//Indicamos que columnas son las entradas
		inputStream.setInputArray(vectorEntrada);
		inputStream.setAdvancedColumnSelector("1,2,3");
		
		red.addInputSynapse(inputStream);
		TeachingSynapse supervisor = new TeachingSynapse();
		
		supervisor.setMonitor(monitor);
		
		// Setting of the file containing the desired responses provided by a FileInputSynapse
		MemoryInputSynapse samples = new MemoryInputSynapse();
		
		
		//Indicamos cual de las columnas es la salida
		samples.setInputArray(vectorEntrada);
		samples.setAdvancedColumnSelector("4,5,6");
		supervisor.setDesired(samples);
		
		// Connects the Teacher to the last layer of the net
		red.addOutputSynapse(supervisor);
		/*
		 * All the layers must be activated invoking their method start;
		 * the layers are implemented as Runnable objects, then they are
		 * instanziated on separated threads.
		 */
		red.start();
		monitor.setTrainingPatterns(cantidad_ArchivosImagenes);	// # of rows (patterns) contained in the input file
		monitor.setTotCicles(10000);		// How many times the net must be trained on the input patterns
		monitor.setLearning(true);		// The net must be trained
		mills = System.currentTimeMillis();
		monitor.Go();					// The net starts the training job
	}
	
	public void reconocer(String pathFigura, JTextArea jep){
		
		double[][] vectorPrueba = new double[][] { {0.0, 0.0, 0.0} };
		procesadorImagenes.cargarImagen(pathFigura);
		procesadorImagenes.procesarImagen();
		procesadorImagenes.recortarImagen();
		double vectorProporcion[] = procesadorImagenes.getPorcentajes();
		for(int i = 0;i<vectorProporcion.length;i++)
		{
			vectorPrueba[0][i] = vectorProporcion[i];
		} 
				
		// Joone utiliza hilos para ejecutar el entrenamiento,
		// por lo tanto debemos esperar a que el entrenamiento termine
		// antes de probar la red. El metodo join() espera a que los hilos de la 
		// red terminen.
		red.join();
		
		/* We get the first layer of the net (the input layer),
        then remove all the input synapses attached to it
        and attach a DirectSynapse */
		entrada.removeAllInputs();
		DirectSynapse memInp = new DirectSynapse();
		entrada.addInputSynapse(memInp);
		/*
		 * We get the last layer of the net (the output layer), then remove all
		 * the output synapses attached to it and attach a DirectSynapse
		 */
		salida.removeAllOutputs();
		DirectSynapse memOut = new DirectSynapse();
		salida.addOutputSynapse(memOut);
		// Now we interrogate the net
		monitor.setLearning(false);
		red.start();
		//for (int i=0; i < cant_de_img; ++i)
		if(LOGGER) System.out.println("***************** Reconocimiento *****************");
		jep.setText("***************** Reconocimiento *****************\n");
		for (int i=0; i < 1; ++i)
		{
			// Prepare the next input pattern
			if(LOGGER) System.out.println("Input: "+vectorPrueba[i][0]+"  "+vectorPrueba[i][1]+"  "+vectorPrueba[i][2]);
			jep.append("Input: "+vectorPrueba[i][0]+"  "+vectorPrueba[i][1]+"  "+vectorPrueba[i][2]+"\n");
			Pattern iPattern = new Pattern(vectorPrueba[i]);
			iPattern.setCount(i+1);
			// Interrogate the net
			memInp.fwdPut(iPattern);
			// Read the output pattern and print out it
			// double[] pattern = memOut.getNextPattern();
			Pattern pattern = memOut.fwdGet();
			String salida;
			
			if(LOGGER)
			{
				System.out.println("********* Nivel de reconocimiento *********");
				System.out.println("Triangulo:" + pattern.getArray()[0]);
				System.out.println("Circulo:" + pattern.getArray()[1]);
				System.out.println("Rectangulo:" + pattern.getArray()[2]);
			}
			jep.append("********* Nivel de reconocimiento *********\n");
			jep.append("Triangulo:" + pattern.getArray()[0]+"\n");
			jep.append("Circulo:" + pattern.getArray()[1]+"\n");
			jep.append("Rectangulo:" + pattern.getArray()[2]+"\n");
			
							
			if((pattern.getArray()[0]>0.8)&&(pattern.getArray()[1]<0.1)&&(pattern.getArray()[2]<0.1))
				salida="Es un TRIANGULO";
			else if((pattern.getArray()[0]<0.1)&&(pattern.getArray()[1]>0.8)&&(pattern.getArray()[2]<0.1))
				salida="Es un CIRCULO";
			else if((pattern.getArray()[0]<0.1)&&(pattern.getArray()[1]<0.1)&&(pattern.getArray()[2]>0.8))
				salida="Es un RECTANGULO";
			else
				salida="La figura no fue reconocida";
			
			if(LOGGER) System.out.println(salida);
			jep.append(salida+"\n");
		}
		//Tell the network to stop
		Pattern stop = new Pattern(new double[3]);
		stop.setCount(-1);
		memInp.fwdPut(stop);
		memOut.fwdGet();
	}
	


/**
 * Method declaration
 */
public  void netStopped(NeuralNetEvent e) {
	long delay = System.currentTimeMillis() - mills;
	System.out.println("Training finished after "+delay+" ms");
}

/**
 * Method declaration
 */
public void cicleTerminated(NeuralNetEvent e) {
}

/**
 * Method declaration
 */
public void netStarted(NeuralNetEvent e) {
	System.out.println("Training...");
}

public void errorChanged(NeuralNetEvent e) {
	Monitor mon = (Monitor) e.getSource();
	long	c = mon.getCurrentCicle();
	long	cl = c / 1000;
	
	// We want to print the results every 1000 cycles
	if ((cl * 1000) == c) {
		System.out.println(c + " cycles remaining - Error = " + mon.getGlobalError());
	}
}

public void netStoppedError(NeuralNetEvent e,String error) {
}

}


