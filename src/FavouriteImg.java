
//class that is used for saving an image
public class FavouriteImg 
{
	//complex number that the julia set was generated from 
	private ComplexNumber cn;
	//Name variable used for the toString method that is used for the combobox displaying the saved images
	private String name;
	
	//constructor taking the number and the name as parameters
	public FavouriteImg(ComplexNumber c, String name)
	{
		this.cn = c;
		this.name = name;
	}
	
	//used by the combobox when displaying the saved image
	public String toString()
	{
		return this.name;
	}
	
	//used by the combobox when the user chooses an image from the favourites. The number is passed and the saved julia set is generated from it
	public ComplexNumber getNumber()
	{
		return this.cn;
	}
}
