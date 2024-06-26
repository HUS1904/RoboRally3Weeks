package dk.dtu.compute.se.pisd.roborally.model;

import com.google.gson.annotations.Expose;
import lombok.Getter;

import java.util.ArrayList;

public class Course {
    @Expose
    public final int width;
    @Expose
    public final int height;

    @Getter
    @Expose
    private ArrayList<ArrayList<Space>> spaces;

    public Course(int width, int height, ArrayList<ArrayList<Space>> spaces) {
        this.width = width;
        this.height = height;
        this.spaces = spaces;
    }

    public void setSpaces(ArrayList<ArrayList<Space>> spaces) {
        this.spaces = spaces;
    }
}
