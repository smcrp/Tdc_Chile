package Levantamiento;

import java.util.ArrayList;

/**
 * Created by Sarah Rengel on 11-04-2016.
 */
public class Products {
    private String idproduct;
    private ArrayList<Question> questions;

    public Products(int id) {
    }

    public String getId() {
        return idproduct;
    }

    public void setId(String idproduct) {
        this.idproduct = idproduct;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public Products() {
    }
}
