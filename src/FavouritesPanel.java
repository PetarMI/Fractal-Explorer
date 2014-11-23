import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

//used to provide options to the user for the julia set
public class FavouritesPanel extends JPanel 
{
	//user chooses saved images from this list
	private JComboBox<FavouriteImg> fav = new JComboBox<FavouriteImg>();
	//used when user does not assign a name for the image that is saved
	private int favNumber = 1;
	//field where user enters name of the image to be saved with
	private JTextField favName;
	//julia panel attached to that panel
	private JuliaPanel jp;
	//combobox for the user to choose number of threads
	private JComboBox<Integer> threads = new JComboBox<Integer>(new Integer[]{1, 2, 4});
	
	//constructor taking a juliaPanel as a parameter
	public FavouritesPanel(JuliaPanel p)
	{
		jp = p;
		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(500, 72));
	}
	
	public void init()
	{
		//button that is used by user to save the image
		JButton save = new JButton("Save");
		favName = new JTextField(10);
		//add listeners to the button and the combobox
		save.addActionListener(new SaveListener());
		fav.addActionListener(new FavListener());
		//first item of the combobox does not contain an image
		fav.addItem(new FavouriteImg(null, "------"));
		JLabel noThreads = new JLabel("Number of threads");
		threads.addItemListener(new ThreadListener());
		//default number of threads is 2
		threads.setSelectedIndex(1);
		
		GridBagConstraints c = new GridBagConstraints();
		
		//arrange components in the panel
		c.gridx = 1;
		this.add(fav, c);
		c.gridx = 3;
		this.add(noThreads, c);
		c.gridy = 1;
		c.gridx = 0;
		this.add(favName, c);
		c.gridx = 2;
		this.add(save, c);
		c.gridx = 3;
		this.add(threads, c);
	}
	
	//used for saving an image
	class SaveListener implements ActionListener
	{		
		public void actionPerformed(ActionEvent e)
		{
			//save the image to a file too (for user convenience)
			File file;
			//set the name of the image to be saved as what the user assigned in the field
			String name = favName.getText() + ".png";
			//if the user does not assign a name a default is chosen
			if (name.equals(".png"))
			{
				//each time program is started default saving is done by favourite + number of saved image
				name = "favourite" + favNumber + ".png";
				favNumber++;
			}
			
			//save the image to a file
			file = new File(name);
			try
			{
				ImageIO.write(jp.getImage(), "png", file);
			}
			catch(IOException exc)
			{
				System.err.println(exc);
			}
			
			//add the image to the list of favourites in the combobox by passing the number that generated it and the name
			fav.addItem(new FavouriteImg(jp.getSelectedNumber(), name));
		}
	}
	
	//used by user to display one of the saved images
	class FavListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			FavouriteImg img = (FavouriteImg)fav.getSelectedItem();
			//set the number that generates the julia display to the one selected by the user and repaint
			jp.setSelectedNumber(img.getNumber());
			jp.repaint();
		} 
	}
	
	class ThreadListener implements ItemListener
	{
		public void itemStateChanged(ItemEvent e)
		{
			jp.setThreads((int)threads.getSelectedItem());
		}
	}
}
