import java.awt.*;
import java.awt.event.*;
import java.util.Stack;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class UserPanel extends JPanel
{
	//store the reference to the connected mandelbrot set
	MandelbrotPanel attachedMandelbrot;
	//max and min for the slider
	static final int ITER_MIN = 0;
	static final int ITER_MAX = 2048;
	//fields that show the current coordinates of the complex plane 
	private JTextField leftX = new JTextField(7);
	private JTextField rightX = new JTextField(7);
	private JTextField topY = new JTextField(6);
	private JTextField bottomY = new JTextField(6);
	//button for zooming out
	private JButton zoomOut = new JButton("Zoom Out");
	//combobox for the fractal that is displayed 
	private JComboBox<String> fractal = new JComboBox<String>();
	//slider that lets the user choose the max number of iterations
	private JSlider noIterations;
	//stack that saves the old coordinates to display in case of zooming out
	private Stack<Double> oldCoords;
	
	public UserPanel(MandelbrotPanel m)
	{
		this.attachedMandelbrot = m;
		setLayout(new FlowLayout());
		setPreferredSize(new Dimension(500, 72));
		//get the coordinates that are stored in the mandelbrot stack
		oldCoords = attachedMandelbrot.getOldCoords();
		//assign values for the iterations
		noIterations = new JSlider(JSlider.HORIZONTAL, ITER_MIN, ITER_MAX, 512);
	}
	
	public void init()
	{
		//set the text of each field to the current coordinates of the mandelbrot set
		leftX.setText(attachedMandelbrot.getLeftX().toString());
		rightX.setText(attachedMandelbrot.getRightX().toString());
		topY.setText(attachedMandelbrot.getTopY().toString());
		bottomY.setText(attachedMandelbrot.getBottomY().toString());
				
		//set the properties of the slider 
		noIterations.setMajorTickSpacing(400);
		noIterations.setMinorTickSpacing(200);
		noIterations.setPaintTicks(true);
		noIterations.setPaintLabels(true);
		
		//add the two fractals as choices to the user
		fractal.addItem("Mandelbrot");
		fractal.addItem("Burning ship");
		
		//add the labels and the text fields to the panel
		add(new JLabel("Left X"));
		add(leftX);
		add(new JLabel("Right X"));
		add(rightX);
		add(new JLabel("Top Y"));
		add(topY);
		add(new JLabel("Bottom Y"));
		add(bottomY);
		
		//add the slider, zoom out button and the combobox with the fractals to the panel
		add(noIterations);
		add(zoomOut);
		add(fractal);
		
		//Add one listener to all the fields 
		UserListener ul = new UserListener();
		leftX.addActionListener(ul);
		rightX.addActionListener(ul);
		topY.addActionListener(ul);
		bottomY.addActionListener(ul);
		
		//add respective listener to each of the components
		noIterations.addChangeListener(new SliderListener());
		zoomOut.addActionListener(new ZoomOutListener());
		fractal.addItemListener(new FractalListener());
	}
	
	//Listens to changes that user makes to the fields and repaints the mandelbrot display
	class UserListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{	
			//if the user changes the value in one of the fields get the values of all fields
			double lx = Double.parseDouble(leftX.getText());
			double rx = Double.parseDouble(rightX.getText());
			double ty = Double.parseDouble(topY.getText());
			double by = Double.parseDouble(bottomY.getText());
			//notify the mandelbrot panel to the changes made by the user
			attachedMandelbrot.changePlaneCoords(lx, by, rx, ty);
			//repaint with the new coordinates 
			attachedMandelbrot.repaint();
		}
	}
	
	//listens for changes made to the number of iterations
	class SliderListener implements ChangeListener
	{
		public void stateChanged(ChangeEvent e)
		{
			//notify the mandelbrot panel to the changes 
			attachedMandelbrot.setIteration(noIterations.getValue());
			//repaint using the new number of iterations
			attachedMandelbrot.repaint();
		}
	}
			
	//llistener for zoom out events
	class ZoomOutListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			//check if you can zoom out more
			if (attachedMandelbrot.getZoomDepth() > 0)
			{
				//get the previous values from the stack that stores them
				double xmin = oldCoords.pop();
				double ymin = oldCoords.pop();
				double xmax = oldCoords.pop();
				double ymax = oldCoords.pop();
				//update the text fields 
				changeCoordinates(xmin, xmax, ymax, ymin);
				//notify the mandelbrot panel to the changes 
				attachedMandelbrot.changePlaneCoords(xmin, ymin, xmax, ymax);
				//decrement variable that tells how many zoom outs are possible
				attachedMandelbrot.decrementZoomDepth();
				//repaint the mandelbrot display using the new "old" coordinates 
				attachedMandelbrot.repaint();
			}
		}
	}
		
	//listener for changes in the fractal that is displayed 
	class FractalListener implements ItemListener
	{
		public void itemStateChanged(ItemEvent e)
		{
			//set the fractal in the Mandelbrot panel to the user chosen one
			attachedMandelbrot.toggleFractal(fractal.getSelectedItem().toString());
			//repaint using the new fractal
			attachedMandelbrot.repaint();
		}
	}
	
	//helper method that changes the text in the fields that display the current coordinates
	public void changeCoordinates(Double left, Double right, Double top, Double bottom)
	{
		//change text to the values passed as a parameter to the method
		this.leftX.setText(left.toString());
		this.rightX.setText(right.toString());
		this.topY.setText(top.toString());
		this.bottomY.setText(bottom.toString());
	}
}
