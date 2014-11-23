import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Mandelbrot 
{
	public static void main(String[] args)
	{
		MandelbrotFrame mfr = new MandelbrotFrame("Mandelbrot set vizualization");
		mfr.init();
	}
}

class MandelbrotFrame extends JFrame
{
	public MandelbrotFrame(String s)
	{
		super(s);
		this.setSize(1020, 610);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public void init()
	{
		//Setting specification for the Frame main panel
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout());
		this.setContentPane(mainPanel);
		
		//Panel for the mandelbrot display and the user options panel
		JPanel mp = new JPanel();
		mp.setLayout(new BorderLayout());
		
		//Panel for the julia set display and its user option panel
		JPanel jp = new JPanel();
		jp.setLayout(new BorderLayout());
		
		//Panel for Julia set image
		JuliaPanel juliaPanel = new JuliaPanel();
		
		//panel for the save image options associated with the julia panel
		FavouritesPanel favPanel = new FavouritesPanel(juliaPanel);
		favPanel.init();
		//add the julia image panel and the favourites panel to their parent panel
		jp.add(favPanel, BorderLayout.NORTH);
		jp.add(juliaPanel, BorderLayout.CENTER);
		
		//Panel for the Mandelbrot set image
		MandelbrotPanel mandelbrotPanel = new MandelbrotPanel();
		mandelbrotPanel.setBounds(0, 0, 500, 500);
		//attach a julia panel to the mandelbrot panel
		mandelbrotPanel.setJuliaPanel(juliaPanel);
		
		//create a panel for the user options that takes the mandelbrot panel as parameter
		UserPanel up = new UserPanel(mandelbrotPanel);
		up.init();
		//attach it to the mandelbrot panel
		mandelbrotPanel.setUserPanel(up);
		
		//and the mandelbrot and its user panel to their parent panel
		mp.add(mandelbrotPanel, BorderLayout.CENTER);
		mp.add(up, BorderLayout.NORTH);
		
		//in the main panel add both panels that contain the image panels and the user panels
		mainPanel.add(mp);
		mainPanel.add(jp);
		
		this.setVisible(true);
	}
	
}
