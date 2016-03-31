package Levantamiento;

import android.renderscript.ScriptGroup;

import java.util.ArrayList;

/**
 * Created by Sarah Rengel on 29-03-2016.
 */
public class Question {

    private String id;
    private String name;
    private String type;
    private String idType;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public Question(String id, String name, String type, String idType) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.idType = idType;
    }

    public Question() {
    }
}
