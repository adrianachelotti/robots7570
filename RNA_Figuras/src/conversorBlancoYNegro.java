import java.awt.Graphics;
import java.awt.Image;
import java.awt.Canvas;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;




public class conversorBlancoYNegro {

	private BufferedImage imageO;
	private int IMAGEN_HEIGHT = 240;
	private int IMAGEN_WIDTH = 320;
	public conversorBlancoYNegro() {
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
		int screenWidth = this.imageO.getWidth();
		int screenHeigth = this.imageO.getHeight();
		int xBegin=0;
		int xEnd=0;
		int yBegin=0;
		int yEnd=0;
		int i=0;
		int j=0;
		boolean extremoEncontrado= false;
		
		//busco el extremo izquierdo de la figura
		while (i<screenWidth &&!extremoEncontrado) 
		{
			j=0;
			while (j<screenHeigth &&!extremoEncontrado)
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
		i=screenWidth-1;
		j=screenHeigth-1;
		extremoEncontrado=false;
		//busco el extremo derecho
		while (i>=0 &&!extremoEncontrado) 
		{
			j=screenHeigth-1;	
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
		while (j<screenHeigth &&!extremoEncontrado) 
		{
			i=0;
			while (i<screenWidth &&!extremoEncontrado)
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
		i=screenWidth-1;
		j=screenHeigth-1;
		extremoEncontrado=false;
		//busco el extremo superior
		while (j>=0 &&!extremoEncontrado) 
		{
			i=screenWidth-1;
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
		BufferedImage imagenAux = this.imageO.getSubimage(xBegin, yBegin, xEnd-xBegin, yEnd-yBegin);
		try {
			ImageIO.write(imagenAux, "jpg", new File("Figuras/Recortes/"+name+".jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void main (String args[]) 
	{
		
		conversorBlancoYNegro converso = new conversorBlancoYNegro();
		String path = "Figuras/";
		File directorio = new File(path);
		String [] ficheros = directorio.list();
		for (int s = 0; s < ficheros.length; s++) 
		{
			if (ficheros[s].contains(".jpg"))
			{
				converso.cargarImagen("Figuras/"+ficheros[s]);
				converso.procesarImagen();
				converso.recortarImagen(ficheros[s]);
				
				System.out.println(ficheros[s]);
			}
		}

	
	}
}
