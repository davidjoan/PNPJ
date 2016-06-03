package pe.cayro.pnpj.v2.model.report;

/**
 * Created by David on 4/17/16.
 */
public class InstitutionShare {

    private int id;
    private String code;
    private String name;
    private String address;
    private float porcday;
    private float porcweek;
    private float porcmonth;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getPorcday() {
        return porcday;
    }

    public void setPorcday(float porcday) {
        this.porcday = porcday;
    }

    public float getPorcweek() {
        return porcweek;
    }

    public void setPorcweek(float porcweek) {
        this.porcweek = porcweek;
    }

    public float getPorcmonth() {
        return porcmonth;
    }

    public void setPorcmonth(float porcmonth) {
        this.porcmonth = porcmonth;
    }
}
