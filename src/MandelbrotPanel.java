import java.awt.*;

import javax.swing.*;

import java.util.Vector;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Stack;

import javax.swing.JPanel;

class MandelbrotPanel extends JPanel
{
	//default number of iterations to check if number is non-divergent 
	private int noIterations = 512;
	//Panel is square and doesn't change so only one value for size
	private static final int SIZE = 500;
	//visible part of the complex plane coordinates and their default values
	private double xmin;
	private double ymin;
	private double xmax;
	private double ymax;
	//Difference of values of adjacent complex numbers 
	private double xdiff;
	private double ydiff;
	//Set of complex numbers visible for the specified coordinates. Each pixel corresponds to a complex number
	private ComplexNumber[][] cnSet = new ComplexNumber[SIZE][SIZE];
	//Number that is clicked or hovered over
	private ComplexNumber userSelectedPoint = null;
	//Text field displaying the clicked number
	private JTextField complexNum;
	//attached panel that contains the julia set. 
	private JuliaPanel attachedJulia;
	//attached panel for the user options
	private UserPanel attachedUserPanel;
	//array containing the colors to draw the mandelbrot
	private Color[] cols;
	//image that contains the mandelbrot set representation
	private BufferedImage image = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
	//variable indicating whether the user wants live updates of the julia set
	private boolean liveUpdate = true;
	//stack containing the coordinates of the complex plane before the zoom. Used when unzooming 
	private Stack<Double> oldCoords = new Stack<Double>();
	//store how many zoom operations have been made. Used when unzooming
	private int zoomDepth = 0;
	//indicating which fractal the user wants to be drawn. Default is mandelbrot
	private String fractal = "Mandelbrot";
	//variables connected to zooming
	private boolean dragging = false;
	//coordinates of the rectangle for the zoom
	private Point rectCoords = new Point();
	//width and height of the rectangle - determined in the 
	private int width, height;
	
	public MandelbrotPanel()
	{
		xmin = -2.0;
		ymin = -1.6;
		xmax = 2.0;
		ymax = 1.6;
		//calculate difference between adjacent numbers
		xdiff = (xmax - xmin)/SIZE;
		ydiff = (ymax - ymin)/SIZE;
		complexNum = new JTextField(30);
		
		//Fill the set with complex numbers that are in the current complex plane
		for(int i = 0; i < SIZE; i++)
		{
			for (int j = 0; j < SIZE; j++)
			{
				cnSet[i][j] = new ComplexNumber(xmin + i*xdiff, ymin + j*ydiff);
			}
		}
		
		//generate the colours that are going to be used for displaying the set
		generateColors();
		//add the text field with the current number
		this.add(complexNum);
		//listeners for the mandelbrot panel
		ZoomListener zoomListener = new ZoomListener();
		this.addMouseListener(zoomListener);
		this.addMouseMotionListener(zoomListener);
		this.addMouseWheelListener(new LiveUpdateListener());
	}
	
	//get the iterations that are required for number to become divergent 
	public int getIterations(ComplexNumber cn)
	{
		ComplexNumber z = new ComplexNumber(0, 0);
		int iterations;
		//do until z(noIterations) is reached
		for(iterations = 0; iterations < noIterations; iterations++)
		{
			if (z.modSquare() > 4 )
			{ 
				//if number diverges break from the method and return the iterations required
				break;
			}
			//depending on the fractal chosen by the user use the given formula (Works for mandelbrot and Burning ship for now)
			if (fractal == "Mandelbrot")
			{
				z = z.square().add(cn);
			}
			else
			{
				z = new ComplexNumber(Math.abs(z.getReal()), Math.abs(z.getImaginary())).square().add(cn);
			}
		}
		return iterations;
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D  g2d= (Graphics2D) g;
		//set color of the rectangle
		g2d.setColor(Color.BLUE);
		//color that the specific pixel is going to be in
		Color color;
		//if the user is not dragging the mouse (which draws a line over the mandelbrot set) display the set normally
		if (!dragging)
		{
			//for each pixel get the iterations for the number and paint pixel according to them
			for(int i = 0; i < SIZE; i++)
			{
				for (int j = 0; j < SIZE; j++)
				{
					int iterations = getIterations(cnSet[i][j]);
					if (iterations == noIterations)
					{
						color = Color.BLACK;
					}
					else
					{
						color = cols[iterations%cols.length];
					}
					image.setRGB(i, j, color.getRGB());
				}
			}
			g2d.drawImage(image, 0, 0, this);
		}
		else
		{
			//draw the old image
			g2d.drawImage(image, 0, 0, this);
			//draw the rectangle over the image
			g2d.drawRect(rectCoords.x, rectCoords.y, width, height);
		}
	}
	
	class ZoomListener implements MouseListener, MouseMotionListener
	{
		//store complex number that represents pixel that was pressed
		private double numberReal, numberImag;
		//store the coordinates of the event
		private int tempX, tempY;
		//store the starting point of the click/drag
		private int startX, startY;
		
		@Override
		//if the mouse is being dragged draw a line instead of a rectangle to help user see what part will be zoomed
		public void mouseDragged(MouseEvent e) 
		{
			//get the current coordinates of the mouse
			tempX = e.getX();
			tempY = e.getY();
			//set the coordinates of the rectangle 
			//Math is used to make the rectangle move in all directions
			rectCoords.x = Math.min(startX, tempX);
			rectCoords.y = Math.min(startY, tempY);
			//calculate the new width and height
            width = Math.abs(startX - tempX);
            height = Math.abs(startY - tempY);
			repaint();
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			//ensure that event is not outside the mandelbrot display
			if (e.getX() > 500 || e.getY() > 500)
			{
				return;
			}
			dragging = true;
			//save both the pixel that was clicked and the real and imaginary part of the its number
			numberReal = cnSet[e.getX()][e.getY()].getReal(); 
			numberImag = cnSet[e.getX()][e.getY()].getImaginary();
			startX = e.getX();
			startY = e.getY();
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			//ensure the event is not outside the mandelbrot display
			if (e.getX() > 500 || e.getY() > 500)
			{
				dragging = false;
				return;
			}
			
			//if for the press and release it is the same pixel (i.e there was a click) draw the new julia display
			if (startX == e.getX() && startY == e.getY())
			{
				//set the selected complex number that is used for drawing the julia set
				userSelectedPoint = cnSet[e.getX()][e.getY()];
				//show the complex number in the text field
				complexNum.setText(userSelectedPoint.toString());
				//pass the selected number to the julia set so that it knows how to draw 
				attachedJulia.setSelectedNumber(userSelectedPoint);
				attachedJulia.repaint();
				return;
			}
			
			//if the mouse has been dragged save the coordinates of the plane into a stack so they can be accessed in case of unzooming
			oldCoords.push(ymax);
			oldCoords.push(xmax);
			oldCoords.push(ymin);
			oldCoords.push(xmin);
			
			//get new coordinates after the drag
			xmax = Math.max(numberReal, cnSet[e.getX()][e.getY()].getReal());
			xmin = Math.min(numberReal, cnSet[e.getX()][e.getY()].getReal());
			ymax = Math.max(numberImag, cnSet[e.getX()][e.getY()].getImaginary());
			ymin = Math.min(numberImag, cnSet[e.getX()][e.getY()].getImaginary());

			dragging = false;
			//count the number of zooms
			zoomDepth++;
			//give the new coordinates to the mandelbrot display and redraw it
			changePlaneCoords(xmin, ymin, xmax, ymax);
			repaint();
			//change the coordinates that are displayed in the text fields
			attachedUserPanel.changeCoordinates(xmin, xmax, ymax, ymin);
		}
		
		//used for live updating
		public void mouseMoved(MouseEvent e) 
		{	
			//ensure live updating is on and the coordinates are not outside the display
			if (liveUpdate && e.getX() < 500 && e.getY() < 500)
			{
				//same operations as if there is a click on the point
				userSelectedPoint = cnSet[e.getX()][e.getY()];
				complexNum.setText(userSelectedPoint.toString());
				attachedJulia.setSelectedNumber(userSelectedPoint);
				attachedJulia.repaint();
			}
		}
		public void mouseClicked(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}	
	}
	
	//used so that user can switch on and off live updating
	class LiveUpdateListener implements MouseWheelListener
	{
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) 
		{
			//done so by scrolling the mouse one time
			liveUpdate = !liveUpdate;
		}
	}
	
	//helper method for setting new coordinates to the complex plane
	public void changePlaneCoords(double lx, double by, double rx, double uy)
	{
		this.xmin = lx;
		this.ymin = by;
		this.xmax = rx;
		this.ymax = uy;
		//calculate the new difference between adjacent numbers
		xdiff = (rx - lx)/SIZE;
		ydiff = (uy - by)/SIZE;
		
		//Fill the set with complex numbers that are in the current complex plane
		for(int i = 0; i < SIZE; i++)
		{
			for (int j = 0; j < SIZE; j++)
			{
				cnSet[i][j] = new ComplexNumber(xmin + i*xdiff, ymin + j*ydiff);
			}
		}
	}
	
	//generating the colors that the mandelbrot display is drawn in
	public Color[] generateColors()
	{
		cols = new Color[1024];
		for(int i = 0; i < 1024; i++)
		{
			cols[i] = Color.getHSBColor((float) i / (float) 100, 0.85f, 0.9f);
		}
		return cols;
	}
	
	//getters for the coordinates of the plane. Used by the text fields of the User Panel to know the current coordinates
	public Double getLeftX()
	{
		return xmin;
	}
	public Double getRightX()
	{
		return xmax;
	}
	public Double getTopY()
	{
		return ymax;
	}
	public Double getBottomY()
	{
		return ymin;
	}
	
	//set the julia panel that is attached to this mandelbrot
	public void setJuliaPanel(JuliaPanel jp)
	{
		this.attachedJulia = jp;
	}
	
	//set the user panel that is attached to the mandelbrot
	public void setUserPanel(UserPanel up)
	{
		this.attachedUserPanel = up;
	}
	
	//set a new amount for the iterations. Used by User Panel to notify this panel when user changes the amount
	public void setIteration(int n)
	{
		this.noIterations = n;
	}
	
	//used by the userPanel to check if the user can zoom out 
	public int getZoomDepth()
	{
		return this.zoomDepth;
	}
	
	//used by the user panel when the user zooms out
	public void decrementZoomDepth()
	{
		zoomDepth--;
	}
	
	//get the coordinates of all zooms
	public Stack<Double> getOldCoords()
	{
		return this.oldCoords;
	}
	
	//set the fractal to the one that user wants
	public void toggleFractal(String newFractal)
	{
		fractal = newFractal;
	}
}
