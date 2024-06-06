package dk.dtu.compute.se.pisd.roborally.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class Course {
    @Expose
    public final int width;
    @Expose
    public final int height;

    @Expose
    private ArrayList<ArrayList<Space>> spaces;

    public Course(int width, int height) {
        this.width = width;
        this.height = height;
        spaces = new ArrayList<>();

    }


    public ArrayList<ArrayList<Space>> getSpaces() {
        return spaces;
    }

}
