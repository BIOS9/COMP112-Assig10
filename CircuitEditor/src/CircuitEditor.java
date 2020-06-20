// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP112 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP112 - 2018T1, Assignment 10
 * Name: Matthew Corfiatis
 * Username: CorfiaMatt
 * ID: 300447277
 */

import ecs100.*;

import javax.swing.*;
import java.util.*;
import java.io.*;

/*
 *     Instructions:
 *       To select an existing component: click on it.
 *       To select an existing wire: drag between the components connecting it
 *       To add a new component:
 *        - make sure the appropriate tool is selected, then
 *        - click the mouse at an empty space where the component should be.
 *        - The new component will automatically become the selected component.
 *       To move a component:
 *        - drag it to the new position (must be empty space).
 *        - The moved component will automatically become the selected component.
 *       To modify the selected component
 *        - to rotate 90 degrees: click on the rotate button,
 *        - to add or change the label: enter a value in the label textfield, 
 *        - to delete it: click the delete button.
 *       
 *       To add a wire:
 *        - drag the mouse from one component to another.
 *          (Note: A wire must connect two components.)
 *       To delete a wire: click on it.
 */

public class CircuitEditor{
    public static final int WIRE_WIDTH = 2;

    boolean saved = true; //Bool indicating whether the current drawing has been saved
    ArrayList<Component> components = new ArrayList<>(); //List of components in the drawing
    ArrayList<Wire> wires = new ArrayList<>(); //List of wires in the drawing
    String awaitingComponent = ""; //String value that holds the component to be placed on the next mouse click
    Wire temporaryWire = null; //Var to store any temporary wire that is currently being created by dragging the mouse between components

    //Constructor
    public CircuitEditor(){
        this.setupGUI();
    }

    /** Sets up the user interface - mouselistener, buttons, and textField */
    public void setupGUI(){
        /*# YOUR CODE HERE */
        UI.addButton("Clear", this::clearCircuit);
        UI.addButton("Add resistor", ()->addComponent("resistor"));
        UI.addButton("Add capacitor", ()->addComponent("capacitor"));
        UI.addButton("Add source", ()->addComponent("source"));
        UI.addButton("Add connector", ()->addComponent("connector"));
        UI.addTextField("Label", this::doLabel);
        UI.addButton("Rotate", this::doRotate);
        UI.addButton("Delete", this::doDelete);
        UI.addButton("Save", this::saveDrawing);
        UI.addButton("Load", this::loadDrawing);

        UI.setLineWidth(WIRE_WIDTH);
        UI.setDivider(0.0);  // Hide the text area.
        UI.setMouseListener(this::doMouse);
        UI.setMouseMotionListener(this::doMouse);
    }

    // Methods to respond to the buttons (and textfield)
    /*# YOUR CODE HERE */

    /**
     * Clear the diagram and reset everything.
     */
    public void clearCircuit(){
        if(!saved && JOptionPane.showConfirmDialog(null, "Your circuit is not saved, do you still want to clear?") != JOptionPane.YES_OPTION) //Show save confirm dialogue
           return;

        wires.clear();
        components.clear();
        redraw();
        saved = true;
    }

    public void addComponent(String type)
    {
        awaitingComponent = type; //Set the next component to be placed as the requested component
        UI.printMessage("Click where to place the " + type);
        UI.getFrame().setCursor(java.awt.Cursor.CROSSHAIR_CURSOR); //set the crosshair to a cursor
    }

    /** Delete the selected component (if there is one)
     *  along with any wires that are connected to it, or
     *  the selected wire
     */
    public void doDelete(){
        ArrayList<Component> foundC = new ArrayList<>(); //Array to store components to remove from the working array, needed to we dont cause iteration exception
        ArrayList<Wire> foundW = new ArrayList<>(); //Array to store wires to remove

        for (Component c : components) {
            if (c.isSelected()) //If the component is selected
            {
                foundC.add(c); //Mark the component for deletion
                for (Wire w : wires) //Find attached wires
                    if(w.getEnd(0) == c || w.getEnd(1) == c ) foundW.add(w); //Check if wire attaches to component
            }
        }

        for (Wire w : wires)
            if(w.isSelected()) foundW.add(w); //If wire is selected, mark it for deletion

        components.removeAll(foundC); //delete components
        wires.removeAll(foundW); //delete wires
        saved = false;
        this.redraw();
    }

    /** Rotate the selected component  between horizontal and vertical 
     *  Or, rotate the selected wire */
    public void doRotate(){
        for (Component c : components) //Iterate over components
            if(c.isSelected()) c.rotate(); //if the component is selected, rotate it
        for (Wire w : wires) //Iterate over wires
            if(w.isSelected()) w.rotate(); //if the wire is selected, rotate it
        this.redraw();
        saved = false;
    }

    /**
     * Respond to mouse events.
     * Designing all the different cases for the mouse is tricky!!! Plan it out first.
     */
    public void doMouse(String action, double x, double y) {
        switch (action)
        {
            case "clicked": //When the mouse has been clicked
                boolean selectDone = false; //Boolean to only allow one component to be selected
                for (Component c : components)
                {
                    if(!selectDone && c.on(x, y)) //if no components have been selected and the mouse is over this component
                    {
                        c.select();
                        selectDone = true;
                    }
                    else //Deselect all other components
                        c.deselect();
                }

                for(Wire w : wires) //deselect all wires
                    w.deselect();

                if(!selectDone) //If no components have been selected
                {
                    for(Wire w : wires) {
                        if(w.on(x, y)) { //If the mouse is on the wire
                            w.select(); //select the wire
                            break; //Exit the loop so only one wire gets selected
                        }
                    }
                }

                if(!awaitingComponent.equals("")) //If the program is waiting for a mouse click to place a component
                {
                    Component c = new Component(awaitingComponent, 0, 0); //Create component
                    c.moveTo(x - (c.getWidth() / 2), y - (c.getHeight() / 2)); //Move centre of component to cursor
                    components.add(c); //Add component to the frame
                    redraw(); //Redraw circuit
                    UI.printMessage("Placed " + awaitingComponent); //Print message to say component places
                    UI.getFrame().setCursor(java.awt.Cursor.DEFAULT_CURSOR); //Reset cursor
                    awaitingComponent = ""; //Reset awaiting component
                    saved = false;
                }
                redraw();
                break;
            case "released": //When the mouse is released
                for (Component c : components)
                    if(c.held) //If component is held down, release it
                    {
                        c.held = false;
                        saved = false;
                    }

                if(temporaryWire != null) //If there is a temporary wire
                {
                    for(Wire w : wires) //Deselect all wires
                        w.deselect();
                    for(Component c : components)
                    {
                        c.deselect();
                        if(temporaryWire.getX() == c.getCentreX() && temporaryWire.getY()  == c.getCentreY()) //If temporary wire end is on a component centre
                        {
                            temporaryWire.completeConnection(c); //Complete the wire connection
                            wires.add(temporaryWire); //Add the wire to the drawing
                            saved = false;
                        }
                    }
                    temporaryWire = null; //Remove temporary wire
                    redraw();
                }
                break;
            case "pressed": //When mouse is pressed
                boolean moving = false;
                Component lastOn = null;  //Component to attach one end of temporary wire to
                for (Component c : components)
                    if(c.on(x, y)) //if mouse is on component
                    {
                        lastOn = c;
                        if(c.isSelected()) {
                            c.setDragStart(x, y);
                            c.held = true;
                            moving = true;
                            break;
                        }
                    }
                if(!moving && lastOn != null)
                {
                    temporaryWire = new Wire(lastOn, x, y); //Create temporary wire
                }
                break;
            case "dragged": //When mouse is dragged
                for (Component c : components)
                    if(c.held) //If the component is held down by the mouse
                    {
                        c.dragMove(x, y); //Move the component to the new cursor position
                    }

                if(temporaryWire != null) //If there is a temporary wire
                {
                    Distance closest = getClosestComponent(x, y); //Find closest component and the distance to it
                    if(closest.distance < 100 && closest.component != temporaryWire.getEnd(0)) //If the distance is below a threshold, snap to the component
                        temporaryWire.temporaryMove(closest.component.getCentreX(), closest.component.getCentreY()); //Move the temporary end of the wire to the closest component
                    else
                        temporaryWire.temporaryMove(x, y); //Move the temporary end of the wire to the cursor
                }
                redraw();
                break;
        }
    }

    private void doLabel(String text)
    {
        for (Component c : components)
            if(c.isSelected()) //If component is selected, set the label
                c.setLabel(text);
        saved = false;
        redraw();
    }


    /** Redraws the diagram.
     *  First highlights the selected component (if any)
     *  Then redraws all the wires,
     *  Then redraws all the components
     */
    public void redraw(){
        UI.clearGraphics(); //Clear drawing
        for(Wire w : wires) //Iterate over the wires. Wires are drawn first so they dont draw over the component icons
            w.redraw();
        if(temporaryWire != null) //If the temporary wire is set, draw it enxt
            temporaryWire.redraw();
        for(Component c : components) //Iterate over components and draw them
            c.redraw();
    }

    /**
     * Gets component closest to point specified
     * @return The closest component
     */
    public Distance getClosestComponent(double x, double y)
    {
        Distance dist = new Distance(); //Create distance storage variable
        dist.distance = Integer.MAX_VALUE; //Set distance as max value so we can find smallest distance
        for(Component c : components)
        {
            double d = Math.sqrt(Math.pow(Math.abs(c.getX() - x), 2) + Math.pow(Math.abs(c.getY() - y), 2)); //Pythagoras to get relative distance
            if(dist.distance > d)
            {
                dist.distance = d;
                dist.component = c;
            }
        }
        return dist;
    }

    /**
     * Load a new drawing from a file.
     * Each line of the file has the type of component or wire, and the field values
     */
    public void loadDrawing(){
        if(!saved && JOptionPane.showConfirmDialog(null, "Your circuit is not saved, do you still want to load?") != JOptionPane.YES_OPTION) //Show not saved confirm dialogue
            return;
        try
        {
            JFileChooser chooser = new JFileChooser(); //File chooser UI
            if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) //if the user selected a file to load
            {
                components.clear(); //clear the components
                wires.clear(); //clear the wires
                File file = chooser.getSelectedFile(); //get the file selected by the user
                Scanner sc = new Scanner(file);
                while(sc.hasNext())
                {
                    Scanner lineSc = new Scanner(sc.nextLine()); //create scanner from each line in file
                    String type = lineSc.next();
                    if(type.equalsIgnoreCase("wire")) //If the line is a wire line
                    {
                        //read co-ordinates
                        double x1 = lineSc.nextDouble();
                        double y1 = lineSc.nextDouble();
                        double x2 = lineSc.nextDouble();
                        double y2 = lineSc.nextDouble();

                        Component c1 = null;
                        Component c2 = null;

                        //Find component that is at the end of the wire, and attach it to that
                        for(Component c : components)
                        {
                            if(c.getX() == x1 && c.getY() == y1)
                                c1 = c;
                            else if (c.getX() == x2 && c.getY() == y2)
                                c2 = c;
                        }
                        Wire w = new Wire(c1, c2);
                        w.deselect(); //Deselect the new wire
                        wires.add(w); //Add the wire to the list of wires
                    }
                    else //Line is a component line
                    {
                        Component c = new Component(type, lineSc.nextDouble(), lineSc.nextDouble()); //Create new component with the specified type and read the position co-ordinates from the file.
                        c.deselect(); //Deselect new component
                        if(lineSc.next().equalsIgnoreCase("vertical")) //Rotate the component if it is vertical
                            c.rotate();
                        String label = "";
                        while(lineSc.hasNext()) //If the line has more text indicating there is a label on the component
                            label += lineSc.next() + " "; //Get label
                        c.setLabel(label);
                        components.add(c);
                    }
                }
                sc.close();
            }
        }
        catch (IOException ex)
        {
            UI.println("Error loading drawing: " + ex);
        }
        saved = true;
        this.redraw();
    }

    /**
     * Save a drawing to a file.
     * Each line of the file has the type of component or wire, and the field values
     */
    public void saveDrawing(){
        try
        {
            JFileChooser chooser = new JFileChooser();
            if(chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) //If user selects file to save
            {
                File file = chooser.getSelectedFile(); //get file user selected
                PrintWriter pw = new PrintWriter(file); //open file as a writable object
                for(Component c : components)
                {
                    pw.write(c.getType() + " " + //Write component type
                            c.getX() + " " + //Write co-ordinates
                            c.getY() + " " +
                            (c.isHorizontal() ? "horizontal" : "vertical") + " " + //Write rotation
                            c.getLabel() + "\r\n"); //Write label and new line
                }
                for(Wire w : wires)
                {
                    Component[] ends = w.getEnds();
                    pw.write("wire " + //Write wire type
                            ends[0].getX() + " " + //Write wire end co-ordinates
                            ends[0].getY() + " " +
                            ends[1].getX() + " " +
                            ends[1].getY() + "\r\n"); //Write newline
                }
                pw.flush(); //Flush the write buffer
                pw.close(); //Close the file stream
                saved = true;
            }
        }
        catch (IOException ex)
        {
            UI.println("Error saving file: " + ex);
        }
    }


    // Main:  constructs a new CircuitEditor object
    public static void main(String[] arguments){
        new CircuitEditor();
    }   

}
