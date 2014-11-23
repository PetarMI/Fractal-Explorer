Important comments for each part and the extensions

!!!Main Method is within the Mandelbrot.java file. It is very simple and that is why it does not have its own class

Part Two
	- To change coordinates from text fields enter value and press enter
	- Maximum number of iterations user can choose is 2048. More iterations just bring extra deep zooming.

Part Five
	- When user wants to save an image he can either assign it a name through the text field above the julia display or leave the program to do that(programm assigns 'favourite1', 'favourite2', etc)
	- User can then choose from favourites through a combobox
	- Each time an image is saved it is also saved as a .png file in the directory of the project.

Part Six
	- Zooming in can be done from any direction (Rectangle is also implemented)

EXTENSIONS
	- Multithreading - two threads are used for calculating the iterations for fixed parts of the julia display. Each thread stops after it has calculated the part that it was given. Code is also provided in the form of a comment (4 lines) in the IterCounter class to make the number of threads 4. Possible extension of this extension is to let the user pick the number of threads.  

	- Burning Ship Fractal - User is provided with the option of choosing to display the Burning ship fractal (through a combobox) instead of the mandelbrot. It is a matter of a simple check in the paint method to know which formula to use.
 
 	- Live updating of the Julia set - As the user hovers with the cursor over the mandelbrot or burning ship the julia display is automatically refreshed. TO TURN ON AND OFF LIVE UPDATING SCROLL ONE TIME WITH THE MOUSE WHEEL. User might want to turn off live updating if he wants to save an image.
 
	- My idea - Zoom out feature - For each zoom that the user makes he can then zoom out to the previous coordinates.