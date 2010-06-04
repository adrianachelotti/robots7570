// JFileChooser Example
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

@SuppressWarnings("serial")
class JFileChooserExample extends JFrame implements ActionListener {
   
	private JTextArea jep = new JTextArea();
	private boolean redEntrenada=false;
	private GeometricFigureRecognizer reconocedor = new GeometricFigureRecognizer();
	private boolean LOGGER = false;
	
	public JFileChooserExample() {
      super("Reconocedor de Figuras");
      Container cp = getContentPane( );
      cp.add(new JScrollPane(jep), BorderLayout.CENTER);
      JMenu menu = new JMenu("Inicio");
      menu.add(make("Entrenar"));
      menu.add(make("Reconocer"));
      menu.addSeparator();
      menu.add(make("Salir"));
      JMenuBar menuBar = new JMenuBar();
      menuBar.add(menu); 
      setJMenuBar(menuBar);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setSize(500,400);
   }

   public void actionPerformed(ActionEvent e) {
      String ac = e.getActionCommand();
      if(ac.equals("Entrenar")) 
      {
    	  if (!redEntrenada)
    	  {
    	  	  this.reconocedor.inicializarReconocimiento(jep);
    	  	  redEntrenada=true;
    	  }
    	  else
    		  mensajeRedEntrenada();
      }
      else if(ac.equals("Reconocer")) 
      {
    	  String pathFigura = obtenerFigura();
    	  if (pathFigura != null)
    	  {
    		  reconocedor.reconocer(pathFigura,jep);
    	  }
      }
      else if(ac.equals("Salir")) System.exit(0);
   }

   public void mensajeRedEntrenada()
   {
	   String mensaje = "La red ya se encuentra entrenada.";
	   if(LOGGER)System.out.println(mensaje);
	  	   
	   JOptionPane.showMessageDialog(this,mensaje,"Información",
				JOptionPane.INFORMATION_MESSAGE);
   }
   
      
   private String obtenerFigura()
   {
      JFileChooser jfc = new JFileChooser();
      int result = jfc.showSaveDialog(this);
      if(result == JFileChooser.CANCEL_OPTION) return null;
      File file = jfc.getSelectedFile();
      if (LOGGER) System.out.println("Directorio:" + file.getAbsolutePath());
      return file.getAbsolutePath();
            
   }

   private JMenuItem make(String name) {
      JMenuItem m = new JMenuItem(name);
      m.addActionListener(this);
      return m;
   }

   public static void main(String[] args) {
      new JFileChooserExample().setVisible(true);}
}