
public class ComplexNumber
{
	//real and imaginary parts of the complex number
	public double real;
	public double imaginary;
	
	//constructor creates new complex number by given real and imaginary parts
	public ComplexNumber(double r, double i)
	{
		this.real = r;
		this.imaginary = i;
	}
	
	//getter for the real part of the number
	public double getReal()
	{
		return this.real;
	}
	
	//getter for the imaginary part of the number
	public double getImaginary()
	{
		return this.imaginary;
	}
	
	//get the squared value of the complex number
	public ComplexNumber square()
	{
		ComplexNumber cn = new ComplexNumber(real*real - imaginary*imaginary, 2*real*imaginary);
		return cn;
	}
	
	//get the squared modulus of the number
	public double modSquare()
	{
		double ms = real*real + imaginary*imaginary;
		return ms;
	}
	
	//add a complex number to the current number
	public ComplexNumber add(ComplexNumber num)
	{
		ComplexNumber cn = new ComplexNumber(real + num.getReal(), imaginary + num.getImaginary());
		return cn;
	}
	
	//multiply the complex number by another complex number
	public ComplexNumber multpily(ComplexNumber num)
	{
		ComplexNumber cn;
		cn = new ComplexNumber(real*num.getReal() - imaginary*num.getImaginary(), real*num.getImaginary() + imaginary*num.getReal());
		return cn;
	}
	
	//return the complex number as a string. Used when outputting the current number that is clicked on the mandelbrot set
	public String toString()
	{
		String num = real + " + " + imaginary + "i";
		return 	num;	
	}
}
