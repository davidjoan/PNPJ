package pe.cayro.pnpj.v2.util;

/**
 * Created by David on 28/01/16.
 */
public class RucValidator {

    /**
     * @param ruc
     * @return
     */
    public boolean validate(String ruc){

        boolean result = false;

        if(ruc.length() > 11){
            return  false;
        }

        int dig01 = Integer.valueOf(ruc.substring(0,1))*5;
        int dig02 = Integer.valueOf(ruc.substring(1,2))*4;
        int dig03 = Integer.valueOf(ruc.substring(2,3))*3;
        int dig04 = Integer.valueOf(ruc.substring(3,4))*2;
        int dig05 = Integer.valueOf(ruc.substring(4,5))*7;
        int dig06 = Integer.valueOf(ruc.substring(5,6))*6;
        int dig07 = Integer.valueOf(ruc.substring(6,7))*5;
        int dig08 = Integer.valueOf(ruc.substring(7,8))*4;
        int dig09 = Integer.valueOf(ruc.substring(8,9))*3;
        int dig10 = Integer.valueOf(ruc.substring(9,10))*2;
        int dig11 = Integer.valueOf(ruc.substring(10,11));

        int suma = dig01 + dig02 + dig03 + dig04 + dig05 + dig06 + dig07 + dig08 + dig09 + dig10;
        int residuo =suma%11;
        int resta = 11-residuo;

        int digChk = 0;
        if(resta == 10){
            digChk = 0;
        }else if(resta == 11){
            digChk = 1;
        }else{
            digChk = resta;
        }

        if(dig11 == digChk){
            result = true;
        }else{
            result = false;
        }

        return result;
    }
}
