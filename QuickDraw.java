import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;



public final class QuickDraw {
	// color black
    public static final Color BLACK = Color.BLACK;
    
    
    //color blue
    public static final Color BLUE = Color.BLUE;

    
    //color cyan
    public static final Color CYAN = Color.CYAN;

    //color dark gray
    public static final Color DARK_GRAY = Color.DARK_GRAY;

   
    //color gray
    public static final Color GRAY = Color.GRAY;

    
    //The color green
    public static final Color GREEN  = Color.GREEN;

    //color light gray
    public static final Color LIGHT_GRAY = Color.LIGHT_GRAY;

   
    //color magenta
    public static final Color MAGENTA = Color.MAGENTA;

  
    //color orange
    public static final Color ORANGE = Color.ORANGE;

   
    //color pink
    public static final Color PINK = Color.PINK;

   
    //color red
    public static final Color RED = Color.RED;

    
    //color white.
    public static final Color WHITE = Color.WHITE;

   
    //color yellow.
    public static final Color YELLOW = Color.YELLOW;
    
    //default background color
    private static final Color DEFAULT_CLEAR_COLOR = WHITE;
    
    //default pen color
    private static final Color DEFAULT_PEN_COLOR   = BLACK;
    
    //current pen color 
    private static Color penColor;
    
    // default pen radius
    private static final double DEFAULT_PEN_RADIUS = 0.002;

    // current pen radius
    private static double penRadius;
    
    //default font
    private static final Font DEFAULT_FONT = new Font("SansSerif", Font.PLAIN, 16);

    //current font
    private static Font font;
    
    //default canvas size
    private static final int DEFAULT_SIZE = 512;
    private static int width  = DEFAULT_SIZE;
    private static int height = DEFAULT_SIZE;
    
    //coordinate
    private static final double DEFAULT_XMIN = 0.0;
    private static final double DEFAULT_XMAX = 1.0;
    private static final double DEFAULT_YMIN = 0.0;
    private static final double DEFAULT_YMAX = 1.0;
    private static double xmin, ymin, xmax, ymax;
    
    private static BufferedImage screenImage, bufferedImage;
    private static Graphics2D screen, bufferedScreen;

    // singleton pattern using private constructor
    private static QuickDraw qickdrw = new QuickDraw();

    // the frame for displaying the drawing
    private static JFrame frame;
    
    //used to control next draw time
    private static long nextDrawTime = -1;  
    
    //used to  whether show the draw immediately
    private static boolean deferred = false;
    
    private QuickDraw() {
    	
    }
    
    // static initializer
    static {
    	initialize();
    }
    
    private static void initialize() {
    	frame = new JFrame();
    	bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        screenImage  = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        bufferedScreen = bufferedImage.createGraphics();
        screen  = screenImage.createGraphics();
        bufferedScreen.setColor(DEFAULT_CLEAR_COLOR);
        bufferedScreen.fillRect(0, 0, width, height);
        setXscale();
        setYscale();
        setFont();
        setPenRadius();
        setPenColor();
        SetBackground();

        ImageIcon icon = new ImageIcon(screenImage);
        JLabel draw = new JLabel(icon);
        frame.setContentPane(draw);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Quick Draw");
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
    }
    
    /**
     * Sets the display area to be the default ranges (512 by 512 pixels)
     */
    public static void setCanvasSize() {
        setCanvasSize(DEFAULT_SIZE, DEFAULT_SIZE);
    }
    
    /**
     Sets the display area to be the specified ranges 
     *
     * @param  canvasWidth the width of the display in pixels
     * @param  canvasHeight the height of the display in pixels
     * @throws IllegalArgumentException if either {@code width} or
     *         {@code height} is non-positive
     */
    public static void setCanvasSize(int canvasWidth, int canvasHeight) {
        if (canvasWidth <= 0 || canvasHeight <= 0)
            throw new IllegalArgumentException("width and height cannot be non-positive");
        width = canvasWidth;
        height = canvasHeight;
        initialize();
    }
    
    /**
     * Set the user x coordinate to be the default ranges (0 - 1)
     */
    public static void setXscale() {
        setXscale(DEFAULT_XMIN, DEFAULT_XMAX);
    }

    /**
     * Set the user y coordinate to be the default ranges(0 - 1)
     */
    public static void setYscale() {
        setYscale(DEFAULT_YMIN, DEFAULT_YMAX);
    }

    /**
     * Set the user x coordinate to be the specified ranges
     *
     * @param  minX the minimum of the user x coordinate
     * @param  maxX the maximum of the user x coordinate
     * @throws IllegalArgumentException if {@code (maxX == minX)}
     */
    public static void setXscale(double minX, double maxX) {
        if (minX == maxX) throw new IllegalArgumentException("the the minimum and maximum values of X cannot be the same");
        xmin = minX;
        xmax = maxX;
    }

    /**
     * Set the user y coordinate to be the specified ranges
     *
     * @param  minY the minimum of the user x coordinate
     * @param  maxY the maximum of the user x coordinate
     * @throws IllegalArgumentException if {@code (maxY == minY)}
     */
    public static void setYscale(double minY, double maxY) {
        if (minY == maxY) throw new IllegalArgumentException("the the minimum and maximum values of Y cannot be the same");
        ymin = minY; 
        ymax = maxY ;
    }

    
    // helper functions that switch between user coordinates and actual coordinates
    private static double  scaleX(double x) { 
    	return width  * (x - xmin) / (xmax - xmin); 
    }
    
    private static double  scaleY(double y) { 
    	return height * (ymax - y) / (ymax - ymin);
    }
    
    private static double factorX(double w) { 
    	return w * width  / Math.abs(xmax - xmin);  
    }
    
    private static double factorY(double h) { 
    	return h * height / Math.abs(ymax - ymin);  
    }
    
    /**
     * set the background to the default color (white).
     */
    public static void SetBackground() {
        SetBackground(DEFAULT_CLEAR_COLOR);
    }

    /**
     * set the background to the specified color.
     *
     * @param color the color to set the background
     */
    public static void SetBackground(Color color) {
        bufferedScreen.setColor(color);
        bufferedScreen.fillRect(0, 0, width, height);
        bufferedScreen.setColor(penColor);
        draw();
    }

    /**
     * Returns the current pen radius.
     *
     * @return the value of the pen radius
     */
    public static double getPenRadius() {
        return penRadius;
    }

    /**
     * Set the pen size to the default value
     */
    public static void setPenRadius() {
        setPenRadius(DEFAULT_PEN_RADIUS);
    }

    /**
     * Set the pen size to the specified size.
     * @param  radius the size of the pen in radius
     * @throws IllegalArgumentException if {@code radius} is negative
     */
    public static void setPenRadius(double radius) {
        if (radius < 0) throw new IllegalArgumentException("pen radius cannot be negative");
        penRadius = radius;
        float scaledPenRadius = (float) (radius * DEFAULT_SIZE);
        BasicStroke stroke = new BasicStroke(scaledPenRadius, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        bufferedScreen.setStroke(stroke);
    }

    /**
     * Returns the current pen color.
     *
     * @return the current pen color
     */
    public static Color getPenColor() {
        return penColor;
    }

    /**
     * Set the pen color to the default color
     */
    public static void setPenColor() {
        setPenColor(DEFAULT_PEN_COLOR);
    }

    /**
     * Sets the pen color to the specified color.
     * @param color the color to make the pen
     */
    public static void setPenColor(Color color) {
        if (color == null) throw new NullPointerException();
        penColor = color;
        bufferedScreen.setColor(penColor);
    }

    /**
     * Set the pen color to the specified RGB value
     *
     * @param  red the amount of red (between 0 and 255)
     * @param  green the amount of green (between 0 and 255)
     * @param  blue the amount of blue (between 0 and 255)
     * @throws IllegalArgumentException if {@code red}, {@code green},
     *         or {@code blue} is not the ranges
     */
    public static void setPenColor(int red, int green, int blue) {
        if (red   < 0 || red   >= 256) throw new IllegalArgumentException("amount of red must be between 0 and 255");
        if (green < 0 || green >= 256) throw new IllegalArgumentException("amount of green must be between 0 and 255");
        if (blue  < 0 || blue  >= 256) throw new IllegalArgumentException("amount of blue must be between 0 and 255");
        setPenColor(new Color(red, green, blue));
    }

    /**
     * Returns the current font.
     *
     * @return the current font
     */
    public static Font getFont() {
        return font;
    }

    /**
     * Set the font to the default font
     */
    public static void setFont() {
        setFont(DEFAULT_FONT);
    }

    /**
     * Set the font to the specified value.
     *
     * @param font the font
     */
    public static void setFont(Font font) {
        if (font == null) throw new NullPointerException();
        QuickDraw.font = font;
    }

    /**
     * Draw a line in the specified user coordinates
     *
     * @param  x0 user x coordinate of start point
     * @param  y0 user y coordinate of start point
     * @param  x1 user x coordinate of end point
     * @param  y1 user y coordinate of end point
     */
    public static void line(double x0, double y0, double x1, double y1) {
       bufferedScreen.draw(new Line2D.Double(scaleX(x0), scaleY(y0), scaleX(x1), scaleY(y1)));
       draw();
    }

    
    /**
     * Draw a circle centered at (x,y)
     *
     * @param  x user x coordinate of center 
     * @param  y user y coordinate of center 
     * @param  radius the radius of the circle
     * @throws IllegalArgumentException if {@code radius} is negative
     */
    public static void circle(double x, double y, double radius) {
        if (radius < 0) throw new IllegalArgumentException("radius cannot be negative");
        bufferedScreen.draw(new Ellipse2D.Double(scaleX(x) - factorX(2*radius)/2, scaleY(y) - factorY(2*radius)/2, factorX(2*radius), factorY(2*radius)));
        draw();
    }

    /**
     * Draw a filled circle centered at (x,y)
     *
     * @param  x user x coordinate of center 
     * @param  y user y coordinate of center 
     * @param  radius the radius of the circle
     * @throws IllegalArgumentException if {@code radius} is negative
     */
    public static void filledCircle(double x, double y, double radius) {
        if (radius < 0) throw new IllegalArgumentException("radius cannot be negative");
        bufferedScreen.fill(new Ellipse2D.Double(scaleX(x) - factorX(2*radius)/2, scaleY(y) - factorY(2*radius)/2, factorX(2*radius), factorY(2*radius)));
        draw();
  
    }

    /**
     * Draw a square centered at (x,y) with double length of the specified value
     *
     * @param  x user x coordinate of center 
     * @param  y user y coordinate of center 
     * @param  length half the length of the square
     * @throws IllegalArgumentException if {@code length} is negative
     */
    public static void square(double x, double y, double length) {
        if (length < 0) throw new IllegalArgumentException("half length cannot be negative");
        bufferedScreen.draw(new Rectangle2D.Double(scaleX(x) - factorX(2*length)/2, scaleY(y) - factorY(2*length)/2, factorX(2*length), factorY(2*length)));
        draw();
    }

    /**
     * Draw a filled square centered at (x,y) with double length
     *
     * @param  x user x coordinate of center 
     * @param  y user y coordinate of center 
     * @param  length half the length of the square
     * @throws IllegalArgumentException if {@code length} is negative
     */
    public static void filledSquare(double x, double y, double length) {
    	 if (length < 0) throw new IllegalArgumentException("half length cannot be negative");
         bufferedScreen.fill(new Rectangle2D.Double(scaleX(x) - factorX(2*length)/2, scaleY(y) - factorY(2*length)/2, factorX(2*length), factorY(2*length)));
         draw();
    }


    /**
     * Draw a rectangle centered at (x,y) with double length of the specified values
     *
     * @param  x user x coordinate of center 
     * @param  y user y coordinate of center 
     * @param  width half of the width 
     * @param  height half of the height
     * @throws IllegalArgumentException if either {@code width} or {@code height} is negative
     */
    public static void rectangle(double x, double y, double width, double height) {
        if (width  < 0 || height < 0) throw new IllegalArgumentException("cannot be negative");
        bufferedScreen.draw(new Rectangle2D.Double(scaleX(x) - factorX(2*width)/2, scaleY(y) - factorY(2*height)/2, factorX(2*width), factorY(2*height)));
        draw();
    }

    /**
     * Draw a filled rectangle centered at (x,y) with double length of the specified values
     *
     * @param  x user x coordinate of center 
     * @param  y user y coordinate of center 
     * @param  width half of the width 
     * @param  height half of the height
     * @throws IllegalArgumentException if either {@code width} or {@code height} is negative
     */
    public static void filledRectangle(double x, double y, double width, double height) {
        if (width  < 0 || height < 0) throw new IllegalArgumentException("cannot be negative");
        bufferedScreen.fill(new Rectangle2D.Double(scaleX(x) - factorX(2*width)/2, scaleY(y) - factorY(2*height)/2, factorX(2*width), factorY(2*height)));
        draw();
    }
    
    /**
     * draw the text string centered at (x,y)
     *
     * @param  x user x coordinate of center 
     * @param  y user y coordinate of center 
     * @param  text the string of the text
     */
    public static void text(double x, double y, String text) {
        if (text == null) throw new NullPointerException();
        bufferedScreen.setFont(font);
        bufferedScreen.drawString(text, (float) (scaleX(x) - bufferedScreen.getFontMetrics().stringWidth(text)/2.0), (float) (scaleY(y) + bufferedScreen.getFontMetrics().getDescent()));
        draw();
    }
    
    /**
     * Pause for the specified time in milliseconds
     * @param t number of milliseconds
     */
    public static void pause(int t) {
    	
        long CurrentTime = System.currentTimeMillis();
        nextDrawTime = CurrentTime + t;
        if (CurrentTime < nextDrawTime) {
            try {
                Thread.sleep(t);
            }
            catch (InterruptedException e) {
                System.out.println("Error sleeping");
            }
        }
    }
    
    /**
     * displays the drawing
     */
    public static void show() {
    	screen.drawImage(bufferedImage, 0, 0, null);
        frame.repaint();
    }
    
   // show the drawing is deferred is false
    private static void draw() {
        if (!deferred) show();
    }

    /**
     * Enable defer
     */
    public static void enableDefer() {
        deferred = true;
    }

    /**
     * Disable defer
     */
    public static void disableDefer() {
        deferred = false;
    }

    public static void main(String[] args) {	
        QuickDraw.square(.4, .5, .2);
        QuickDraw.filledSquare(.1, .1, .3);
        QuickDraw.setPenColor(QuickDraw.BLACK);
        QuickDraw.text(0.9, 0.9, "Welcome"); 
        
    }
}
