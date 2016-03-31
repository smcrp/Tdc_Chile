package Levantamiento;

import java.util.ArrayList;

/**
 * Created by Sarah Rengel on 29-03-2016.
 */
public class Registro {

    private String id;
    private String name;

    private ArrayList<Question> questions;

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Registro(String id, String name, ArrayList<Question> questions) {
        this.id = id;
        this.name = name;
        this.questions = questions;
    }

    public Registro() {
    }
}
