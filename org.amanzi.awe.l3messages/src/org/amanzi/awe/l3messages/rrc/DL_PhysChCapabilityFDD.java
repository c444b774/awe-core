
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
    @ASN1Sequence ( name = "DL_PhysChCapabilityFDD", isSet = false )
    public class DL_PhysChCapabilityFDD implements IASN1PreparedElement {
            @ASN1Integer( name = "" )
    @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 8L 
		
	   )
	   
        @ASN1Element ( name = "maxNoDPCH-PDSCH-Codes", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private Integer maxNoDPCH_PDSCH_Codes = null;
                
  
        @ASN1Element ( name = "maxNoPhysChBitsReceived", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private MaxNoPhysChBitsReceived maxNoPhysChBitsReceived = null;
                
  @ASN1Boolean( name = "" )
    
        @ASN1Element ( name = "supportForSF-512", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private Boolean supportForSF_512 = null;
                
  @ASN1Boolean( name = "" )
    
        @ASN1Element ( name = "dummy", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private Boolean dummy = null;
                
  
        @ASN1Element ( name = "dummy2", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private SimultaneousSCCPCH_DPCH_Reception dummy2 = null;
                
  
        
        public Integer getMaxNoDPCH_PDSCH_Codes () {
            return this.maxNoDPCH_PDSCH_Codes;
        }

        

        public void setMaxNoDPCH_PDSCH_Codes (Integer value) {
            this.maxNoDPCH_PDSCH_Codes = value;
        }
        
  
        
        public MaxNoPhysChBitsReceived getMaxNoPhysChBitsReceived () {
            return this.maxNoPhysChBitsReceived;
        }

        

        public void setMaxNoPhysChBitsReceived (MaxNoPhysChBitsReceived value) {
            this.maxNoPhysChBitsReceived = value;
        }
        
  
        
        public Boolean getSupportForSF_512 () {
            return this.supportForSF_512;
        }

        

        public void setSupportForSF_512 (Boolean value) {
            this.supportForSF_512 = value;
        }
        
  
        
        public Boolean getDummy () {
            return this.dummy;
        }

        

        public void setDummy (Boolean value) {
            this.dummy = value;
        }
        
  
        
        public SimultaneousSCCPCH_DPCH_Reception getDummy2 () {
            return this.dummy2;
        }

        

        public void setDummy2 (SimultaneousSCCPCH_DPCH_Reception value) {
            this.dummy2 = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(DL_PhysChCapabilityFDD.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            