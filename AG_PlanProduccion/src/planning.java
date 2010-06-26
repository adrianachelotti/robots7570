import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.jgap.*;
import org.jgap.impl.*;
import java.util.*;

//import som.*;

public class planning {

	/**
	 * @param args
	 */
	public static void main(String[] args)throws Exception {

		// Default Configuration
		// -------------------------------------------------------------
		Configuration conf = new DefaultConfiguration();


		//la cantidad de productos distintos
		int numberOfProducts=4; 

		//la produccion total que se espera conseguir de cada producto.
		int totalProduction [];

		//la cantidad de maquinas disponibles
		int numberOfMachine=5;

		//la cantidad de dias que se quieren planificar
		int daysToPlanning=6;

		//cargo la produccion total de cada producto.
		totalProduction = getTotalProductionByProduct(numberOfProducts);
		totalProduction = new int[4];
		totalProduction[0] = 116;
		totalProduction[1] = 198;
		totalProduction[2] = 84;
		totalProduction[3] = 101;

		// Cargo la matriz de productividad de las maquinas
		// ------------------------------------------------------------	
		int productionMachine[][] = getProductionMachine("src/planning/infoMachine.txt",numberOfProducts,numberOfMachine); 

		MinimizingDistanceFunction myFunc = new MinimizingDistanceFunction(productionMachine ,totalProduction,daysToPlanning); 



		conf.setFitnessFunction(myFunc);

		// 
		// --------------------------------------------------------------
		Gene[] sampleGenes = new Gene[daysToPlanning*numberOfMachine];

		for (int i = 0; i < sampleGenes.length; i++) {

			sampleGenes[i] = new IntegerGene(conf, 0, numberOfProducts-1);
		}


		Chromosome sampleChromosome = new Chromosome(conf, sampleGenes);

		conf.setSampleChromosome(sampleChromosome);

		// 
		// --------------------------------------------------------------
		conf.setPopulationSize(3000);

		Genotype population = Genotype.randomInitialGenotype(conf);

		int MAX_ALLOWED_EVOLUTIONS = 20;




		System.out.println("The production expected: ");

		for (int j=0;j<numberOfProducts;j++)
		{
			System.out.println(" Product: "+j+ " --->"+totalProduction[j] );

		}

		List bestChromosomes = new ArrayList();//todos los individuos
		HashMap map = new HashMap<IChromosome, Integer>();//sin repetidos

		for( int i = 0; i < MAX_ALLOWED_EVOLUTIONS; i++) {

			IChromosome bestSolutionSoFar = population.getFittestChromosome();

			System.out.println("Poblacion "+i);


			int productionPlanning[] = myFunc.getTotalProductionPlanning(bestSolutionSoFar);


			System.out.println("The best solution contained the following production: ");

			for (int j=0;j<numberOfProducts;j++)
			{
				System.out.println(" Product: "+j+ " --->"+productionPlanning[j] );

			}
			population.evolve();  
			
			if(i>1) // si queremos juntar datos a partir de alguna evolucion
			{
				List aux = population.getFittestChromosomes(1000);
				Iterator it = aux.iterator();
				int count=0;
				while(it.hasNext())
				{
					IChromosome chromo = (IChromosome)it.next();
					
						if(map.containsKey(chromo))
						{
							Integer cant = (Integer)map.get(chromo) + 1;
							if (count<35)
							{
								map.put(chromo, cant);
							}
						}
						else
						{
							if (count<35)
							{
								map.put(chromo,1);	
								count++;
							}
						}
				}
				bestChromosomes.addAll(population.getFittestChromosomes(45));
			}
		}
		
		IChromosome bestSolutionSoFar = population.getFittestChromosome();
		printPlanning(bestSolutionSoFar,numberOfMachine,daysToPlanning);
		
		Set set = map.keySet();
		Iterator it = set.iterator();
		List chromoNotRepeated = new ArrayList();
		while(it.hasNext())
		{
			chromoNotRepeated.add(it.next());
		}
		

		// Salida entera, salida sin repetidos
		writeOutputFile(bestChromosomes, numberOfMachine, daysToPlanning,"src/planning/out.txt");
		writeOutputFile(chromoNotRepeated, numberOfMachine, daysToPlanning,"src/planning/outrun.txt");

		System.out.println("Cantidad de individuos recolectados: " + bestChromosomes.size());
		System.out.println("Cantidad de individuos sin repetidos: " + chromoNotRepeated.size());

		//RED SOM
		
	//	Som redSom= new Som(21,3,3);
		
		// Entrenamos la red con todos los individuos (hay repetidos)
		//redSom.train( "src/planning/outrun.txt" , 0.4 , 0.4 , 50, chromoNotRepeated.size() );
		//redSom.run("src/planning/outrun.txt", "src/planning/salidaRed.txt");
		
		//clustering("src/planning/outrun.txt","src/planning/salidaRed.txt","src/planning/output4Analysis.txt");

	}


	private static void clustering(String fileGen, String fileSOM, String fileOutput) throws IOException{
		
		final int cantNeuronas=9;
		
		BufferedReader archSom = new BufferedReader(new FileReader(fileSOM));
		
		BufferedReader archPlanning = new BufferedReader(new FileReader(fileGen));
		
		String lineS = archSom.readLine();
		String lineP = archPlanning.readLine();
		
		PrintWriter archOutput= new PrintWriter( new BufferedWriter(new FileWriter(fileOutput)));
		
		while (lineS !=null){
			
			String[] salidaNeuronas = lineS.split(";");
			String[] plan= lineP.split(";");
			
			for (int neurona=0; neurona<cantNeuronas; neurona++ ){
				
				int valorNeurona= (int)Float.parseFloat(salidaNeuronas[neurona]);
				
				if (valorNeurona!=0){
					for( int i=0; i<21; i++){
						archOutput.write(plan[i]);
						archOutput.write("\t");
					}
					archOutput.write(Integer.toString(neurona)+".0");
					archOutput.write("\r\n");
					break;
				}
				
			}
			lineS = archSom.readLine();
			lineP = archPlanning.readLine();
		}
		archOutput.close();
		archSom.close();
		archPlanning.close();
	}
	
	private static void writeOutputFile(List population, int numberOfMachine, int daysToPlanning, String file)
	{
		Iterator it = population.iterator();
		try {
			PrintWriter writer = new PrintWriter( new BufferedWriter(new FileWriter(file)));
			while(it.hasNext())
			{
				IChromosome fitChromosome = (IChromosome)it.next();
				for (int i=0;i<numberOfMachine;i++)
				{
					for (int j=0;j<daysToPlanning;j++)
					{
						int product = (Integer) fitChromosome.getGene(daysToPlanning*i+j).getAllele();
						writer.print(product);
						writer.print(".0");
						if(i!=numberOfMachine-1||j!=daysToPlanning-1)
							writer.print(";");
					}
				}
				writer.print("\r\n");
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param numberOfProduct
	 * @return int[] totalProduction. La produccion que se espera obtener.
	 */
	private static int[] getTotalProductionByProduct(int numberOfProduct){
		int totalProduction[] = new int [numberOfProduct];

		//TODO leer de algun lado estos valores
		for (int i=0;i<numberOfProduct;i++)
			totalProduction[i]= i*50+50;

		return totalProduction;
	}
	
	/**
	 * @param filePath
	 * @return int[][]
	 */
	private static	int [][]  getProductionMachine(String filePath,int numberOfProducts,int numberOfMachine)throws Exception { 

		int matrix[][] = new int [numberOfProducts][numberOfMachine];

		BufferedReader reader = new BufferedReader(new FileReader(filePath));

		for(int i=0;i<numberOfProducts; i++){

			String line = reader.readLine();
			String[] numbers = line.split("\\t");
			for(int j=0; j<numberOfMachine; j++) {

				matrix[i][j] = new Integer(numbers[j]);
			}
		}

		return matrix;
	}

	private static void printPlanning(IChromosome bestSolutionSoFar,int numberOfMachine,int daysToPlanning){

		System.out.print("Days");
		for (int j=0;j<daysToPlanning;j++)
			System.out.print("           "+j);
		System.out.println("");



		for (int i=0;i<numberOfMachine;i++)
		{
			for (int j=0;j<daysToPlanning;j++)
			{
				int product = (Integer) bestSolutionSoFar.getGene(daysToPlanning*i+j).getAllele();
				int machine =i;
				int day = j;

				if (j==0)
					System.out.print("Machine:"+machine+" -->");

				System.out.print(" Product: "+product+" " );
			}
			System.out.println("");
		}
	}

}
