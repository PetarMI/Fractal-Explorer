import java.awt.Color;
import java.awt.image.*;
import java.util.concurrent.*;

//thread used to count the iterations for a number from a julia set 
public class IterCounter implements Callable<Integer>
{
	//default value for iterations
	private static final int NO_ITERATIONS = 500;
	//julia panel for which iterations are counted
	private JuliaPanel jp;
	//set of complex numbers
	private ComplexNumber[][] cnSet;
	//image to draw on
	private BufferedImage image;
	//colors that are used
	private Color[] cols;
	//indicate the part of the julia image to be calculated
	private int xmin;
	private int xmax;
	private int ymin;
	private int ymax;

	//constructor taking the coordinates as parameters
	public IterCounter(JuliaPanel panel, int i, int j, int k, int l)
	{
		this.jp = panel;
		cnSet = jp.getSet();
		image = jp.getImage();
		cols = this.generateColors();
		xmin = i;
		xmax = j;
		ymin = k;
		ymax = l;
	}
	
	@Override
	public Integer call()
	{
		Color color = null;
		
		//for each number from that part of the julia image count the iterations and set the appropriate color
		for(int i = xmin; i < xmax; i++)
		{
			for (int j = ymin; j < ymax; j++)
			{
				int iterations = getIterations(cnSet[i][j]);
				//if it didn't diverge color is black
				if (iterations == NO_ITERATIONS)
				{
					color = Color.BLACK;
				}
				else
				{
					color = cols[iterations];
				}
				image.setRGB(i, j, color.getRGB());
			}
		}
		return 0;
	}
	
	//count the iterations required for a number to diverge
	public int getIterations(ComplexNumber cn)
	{
		ComplexNumber z = cn;
		int iterations;
		//do this until it reaches the default value for the julia set
		for(iterations = 0; iterations < NO_ITERATIONS; iterations++)
		{
			//break if it diverges
			if (z.modSquare() > 4)
			{ 
				break;
			}
			z = z.square().add(jp.getSelectedNumber());
		}
		return iterations;
	}
	
	//generate the colors that are used for the drawing the image
	public Color[] generateColors()
	{
		cols = new Color[500];
		for(int i = 0; i < 500; i++)
		{
			cols[i] = Color.getHSBColor((float) i / (float) 100, 0.85f, 0.9f);
		}
		return cols;
	}
}
