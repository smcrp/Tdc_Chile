package Levantamiento;

/**
 * Created by Sarah Rengel on 29-03-2016.
 */
public class Question {

    private int id;
    private String name;
    private String type;
    private int idType;
    private int idRegistro;
    private String idQr;
    private int level;
    private String answer;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdQr() {
        return idQr;
    }

    public void setIdQr(String idQr) {
        this.idQr = idQr;
    }

    public int getIdType() {
        return idType;
    }

    public void setIdType(int idType) {
        this.idType = idType;
    }

    public int getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(int idRegistro) {
        this.idRegistro = idRegistro;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Question(int id, String name, String type, int idType) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.idType = idType;
    }

    public Question(int id, String name, String type, int idType, int idRegistro, int level, String answer) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.idType = idType;
        this.idRegistro = idRegistro;
        this.level = level;
        this.answer = answer;
    }

    public Question() {
    }
}
