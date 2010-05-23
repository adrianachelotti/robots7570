import java.awt.Graphics;
import java.awt.Image;
import java.awt.Canvas;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;




public class ProcesadorDeImagenes {

	private BufferedImage imageO;
	private BufferedImage imagenR;
	private int IMAGEN_HEIGHT = 240;
	private int IMAGEN_WIDTH = 320;
	public ProcesadorDeImagenes() {
		// TODO Auto-generated constructor stub
		this.imageO= new BufferedImage(IMAGEN_WIDTH,IMAGEN_HEIGHT,BufferedImage.TYPE_INT_RGB);
	}
	
	public void cargarImagen(String nameFile)
	{
		File archivoImagenAProcesar = new File(nameFile);
		try {
			this.imageO= ImageIO.read(archivoImagenAProcesar);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
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
	public void recortarImagen(String name)
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
		System.out.println(xBegin+"  "+xEnd+"  "+yBegin+"  "+yEnd);
		this.imagenR= this.imageO.getSubimage(xBegin, yBegin, xEnd-xBegin, yEnd-yBegin);
		try {
			ImageIO.write(this.imagenR, "jpg", new File("Figuras/Recortes/"+name.replace(".jpg", "_recotada")+".jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	


	private double[] getPorcentajes() 
	{
		int ancho = this.imagenR.getWidth();
		int alto = this.imagenR.getHeight();
		double[] porcentajes = {0,0,0};
		double unTercioDelArea=(double)(ancho*alto);
		for (int i = 0; i < alto; i++) 
		{
			for (int j = 0; j < ancho; j++) 
			{
							
				if(i<alto/3)
				{
					if (this.imagenR.getRGB(j, i)!=-1)
					{
						porcentajes[0]++;
					}
				}
				if((i>=alto/3)&&(i<=(alto*2/3)))
				{
					if (this.imagenR.getRGB(j,i)!=-1)
					{
						porcentajes[1]++;
					}
				}
				if((i<=alto)&&(i>=(alto*2/3)))
				{
					if (this.imagenR.getRGB(j, i)!=-1)
					{
						porcentajes[2]++;
					}
				}
				
			}
			
		}
		porcentajes[0]=(double)100* porcentajes[0]/unTercioDelArea;
		porcentajes[1]=(double)100* porcentajes[1]/unTercioDelArea;
		porcentajes[2]=(double)100* porcentajes[2]/unTercioDelArea;
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
				converso.recortarImagen(ficheros[s]);
				double porcentajes[]= converso.getPorcentajes();
				for (int i = 0; i < porcentajes.length; i++) {
					System.out.println(porcentajes[i]);	
				}
					
				
				
			}
		}

	
	}
	
}
