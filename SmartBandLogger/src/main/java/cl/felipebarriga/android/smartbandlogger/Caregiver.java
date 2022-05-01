package cl.felipebarriga.android.smartbandlogger;

import java.io.Serializable;

public class Caregiver implements Serializable {

    public String name;
    public String number;
    public String ext;

    public Caregiver() {

    }

    public Caregiver(String data) {
        String[] dataSplitted = data.split(",");
        this.name = dataSplitted[0];
        this.number = dataSplitted[1];
        this.ext = dataSplitted[2];
    }

    public Caregiver(String name, String number, String ext) {
        this.name = name;
        this.number = number;
        this.ext = ext;
    }

    public String getname() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String pNumber) {
        this.number = pNumber;
    }

    public void setExt(String pExt) {
        this.ext = pExt;
    }

    public String getExt() {
        return this.ext;
    }

    public String caregiverToString(){
        return this.name + "," + this.number + "," + this.ext + ";";
    }
}
