package Levantamiento;

import java.util.ArrayList;

/**
 * Created by Sarah Rengel on 29-03-2016.
 */
public class Registro {

    private int id;
    private int idJson;
    private String name;
    private int status;
    private String create;

    private ArrayList<Question> questions;

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIdJson() {
        return idJson;
    }

    public void setIdJson(int idJson) {
        this.idJson = idJson;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreate() {
        return create;
    }

    public void setCreate(String create) {
        this.create = create;
    }

    public Registro(int id, String name, ArrayList<Question> questions) {
        this.id = id;
        this.name = name;
        this.questions = questions;
    }

    public Registro(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Registro() {
    }

}
