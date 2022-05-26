package cl.felipebarriga.android.smartbandlogger;

public class caregiverManager {
    String number;
    String contactName;

    public caregiverManager(String contactName, String number) {
        this.contactName = contactName;
        this.number = number;

    }

    public caregiverManager(String data) {
        String[] dataSplitted = data.split(",");
        this.contactName = dataSplitted[0];
        this.number = dataSplitted[1];
    }


    public String getContactName() {
        return this.contactName;
    }

    public void setContactName(String name) {
        this.contactName = name;
    }

    public String getnumber() {
        return this.number;
    }

    public void setNumber(String pNumber) {
        this.number = pNumber;
    }

    public String caregiverToString(){
        return this.contactName + "," + this.number + ";";
    }

}
