
//Model class for fetching data from Api


package com.example.mytax;

public class Kommune {
    private String Municipality;
    private String LocalTaxRate;

    public Kommune(String municipality, String localTaxRate) {
        Municipality = municipality;
        LocalTaxRate = localTaxRate;
    }

    public String getMunicipality() {
        return Municipality;
    }

    public String getLocalTaxRate() {
        return LocalTaxRate;
    }

    public String toString(){
        return this.Municipality;
    }
}
