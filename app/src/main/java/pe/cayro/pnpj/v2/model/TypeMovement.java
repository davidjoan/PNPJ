package pe.cayro.pnpj.v2.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by David on 4/23/16.
 * {"id":1,"name":"REPOSICI\u00d3N","type":"I"}
 */
public class TypeMovement extends RealmObject {

    @PrimaryKey
    private int id;
    private String name;
    private String type;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
