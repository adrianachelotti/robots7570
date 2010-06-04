// JFileChooser Example
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

@SuppressWarnings("serial")
class JFileChooserExample extends JFrame implements ActionListener {
   
	private JEditorPane jep = new JEditorPane();
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
      setSize(200,300);
   }

   public void actionPerformed(ActionEvent e) {
      String ac = e.getActionCommand();
      if(ac.equals("Entrenar")) 
      {
    	  if (!redEntrenada)
    	  {
    	  	  this.reconocedor.inicializarReconocimiento();
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
    		  System.out.println("IMAGEN: " + pathFigura);
    		  reconocedor.reconocer(pathFigura);
    	  }
      }
      else if(ac.equals("Salir")) System.exit(0);
   }

   public void mensajeRedEntrenada()
   {
	   System.out.println("RED YA ENTRENADA");
   }
   
   
   /*
    private void openFile() {
      JFileChooser jfc = new JFileChooser();
      int result = jfc.showOpenDialog(this);
      if(result == JFileChooser.CANCEL_OPTION) return;
      try {
         File file = jfc.getSelectedFile();
         BufferedReader br = new BufferedReader(new FileReader(file));
         String s=""; int c=0;
         while((c=br.read())!=-1) s+=(char)c;
         br.close(); jep.setText(s);
      } catch (Exception e) {
         JOptionPane.showMessageDialog(this,e.getMessage(),
         "File error",JOptionPane.ERROR_MESSAGE);}
   }
*/
   private String obtenerFigura()
   {
      JFileChooser jfc = new JFileChooser();
      int result = jfc.showSaveDialog(this);
      if(result == JFileChooser.CANCEL_OPTION) return null;
      File file = jfc.getSelectedFile();
      if (LOGGER) System.out.println("Directorio:" + file.getAbsolutePath());
      return file.getAbsolutePath();
      
      /*try
      {
         BufferedWriter bw = new BufferedWriter(new FileWriter(file));
         bw.write(jep.getText());
         bw.close();
      }
      catch (Exception e) 
      {
         JOptionPane.showMessageDialog(this,e.getMessage(),"File Error",
        		 					JOptionPane.ERROR_MESSAGE);
      }*/
   }

   private JMenuItem make(String name) {
      JMenuItem m = new JMenuItem(name);
      m.addActionListener(this);
      return m;
   }

   public static void main(String[] args) {
      new JFileChooserExample().setVisible(true);}
}