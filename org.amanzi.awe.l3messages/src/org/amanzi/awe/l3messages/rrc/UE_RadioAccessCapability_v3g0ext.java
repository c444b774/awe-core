
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
    @ASN1Sequence ( name = "UE_RadioAccessCapability_v3g0ext", isSet = false )
    public class UE_RadioAccessCapability_v3g0ext implements IASN1PreparedElement {
            
        @ASN1Element ( name = "ue-PositioningCapabilityExt-v3g0", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private UE_PositioningCapabilityExt_v3g0 ue_PositioningCapabilityExt_v3g0 = null;
                
  
        
        public UE_PositioningCapabilityExt_v3g0 getUe_PositioningCapabilityExt_v3g0 () {
            return this.ue_PositioningCapabilityExt_v3g0;
        }

        

        public void setUe_PositioningCapabilityExt_v3g0 (UE_PositioningCapabilityExt_v3g0 value) {
            this.ue_PositioningCapabilityExt_v3g0 = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(UE_RadioAccessCapability_v3g0ext.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            