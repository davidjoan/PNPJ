package pe.cayro.pnpj.v2.model.report;

/**
 * Created by David on 4/17/16.
 */
public class MedicalSampleShare {

    private int id;
    private String name;
    private int cantday;
    private float shareday;
    private float porcday;
    private int cantweek;
    private float shareweek;
    private float porcweek;
    private int cantmonth;
    private float sharemonth;
    private float porcmonth;
    private float indicator;

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

    public int getCantday() {
        return cantday;
    }

    public void setCantday(int cantday) {
        this.cantday = cantday;
    }

    public float getShareday() {
        return shareday;
    }

    public void setShareday(float shareday) {
        this.shareday = shareday;
    }

    public float getPorcday() {
        return porcday;
    }

    public void setPorcday(float porcday) {
        this.porcday = porcday;
    }

    public int getCantweek() {
        return cantweek;
    }

    public void setCantweek(int cantweek) {
        this.cantweek = cantweek;
    }

    public float getShareweek() {
        return shareweek;
    }

    public void setShareweek(float shareweek) {
        this.shareweek = shareweek;
    }

    public float getPorcweek() {
        return porcweek;
    }

    public void setPorcweek(float porcweek) {
        this.porcweek = porcweek;
    }

    public int getCantmonth() {
        return cantmonth;
    }

    public void setCantmonth(int cantmonth) {
        this.cantmonth = cantmonth;
    }

    public float getSharemonth() {
        return sharemonth;
    }

    public void setSharemonth(float sharemonth) {
        this.sharemonth = sharemonth;
    }

    public float getPorcmonth() {
        return porcmonth;
    }

    public void setPorcmonth(float porcmonth) {
        this.porcmonth = porcmonth;
    }

    public float getIndicator() {
        return indicator;
    }

    public void setIndicator(float indicator) {
        this.indicator = indicator;
    }
}
