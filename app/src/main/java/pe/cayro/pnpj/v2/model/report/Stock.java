package pe.cayro.pnpj.v2.model.report;

/**
 * Created by David on 4/23/16.
 * {"id":1,"code":"1285","name":"MM- INCORIL 60 MG. COMPR. X 5","stock":"150"}
 */
public class Stock {

    private int id;
    private String code;
    private String name;
    private int stock;

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

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
