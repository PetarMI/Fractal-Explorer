import java.awt.*;
import java.awt.image.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.ArrayList;

import javax.swing.*;

public class JuliaPanel extends JPanel
{
	private static final int SIZE = 500;
	//visible part of the complex plane coordinates and their default values
	private static final double xmin = -2.0;
	private static final double ymin = -1.6;
	private static final double xmax = 2.0;
	private static final double ymax = 1.6;
	//Difference of values of adjacent complex numbers 
	private double xdiff;
	private double ydiff;
	//Set of complex numbers visible for the specified coordinates
	private ComplexNumber[][] cnSet = new ComplexNumber[SIZE][SIZE];
	//Number to construct the Julia set from
	private ComplexNumber userSelectedPoint;
	private BufferedImage image = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
	//service to execute the thread that count the iterations
	private ExecutorService iterExecutor = Executors.newCachedThreadPool();
	//store the threads that count the iterations
	private ArrayList<IterCounter> threads = new ArrayList<IterCounter>();
	
	public JuliaPanel()
	{	
		//calculate difference between adjacent numbers
		xdiff = (xmax - xmin)/SIZE;
		ydiff = (ymax - ymin)/SIZE;
		
		//fill the set with the numbers according to each pixel 
		for(int i = 0; i < SIZE; i++)
		{
			for (int j = 0; j < SIZE; j++)
			{
				cnSet[i][j] = new ComplexNumber(xmin + i*xdiff, ymin + j*ydiff);
			}
		}
		this.setBounds(0, 0, 500, 500);
		//add 2 threads as default
		threads.add(new IterCounter(this, 0, 500, 0, 250));
		threads.add(new IterCounter(this, 0, 500, 250, 500));
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if (userSelectedPoint != null)
		{	
			//start all the threads that count the iterations and set colors to the pixels
			//all threads access different parts of the set of complex numbers simultaneously
			//notifyAll makes the default thread wait until the help threads are finished changing the image
			try
			{
				iterExecutor.invokeAll(threads);
			} 
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			//after the threads calculate the image draw it
			g.drawImage(image, 0, 0, this);
		}	
		else
		{
			//if there is no pint to calculate the image from display white space
			this.setBackground(Color.WHITE);
		}
	}
	
	//change the number of threads
	public void setThreads(int n)
	{
		threads.clear();
		if (n == 2)
		{
			threads.add(new IterCounter(this, 0, 500, 0, 250));
			threads.add(new IterCounter(this, 0, 500, 250, 500));
		}
		else if (n == 4)
		{
			threads.add(new IterCounter(this, 0, 250, 0, 250));
			threads.add(new IterCounter(this, 250, 500, 0, 250));
			threads.add(new IterCounter(this, 0, 250, 250, 500));
			threads.add(new IterCounter(this, 250, 500, 250, 500));
		}
		else 
		{
			threads.add(new IterCounter(this, 0, 500, 0, 500));
		}
	}
	
	//used by other panels to set the number that the image is calculated from
	public void setSelectedNumber(ComplexNumber cn)
	{
		this.userSelectedPoint = cn;
	}
	
	//getter for the number calculating the image
	public ComplexNumber getSelectedNumber()
	{
		return this.userSelectedPoint;
	}
	
	//getter for the image that is drawn
	public BufferedImage getImage()
	{
		return this.image;
	}
	
	//used by the help threads to know what complex numbers to work with
	public ComplexNumber[][] getSet()
	{
		return cnSet;
	}
}
