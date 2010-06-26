import org.jgap.*;



public class MinimizingDistanceFunction extends FitnessFunction {

    
    //matriz con las productividad por maquina de cada producto
    private int matrixProductionMachine[][];
    
    //la produccion total que se espera conseguir de cada producto.
    private int m_totalProduction [];
    
    //la cantidad de productos distintos
    private int m_NumberOfProducts; 
    
    //la cantidad de maquinas disponibles
    private int m_NumberOfMachine;
    
    //la cantidad de dias que se quieren planificar
    private int m_DaysToPlanning;

    
    //Se pasa la matriz con las producciones de las maquinas
    // un vector  con la produccion total de cada producto
    // y la cantidad de dias que se quiere planificar.
    public MinimizingDistanceFunction(int matrix [][] ,int totalProduction [], int daysToPlanning ) {
    	
        if( matrix.length ==0) {
        	
            throw new IllegalArgumentException("Completar la matriz de produccion");
        }

        m_totalProduction = totalProduction;
        matrixProductionMachine =matrix;
        m_NumberOfProducts = matrixProductionMachine.length;
        m_NumberOfMachine = matrixProductionMachine[0].length;
        m_DaysToPlanning =daysToPlanning;
    }

    /**
     * Determina que tan buena es la solucion propuesta
     * Condiciones:
     * Se busca que la planificacion total se cumpla. Se considera que todos
     * los productos son importantes, por lo tanto no se admite soluciones con
     * produccion total menor a la planificadas.
     * @param a_subject: The Chromosome instance to evaluate.
     *
     * @return A positive integer reflecting the fitness rating of the given
     *         Chromosome.
     */
    public double evaluate(IChromosome a_subject) {
    	
    	//dame la produccion planificada que hay en a_subject
    	int totalProductionPlanning [] = getTotalProductionPlanning(a_subject);
    	
    	//evalua la produccion planificada con la esperada
    	double ranking = getEvaluation (totalProductionPlanning);
    	
    	
    	if (ranking <=0) return 1;
    	else return ranking;
    }

    public int [] getTotalProductionPlanning(IChromosome a_potentialSolution){
    
    	int productionPlanning[] = new int[m_NumberOfProducts];
    	
    	
    	for (int i=0;i<m_NumberOfProducts;i++)
    	  		productionPlanning[i]=0;
    	
    	for (int i=0;i<m_NumberOfMachine;i++)
    	{
    		for (int j=0;j<m_DaysToPlanning;j++)
    		{
    			int product = (Integer) a_potentialSolution.getGene(m_DaysToPlanning*i+j).getAllele();
    			int machine =i;
    			if (product <m_NumberOfProducts)
    				productionPlanning[product]+=matrixProductionMachine[product][machine]; 
    		}
    	}
    	return productionPlanning;
    }
    	
 //se evalua la solucion en base al % de la produccion que se esperaba y a la que
 // se planifico.
  private double getEvaluation (int totalProductionPlanning[])
   {
	  double ranking =0;
	  
	  for (int i=0;i<m_NumberOfProducts;i++){
		  
		  double tProductionPlanning=totalProductionPlanning[i];
		  double totalProductionExpected =m_totalProduction[i];
		  	
		  //el procentaje de diferencia entre lo planificado y la produccion esperada
		  double diff_rate = Math.abs(tProductionPlanning-totalProductionExpected)/totalProductionExpected;
		 
		  if (diff_rate >1)
			  diff_rate = 1;
		  
		  ranking+=100*(1-diff_rate);
	  }
 
	 return ranking; 
 	}
  
  
}
