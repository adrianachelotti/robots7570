import org.joone.engine.*;
import org.joone.engine.learning.*;
import org.joone.io.*;
import org.joone.net.NeuralNet;

/**
 * Reconocimiento de triangulos, cuadrados y circulos regulares
 * basado en ejemplo XORMemory.java e ImmediateEmbeddedXOR.java
 * ambos programas se pueden encontrar en el joone-engine
 * en el directorio samples/engine/xor
 */
public class GeometricFigureRecognizer implements NeuralNetListener {
	
	// Input para entrenamiento
	private double[][]  vectorEntrada = new double[][] {
//prop. franja 1,   2,   3,  Tri  Cir  Cua  	
			{0.17, 0.5, 0.67, 1.0, 0.0, 0.0},
			{0.7, 0.98, 0.7, 0.0, 1.0, 0.0},
			{1.0, 1.0, 1.0, 0.0, 0.0, 1.0}
	};
	
	//Vector de prueba (luego va a llegar esta entrada
	private double[][]  vectorPrueba = new double[][] {
			{0.17, 0.5, 0.67},
			{0.7, 0.98, 0.7},
			{1.0, 1.0, 1.0}
	};
	
	private long mills;
	private NeuralNet red;
	private LinearLayer	entrada;
	private SigmoidLayer	oculta;
	private SigmoidLayer	salida;
	private Monitor monitor = new Monitor();
	private int cant_de_img=3;
	
	
	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		GeometricFigureRecognizer   xor = new GeometricFigureRecognizer();
		
		xor.iniciar();
		xor.entrenar();
		xor.testear();
	}
	
	/**
	 * Method declaration
	 */
	public void iniciar() {
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
	
	public void entrenar(){
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
		
		// set the input data
//		input.addInputSynapse(inputStream);
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
		monitor.setTrainingPatterns(3);	// # of rows (patterns) contained in the input file
		monitor.setTotCicles(10000);		// How many times the net must be trained on the input patterns
		monitor.setLearning(true);		// The net must be trained
		mills = System.currentTimeMillis();
		monitor.Go();					// The net starts the training job
	}
	
	public  void testear(){
		// Joone utiliza hilos para ejecutar el entrenamiento,
		// por lo tanto debemos esperar a que el entrenamiento termine
		// antes de probar la red. El metodo join() espera a que los hilos de la 
		// red terminen.
		red.join();
		System.out.println("***************** Test *****************");
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
		for (int i=0; i < cant_de_img; ++i) {
			// Prepare the next input pattern
			System.out.println("Input: "+vectorPrueba[i][0]+"  "+vectorPrueba[i][1]+"  "+vectorPrueba[i][2]);
			Pattern iPattern = new Pattern(vectorPrueba[i]);
			iPattern.setCount(i+1);
			// Interrogate the net
			memInp.fwdPut(iPattern);
			// Read the output pattern and print out it
			// double[] pattern = memOut.getNextPattern();
			Pattern pattern = memOut.fwdGet();
			String salida;
			if((pattern.getArray()[0]>0.8)&&(pattern.getArray()[1]<0.1)&&(pattern.getArray()[2]<0.1))
				salida="Es un TRIANGULO";
			else if((pattern.getArray()[0]<0.1)&&(pattern.getArray()[1]>0.8)&&(pattern.getArray()[2]<0.1))
				salida="Es un CIRCULO";
			else if((pattern.getArray()[0]<0.1)&&(pattern.getArray()[1]<0.1)&&(pattern.getArray()[2]>0.8))
				salida="Es un CUADRADO";
			else
				salida="La figura no fue reconocida";
			
			System.out.println("Output: "+salida);
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


