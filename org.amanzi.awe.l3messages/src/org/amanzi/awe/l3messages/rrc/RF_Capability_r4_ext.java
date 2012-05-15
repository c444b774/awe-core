
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
    @ASN1Sequence ( name = "RF_Capability_r4_ext", isSet = false )
    public class RF_Capability_r4_ext implements IASN1PreparedElement {
            

       @ASN1PreparedElement
       @ASN1Sequence ( name = "tddRF_Capability" , isSet = false )
       public static class TddRF_CapabilitySequenceType implements IASN1PreparedElement {
                
        @ASN1Element ( name = "ue-PowerClass", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private UE_PowerClass ue_PowerClass = null;
                
  
        @ASN1Element ( name = "radioFrequencyBandTDDList", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private RadioFrequencyBandTDDList radioFrequencyBandTDDList = null;
                
  
        @ASN1Element ( name = "chipRateCapability", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private ChipRateCapability chipRateCapability = null;
                
  
        
        public UE_PowerClass getUe_PowerClass () {
            return this.ue_PowerClass;
        }

        

        public void setUe_PowerClass (UE_PowerClass value) {
            this.ue_PowerClass = value;
        }
        
  
        
        public RadioFrequencyBandTDDList getRadioFrequencyBandTDDList () {
            return this.radioFrequencyBandTDDList;
        }

        

        public void setRadioFrequencyBandTDDList (RadioFrequencyBandTDDList value) {
            this.radioFrequencyBandTDDList = value;
        }
        
  
        
        public ChipRateCapability getChipRateCapability () {
            return this.chipRateCapability;
        }

        

        public void setChipRateCapability (ChipRateCapability value) {
            this.chipRateCapability = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_TddRF_CapabilitySequenceType;
        }

       private static IASN1PreparedElementData preparedData_TddRF_CapabilitySequenceType = CoderFactory.getInstance().newPreparedElementData(TddRF_CapabilitySequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "tddRF_Capability", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private TddRF_CapabilitySequenceType tddRF_Capability = null;
                
  
        
        public TddRF_CapabilitySequenceType getTddRF_Capability () {
            return this.tddRF_Capability;
        }

        
        public boolean isTddRF_CapabilityPresent () {
            return this.tddRF_Capability != null;
        }
        

        public void setTddRF_Capability (TddRF_CapabilitySequenceType value) {
            this.tddRF_Capability = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(RF_Capability_r4_ext.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            