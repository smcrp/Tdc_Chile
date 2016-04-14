package Levantamiento;

import java.util.ArrayList;

/**
 * Created by Sarah Rengel on 14-04-2016.
 */
public class ProductsJson {
    private String idproduct;
    private ArrayList<AnswerJson> answers;

    public String getIdproduct() {
        return idproduct;
    }

    public void setIdproduct(String idproduct) {
        this.idproduct = idproduct;
    }

    public ArrayList<AnswerJson> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<AnswerJson> answers) {
        this.answers = answers;
    }

    public ProductsJson() {
    }
}
