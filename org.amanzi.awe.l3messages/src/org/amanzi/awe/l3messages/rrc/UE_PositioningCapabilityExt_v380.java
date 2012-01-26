
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
    @ASN1Sequence ( name = "UE_PositioningCapabilityExt_v380", isSet = false )
    public class UE_PositioningCapabilityExt_v380 implements IASN1PreparedElement {
            @ASN1Boolean( name = "" )
    
        @ASN1Element ( name = "rx-tx-TimeDifferenceType2Capable", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private Boolean rx_tx_TimeDifferenceType2Capable = null;
                
  
        
        public Boolean getRx_tx_TimeDifferenceType2Capable () {
            return this.rx_tx_TimeDifferenceType2Capable;
        }

        

        public void setRx_tx_TimeDifferenceType2Capable (Boolean value) {
            this.rx_tx_TimeDifferenceType2Capable = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(UE_PositioningCapabilityExt_v380.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            