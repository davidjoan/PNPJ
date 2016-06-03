package pe.cayro.pnpj.v2.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import pe.cayro.pnpj.v2.util.Constants;

/**
 * Created by David on 7/01/16.
 */
public class Product extends RealmObject {

    /*
    * {"id":1,"code":1286,"name":"MM- INCORIL 60MG X 30 CP MM","qty_min":0,"qty_max":0,"bonus":2,
    * "qty_max_a":10,"qty_max_b":5,"stock_min":0,"stock_max":0}
    * */
    @PrimaryKey
    private int id;
    private String code;
    private String name;
    @SerializedName(Constants.QTY_MIN)
    private int qtyMin;
    @SerializedName(Constants.QTY_MAX)
    private int qtyMax;

    private int bonus;
    @SerializedName(Constants.QTY_MAX_A)
    private int qtyMaxA;
    @SerializedName(Constants.QTY_MAX_B)
    private int qtyMaxB;
    @SerializedName(Constants.STOCK_MIN)
    private int stockMin;
    @SerializedName(Constants.STOCK_MAX)
    private int stockMax;

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

    public int getQtyMin() {
        return qtyMin;
    }

    public void setQtyMin(int qtyMin) {
        this.qtyMin = qtyMin;
    }

    public int getQtyMax() {
        return qtyMax;
    }

    public void setQtyMax(int qtyMax) {
        this.qtyMax = qtyMax;
    }

    public int getBonus() {
        return bonus;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }

    public int getQtyMaxA() {
        return qtyMaxA;
    }

    public void setQtyMaxA(int qtyMaxA) {
        this.qtyMaxA = qtyMaxA;
    }

    public int getQtyMaxB() {
        return qtyMaxB;
    }

    public void setQtyMaxB(int qtyMaxB) {
        this.qtyMaxB = qtyMaxB;
    }

    public int getStockMin() {
        return stockMin;
    }

    public void setStockMin(int stockMin) {
        this.stockMin = stockMin;
    }

    public int getStockMax() {
        return stockMax;
    }

    public void setStockMax(int stockMax) {
        this.stockMax = stockMax;
    }
}
