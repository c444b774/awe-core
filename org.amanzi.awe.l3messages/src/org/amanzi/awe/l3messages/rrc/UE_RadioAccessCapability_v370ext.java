
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
    @ASN1Sequence ( name = "UE_RadioAccessCapability_v370ext", isSet = false )
    public class UE_RadioAccessCapability_v370ext implements IASN1PreparedElement {
            
        @ASN1Element ( name = "ue-RadioAccessCapabBandFDDList", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private UE_RadioAccessCapabBandFDDList ue_RadioAccessCapabBandFDDList = null;
                
  
        
        public UE_RadioAccessCapabBandFDDList getUe_RadioAccessCapabBandFDDList () {
            return this.ue_RadioAccessCapabBandFDDList;
        }

        

        public void setUe_RadioAccessCapabBandFDDList (UE_RadioAccessCapabBandFDDList value) {
            this.ue_RadioAccessCapabBandFDDList = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(UE_RadioAccessCapability_v370ext.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            