package pe.cayro.pnpj.v2.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by David on 4/23/16.
 */
public class SpecialMoveDetail extends RealmObject {

    @PrimaryKey
    private String uuid;

    private String specialMoveUuid;
    private int productId;
    private int qty;
    private boolean active;
    private boolean sent;
    private Date createdAt;
    private Product product;
    private SpecialMove specialMove;



    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSpecialMoveUuid() {
        return specialMoveUuid;
    }

    public void setSpecialMoveUuid(String specialMoveUuid) {
        this.specialMoveUuid = specialMoveUuid;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public SpecialMove getSpecialMove() {
        return specialMove;
    }

    public void setSpecialMove(SpecialMove specialMove) {
        this.specialMove = specialMove;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
