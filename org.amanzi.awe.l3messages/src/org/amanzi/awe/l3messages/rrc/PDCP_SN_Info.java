
package org.amanzi.awe.l3messages.rrc;
//
// This file was generated by the BinaryNotes compiler.
// See http://bnotes.sourceforge.net 
// Any modifications to this file will be lost upon recompilation of the source ASN.1. 
//

import org.bn.*;
import org.bn.annotations.*;
import org.bn.annotations.constraints.*;
import org.bn.coders.*;
import org.bn.types.*;




    @ASN1PreparedElement
    @ASN1BoxedType ( name = "PDCP_SN_Info" )
    public class PDCP_SN_Info implements IASN1PreparedElement {
    
            @ASN1Integer( name = "PDCP-SN-Info" )
            @ASN1ValueRangeConstraint ( 
		
		min = 0L, 
		
		max = 65535L 
		
	   )
	   
            private Integer value;
            
            public PDCP_SN_Info() {
            }

            public PDCP_SN_Info(Integer value) {
                this.value = value;
            }
            
            public void setValue(Integer value) {
                this.value = value;
            }
            
            public Integer getValue() {
                return this.value;
            }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(PDCP_SN_Info.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            