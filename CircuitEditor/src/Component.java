// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP112 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP112 - 2018T1, Assignment 10
 * Name: Matthew Corfiatis
 * Username: CorfiaMatt
 * ID: 300447277
 */

import ecs100.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.util.*;

/**
 * Class representing components (resistors, capacitors, sources, or connectors).
 * Components have methods
 *   redraw the component
 *   highlight the component
 *   determine if a point (x,y) is on the component
 *   rotate the component (between horizontal and vertical)
 *   move the component to a new position
 */
public class Component{
    static HashMap<String, Image> iconCache = new HashMap<>(); //Store icons in memory once they have been fetched from disk
    static HashMap<String, String> iconUris = new HashMap<>(); //URI's of horizontal icons
    static { //Put values of the image file names in the icon uri map
        iconUris.put("resistor_vertical", "resistor-vertical.png");
        iconUris.put("capacitor_vertical", "capacitor-vertical.png");
        iconUris.put("connector_vertical", "connector-vertical.png");
        iconUris.put("source_vertical", "source-vertical.png");

        iconUris.put("resistor_horizontal", "resistor-horizontal.png");
        iconUris.put("capacitor_horizontal", "capacitor-horizontal.png");
        iconUris.put("connector_horizontal", "connector-horizontal.png");
        iconUris.put("source_horizontal", "source-horizontal.png");
    }

    // fields
    private String type;  // "resistor" or "capacitor" or "source" or "connector"
    private double x; //x co-ordinate of component
    private double y; //y co-ordinate of component
    private double dragX; //Position of cursor when drag was started, this is relative to the component top left corner
    private double dragY;
    private boolean horizontal = true;
    private boolean selected = true;
    public boolean held = false; //If the mouse is currently holding this component
    private String label = "";
    private double length;  // depends on the type
    private double width;

    public Component(String type, double x, double y){
        this.type = type;
        this.x = x;
        this.y = y;
    }

    public String getType()
    {
        return type;
    }

    /** Returns true if the point (x, y) is on top of the component */
    public boolean on(double x, double y){
        String key = type + "_" + (horizontal ? "horizontal" : "vertical"); //Icon selector
        CheckCache(key); //Ensure icon is in the icon cache
        double xDist = x - this.x; //Get relative horizontal distance of mouse from component
        double yDist = y - this.y; //Get relative vertical distance of mouse from component
        return (xDist > 0 && yDist > 0 && //Ensure mouse is far enough down and right to be on the component
                xDist < iconCache.get(key).getWidth(null) && //Ensure that the mouse is not over the right edge of the icon
                yDist < iconCache.get(key).getHeight(null)); //Ensure that the mouse is not over the bottom edge of the icon
    }

    /** Changes the position of the component to (x, y)
     */
    public void moveTo(double x, double y){
        this.x = x;
        this.y = y;
        redraw();
    }

    public void dragMove(double x, double y)
    {
        this.x = x - dragX;
        this.y = y - dragY;
    }

    public void setDragStart(double x, double y)
    {
        dragX = x - this.x;
        dragY = y - this.y;
    }

    public boolean isSelected()
    {
        return selected;
    }

    public void select()
    {
        selected = true;
        redraw();
    }

    public void deselect()
    {
        selected = false;
        redraw();
    }

    public double getCentreX() {
        String key = type + "_" + (horizontal ? "horizontal" : "vertical"); //Icon selector
        CheckCache(key); //Ensure cache contains icon
        return x + (iconCache.get(key).getWidth(null) / 2); //get x the centre of the component
    }

    public double getCentreY() {
        String key = type + "_" + (horizontal ? "horizontal" : "vertical"); //Icon selector
        CheckCache(key); //Ensure cache contains icon
        return y + (iconCache.get(key).getHeight(null) / 2);  //Get the y centre of the component
    }

    /** Draws the component on the graphics pane. */
    public void redraw(){
        String key = type + "_" + (horizontal ? "horizontal" : "vertical"); //Icon selector
        CheckCache(key); //Ensure cache contains icon
        Image img = iconCache.get(key); //Get the image from the cache
        UI.drawImage(img, x, y); //Draw image from cache
        if(selected) //if the component is selected
            UI.setColor(Color.red); //Make the border red
        else //Not selected
            UI.setColor(Color.white); //Erase any existing border
        UI.drawRect(x, y, img.getWidth(null), img.getHeight(null)); //Draw border
        UI.setColor(Color.black);
        UI.drawString(label, x + img.getWidth(null) + 5, y - 5); //Draw the label near the component
    }

    /**
     * Method to ensure that the specified icon is inside the icon cache, and if not, load it from disk and put it in the cache
     * @param key The selector for the icon that is to be checked
     */
    private void CheckCache(String key)
    {
        try {
            if (!iconCache.containsKey(key)) { //Check if icon has already be fetched from disk and stored in cache
                File f = new File(iconUris.get(key)); //If component is vertical, get vertical icon URI
                iconCache.put(key, ImageIO.read(f)); //Cache image in memory for future uses
            }
        }
        catch (IOException ex)
        {
            UI.println("Error loading component icon: " + ex);
        }
    }

    /**
     * Rotates the component between horizontal and vertical
     */
    public void rotate(){
        horizontal = !horizontal;
    }

    /** Add label to the component */
    public void setLabel(String str){
        label = str;
        redraw();
    }

    public String getLabel()
    {
        return label;
    }

    /** Get the position of the component */
    public double getX(){ return this.x; }
    public double getY(){ return this.y; }

    /** Get dimensions of the component */
    public double getWidth() {
        String key = type + "_" + (horizontal ? "horizontal" : "vertical"); //Icon selector
        CheckCache(key); //Ensure cache contains icon
        return iconCache.get(key).getWidth(null); //Get icon width
    }

    public double getHeight() {
        String key = type + "_" + (horizontal ? "horizontal" : "vertical"); //Icon selector
        CheckCache(key); //Ensure cache contains icon
        return iconCache.get(key).getHeight(null); //get icon height
    }


    /** Returns whether the component is horizontal or not */
    public boolean isHorizontal(){return this.horizontal;}

    /**
     * Returns a string description of the component in a form suitable for
     *  writing to a file in order to reconstruct the component later
     */
    public String toString(){
        /*# YOUR CODE HERE */
        return "";
    }

}

