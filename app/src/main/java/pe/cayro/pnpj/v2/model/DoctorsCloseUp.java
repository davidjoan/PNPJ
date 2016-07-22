package pe.cayro.pnpj.v2.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import pe.cayro.pnpj.v2.util.Constants;

/**
 * Created by dtataje on 16/07/2016.
 * {"id":40614,"name":"RAMOS RIVERA MARCOS AURELIO","lastname":null,"surname":null,"specialty":21}
 */
public class DoctorsCloseUp extends RealmObject {

    @PrimaryKey
    private String id;
    private String name;
    private String lastname;
    private String surname;
    @SerializedName(Constants.SPECIALTY_ID)
    private int specialtyId;
    private boolean fichero;

    private Specialty specialty;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getSpecialtyId() {
        return specialtyId;
    }

    public void setSpecialtyId(int specialtyId) {
        this.specialtyId = specialtyId;
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public void setSpecialty(Specialty specialty) {
        this.specialty = specialty;
    }

    public boolean isFichero() {
        return fichero;
    }

    public void setFichero(boolean fichero) {
        this.fichero = fichero;
    }
}
