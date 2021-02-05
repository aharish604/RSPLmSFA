package com.rspl.sf.msfa.priceUpdate;

import java.io.Serializable;

/**
 * Created by e10849 on 22-09-2017.
 */

public class PriceBean implements Serializable {

        private String master_brand;
        private String brand;
        private String BP_EX;
        private String BP_For="";
        private String WSP;
        private String RSP;



        public String getmaster_brand() {
            return master_brand;
        }

        public void setmaster_brand(String master_brandval) {
            master_brand = master_brandval;
        }





        public String getbrand() {
            return brand;
        }

        public void setbrand(String brandval) {
            brand = brandval;
        }



        public String getBP_EX() {
            return BP_EX;
        }

        public void setBP_EX(String BP_EXval) {
            BP_EX = BP_EXval;
        }



        public String getBP_For() {
            return BP_For;
        }

        public void setBP_For(String BP_Forval) {
            BP_For = BP_Forval;
        }



        public String getWSP() {
        return WSP;
    }

        public void setWSP(String WSPval) {
            WSP = WSPval;
    }




        public String getRSP() {
        return RSP;
    }

        public void setRSP(String RSPPval) {
        RSP = RSPPval;
    }

    }
