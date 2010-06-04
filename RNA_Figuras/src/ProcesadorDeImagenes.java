import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;




public class ProcesadorDeImagenes {

	private BufferedImage imageO;
	private BufferedImage imagenR;
	private int IMAGEN_HEIGHT = 240;
	private int IMAGEN_WIDTH = 320;
	private String nombreArchivoAProcesar;
	
	public ProcesadorDeImagenes() {
		// TODO Auto-generated constructor stub
		this.imageO= new BufferedImage(IMAGEN_WIDTH,IMAGEN_HEIGHT,BufferedImage.TYPE_INT_RGB);
	}
	/**
	 * Carga en BufferedImage la imagen de un archivo
	 * @param nameFile
	 */
	public void cargarImagen(String nameFile)
	{
		File archivoImagenAProcesar = new File(nameFile);
		this.nombreArchivoAProcesar = archivoImagenAProcesar.getName();
		try 
		{
			this.imageO= ImageIO.read(archivoImagenAProcesar);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		
	}
	/**
	 * Toma la imagenO y cambia todo pixel distinto de blanco
	 * y los setea a negro
	 */
	public void procesarImagen()
	{
		int screenWidth = this.imageO.getWidth();
		int screenHeigth = this.imageO.getHeight();
		for (int i = 0; i < screenHeigth; i++) 
		{
			for (int j = 0; j < screenWidth; j++) 
			{
				int color =imageO.getRGB(j,i);
				
				if (color!=-1)
				{
					imageO.setRGB(j, i,0x000000);
				}
				
			}
			
		}	
		

	}
	 /**
	  * Busco los delimitadores de la figura dentro de la imagen
	  * Graba esta imagen transformada en la carpeta de Recortes 
	  * @param name: nombre del archivo de la imagen
	  */
	public void recortarImagen()
	{
		int imagenWidth = this.imageO.getWidth();
		int imagenHeight = this.imageO.getHeight();
		int xBegin=0;
		int xEnd=0;
		int yBegin=0;
		int yEnd=0;
		int i=0;
		int j=0;
		boolean extremoEncontrado= false;
		
		//busco el extremo izquierdo de la figura
		while (i<imagenWidth &&!extremoEncontrado) 
		{
			j=0;
			while (j<imagenHeight &&!extremoEncontrado)
			{
				int color =imageO.getRGB(i,j);
				if (color!=-1)
				{
					extremoEncontrado=true;
					xBegin=i;
				}
				j++;
			}
			i++;
		}
		i=imagenWidth-1;
		j=imagenHeight-1;
		extremoEncontrado=false;
		//busco el extremo derecho
		while (i>=0 &&!extremoEncontrado) 
		{
			j=imagenHeight-1;	
			while (j>=0 &&!extremoEncontrado)
			{
				int color =imageO.getRGB(i,j);
				if (color!=-1)
				{
					extremoEncontrado=true;
					xEnd=i;
				}
				j--;
			}
			i--;
		}
		

		i=0;
		j=0;
		extremoEncontrado=false;
		//busco el extremo superior
		while (j<imagenHeight &&!extremoEncontrado) 
		{
			i=0;
			while (i<imagenWidth &&!extremoEncontrado)
			{
				int color =imageO.getRGB(i,j);
				if (color!=-1)
				{
					extremoEncontrado=true;
					yBegin=j;
				}
				i++;
			}
			j++;
		}
		
		//busco el extremo inferior
		i=imagenWidth-1;
		j=imagenHeight-1;
		extremoEncontrado=false;
		//busco el extremo superior
		while (j>=0 &&!extremoEncontrado) 
		{
			i=imagenWidth-1;
			while (i>=0 &&!extremoEncontrado)
			{
				int color =imageO.getRGB(i,j);
				if (color!=-1)
				{
					extremoEncontrado=true;
					yEnd=j;
				}
				i--;
			}
			j--;
		}
		//TODO ver que pasa si los limites se cruzan
//		if ((xEnd<xBegin)||(yBegin<yEnd))
		this.imagenR= this.imageO.getSubimage(xBegin, yBegin, xEnd-xBegin, yEnd-yBegin);
		try {
			ImageIO.write(this.imagenR, "jpg", new File("Figuras/Recortes/"+this.nombreArchivoAProcesar.replace(".jpg", "_recotada")+".jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	

	/**
	 * Divide la imagen en tres zonas en sentido horizontal y calcula el porcentaje
	 * de pixeles negros sobre el total de la subzona
	 * @return double[] que contiene valores  entre 0 y 1.
	 */
	public double[] getPorcentajes() 
	{
		int ancho = this.imagenR.getWidth();
		int alto = this.imagenR.getHeight();
		double[] porcentajes = {0,0,0};
		int total1=0;
		int total2=0;
		int total3=0;
		for (int i = 0; i < alto; i++) 
		{
			for (int j = 0; j < ancho; j++) 
			{
							
				if(i<=alto/3)
				{
					total1++;
					if (this.imagenR.getRGB(j, i)!=-1)
					{
						porcentajes[0]++;
					}
				}
				if((i>alto/3)&&(i<=(alto*2/3)))
				{
					total2++;
					if (this.imagenR.getRGB(j,i)!=-1)
					{
						porcentajes[1]++;
					}
				}
				if((i<=alto)&&(i>=(alto*2/3)))
				{
					total3++;
					if (this.imagenR.getRGB(j, i)!=-1)
					{
						porcentajes[2]++;
					}
				}
				
			}
			
		}
		porcentajes[0]=(double) porcentajes[0]/total1;
		porcentajes[1]=(double) porcentajes[1]/total2;
		porcentajes[2]=(double) porcentajes[2]/total3;
		return porcentajes;
		
	}
	
	
	
	public static void main (String args[]) 
	{
		
		ProcesadorDeImagenes converso = new ProcesadorDeImagenes();
		String path = "Figuras/";
		File directorio = new File(path);
		String [] ficheros = directorio.list();
		for (int s = 0; s < ficheros.length; s++) 
		{
			if (ficheros[s].contains(".jpg"))
			{
				System.out.println(ficheros[s]);
				converso.cargarImagen("Figuras/"+ficheros[s]);
				converso.procesarImagen();
				converso.recortarImagen();
				double porcentajes[]= converso.getPorcentajes();
				for (int i = 0; i < porcentajes.length; i++) 
				{
					System.out.println(porcentajes[i]);	
				}				
				// si el archivo  ya se proceso muevo el archivo
				File archivo = new File("Figuras/"+ficheros[s]);
				archivo.renameTo(new File("Figuras/Procesadas/"+ficheros[s]));
				
			}
		}

	
	}
	
}
