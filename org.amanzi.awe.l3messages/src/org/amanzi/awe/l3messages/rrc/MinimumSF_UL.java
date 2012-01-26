
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
    @ASN1Enum (
        name = "MinimumSF_UL"
    )
    public class MinimumSF_UL implements IASN1PreparedElement {        
        public enum EnumType {
            
            @ASN1EnumItem ( name = "sf1", hasTag = true , tag = 0 )
            sf1 , 
            @ASN1EnumItem ( name = "sf2", hasTag = true , tag = 1 )
            sf2 , 
            @ASN1EnumItem ( name = "sf4", hasTag = true , tag = 2 )
            sf4 , 
            @ASN1EnumItem ( name = "sf8", hasTag = true , tag = 3 )
            sf8 , 
            @ASN1EnumItem ( name = "dummy", hasTag = true , tag = 4 )
            dummy , 
        }
        
        private EnumType value;
        private Integer integerForm;
        
        public EnumType getValue() {
            return this.value;
        }
        
        public void setValue(EnumType value) {
            this.value = value;
        }
        
        public Integer getIntegerForm() {
            return integerForm;
        }
        
        public void setIntegerForm(Integer value) {
            integerForm = value;
        }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(MinimumSF_UL.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            