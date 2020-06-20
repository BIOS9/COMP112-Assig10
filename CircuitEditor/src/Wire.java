// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP112 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP112 - 2018T1, Assignment 10
 * Name: Matthew Corfiatis
 * Username: CorfiaMatt
 * ID: 300447277
 */

import ecs100.*;
import java.awt.Color;

public class Wire {
    public static final double THRESHOLD = 3; // distance from the line that counts as "on"
    private Component[] components;   // array of the two components, one at each end of the wire
    private boolean selected = true; //Boolean indicating whether component is selected
    private boolean temporary = false; //If the wire only has one component and is being dragged to another.
    private double x = 0, y = 0; //Temporary position of the end of the wire
    // wire is drawn horizontal from 1st component then vertical to 2nd component

    public Wire(Component c1, Component c2){
        this.components = new Component[]{c1, c2};
    }
    public Wire(Component c1, double x, double y){ //Constructor for a temporary wite that only has one connection
        this.components = new Component[]{c1};
        temporary = true;
        this.x = x;
        this.y = y;
    }

    public void temporaryMove(double x, double y) //Move the free end of the temporary wire
    {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void completeConnection(Component c) //Completes connection turning temporary wire into a permanent wire
    {
        components = new Component[] { components[0], c }; //Set the second component
        temporary = false; //Set non temporary
        x = 0;
        y = 0;
    }

    /** Does this wire connect to the component */
    public boolean connectsTo(Component comp){
        for(Component c : components)
            if(c == comp)
                return true;
        return false;
    }

    public boolean isSelected()
    {
        return selected;
    }

    public void select()
    {
        selected = true;
    }

    public void deselect()
    {
        selected = false;
    }

    /** Is the point x,y on the wire.
     *  Provided to save you time on the geometry! */
    public boolean on(double x, double y){
        if(temporary) return false;
        double x1 = components[0].getCentreX();
        double y1 = components[0].getCentreY();
        double x2 = components[1].getCentreX();
        double y2 = components[1].getCentreY();
        // first check if it is past the ends of the line...
        if (x < Math.min(x1,x2)-THRESHOLD ||     
        x > Math.max(x1,x2)+THRESHOLD ||
        y < Math.min(y1,y2)-THRESHOLD ||
        y > Math.max(y1,y2)+THRESHOLD) {
            return false;
        }
        // then check the distance from the point to the line
        return (Math.abs(y-y1) < THRESHOLD || Math.abs(x-x2) < THRESHOLD);
    }

    /** "rotate" the wire by reversing the order of the components */
    public void rotate(){
        components = new Component[] { components[1], components[0] };

    }

    /** Draws the wire on the graphics pane.
     *  Horizontal out of the first component, vertical into the second component. */
    public void redraw(){
        //Get centre of both components
        double x1 = components[0].getX() + (components[0].getWidth() / 2);
        double y1 = components[0].getY() + (components[0].getHeight() / 2);
        double x2 = temporary ? x : (components[1].getX() + (components[1].getWidth() / 2));
        double y2 = temporary ? y : (components[1].getY() + (components[1].getHeight() / 2));
        if(temporary) //If the wire is temporary, make it green
            UI.setColor(Color.green);
        else if(selected) //If the wire is selected, make it red
            UI.setColor(Color.red);
        else //If the wire is deselected and not temporary, make it black
            UI.setColor(Color.black);
        UI.drawLine(x1, y1, x2, y1); //Draw the lines
        UI.drawLine(x2, y1, x2, y2);
    }

    public Component[] getEnds(){
        return new Component[]{this.components[0], this.components[1]};
    }

    public Component getEnd(int index) { return components[index]; }

    /** For debugging */ 
    public String toString(){
        return "wire " +this.components[0]+" to "+this.components[1];
    }

}
