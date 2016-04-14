package Levantamiento;

import java.util.ArrayList;

/**
 * Created by Sarah Rengel on 14-04-2016.
 */
public class AntennaJson {
  //  private String idantena;
    private ArrayList<ArrayList<RegistroJson>> antenna;

    public ArrayList<ArrayList<RegistroJson>> getAntenna() {
        return antenna;
    }

    public void setAntenna(ArrayList<ArrayList<RegistroJson>> antenna) {
        this.antenna = antenna;
    }

    public AntennaJson() {

    }
}
