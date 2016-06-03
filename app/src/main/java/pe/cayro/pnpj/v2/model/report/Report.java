package pe.cayro.pnpj.v2.model.report;

/**
 * Created by David on 4/18/16.
 */
public class Report {
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public Report(int id, String name) {
        this.id = id;
        this.name = name;
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
}
