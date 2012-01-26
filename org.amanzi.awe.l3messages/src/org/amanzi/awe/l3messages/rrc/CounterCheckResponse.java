
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
    @ASN1Sequence ( name = "CounterCheckResponse", isSet = false )
    public class CounterCheckResponse implements IASN1PreparedElement {
            
        @ASN1Element ( name = "rrc-TransactionIdentifier", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private RRC_TransactionIdentifier rrc_TransactionIdentifier = null;
                
  
        @ASN1Element ( name = "rb-COUNT-C-InformationList", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private RB_COUNT_C_InformationList rb_COUNT_C_InformationList = null;
                
  

       @ASN1PreparedElement
       @ASN1Sequence ( name = "laterNonCriticalExtensions" , isSet = false )
       public static class LaterNonCriticalExtensionsSequenceType implements IASN1PreparedElement {
                @ASN1BitString( name = "" )
    
        @ASN1Element ( name = "counterCheckResponse-r3-add-ext", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private BitString counterCheckResponse_r3_add_ext = null;
                
  

       @ASN1PreparedElement
       @ASN1Sequence ( name = "nonCriticalExtensions" , isSet = false )
       public static class NonCriticalExtensionsSequenceType implements IASN1PreparedElement {
                
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_NonCriticalExtensionsSequenceType;
        }

       private static IASN1PreparedElementData preparedData_NonCriticalExtensionsSequenceType = CoderFactory.getInstance().newPreparedElementData(NonCriticalExtensionsSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "nonCriticalExtensions", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private NonCriticalExtensionsSequenceType nonCriticalExtensions = null;
                
  
        
        public BitString getCounterCheckResponse_r3_add_ext () {
            return this.counterCheckResponse_r3_add_ext;
        }

        
        public boolean isCounterCheckResponse_r3_add_extPresent () {
            return this.counterCheckResponse_r3_add_ext != null;
        }
        

        public void setCounterCheckResponse_r3_add_ext (BitString value) {
            this.counterCheckResponse_r3_add_ext = value;
        }
        
  
        
        public NonCriticalExtensionsSequenceType getNonCriticalExtensions () {
            return this.nonCriticalExtensions;
        }

        
        public boolean isNonCriticalExtensionsPresent () {
            return this.nonCriticalExtensions != null;
        }
        

        public void setNonCriticalExtensions (NonCriticalExtensionsSequenceType value) {
            this.nonCriticalExtensions = value;
        }
        
  
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_LaterNonCriticalExtensionsSequenceType;
        }

       private static IASN1PreparedElementData preparedData_LaterNonCriticalExtensionsSequenceType = CoderFactory.getInstance().newPreparedElementData(LaterNonCriticalExtensionsSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "laterNonCriticalExtensions", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private LaterNonCriticalExtensionsSequenceType laterNonCriticalExtensions = null;
                
  
        
        public RRC_TransactionIdentifier getRrc_TransactionIdentifier () {
            return this.rrc_TransactionIdentifier;
        }

        

        public void setRrc_TransactionIdentifier (RRC_TransactionIdentifier value) {
            this.rrc_TransactionIdentifier = value;
        }
        
  
        
        public RB_COUNT_C_InformationList getRb_COUNT_C_InformationList () {
            return this.rb_COUNT_C_InformationList;
        }

        
        public boolean isRb_COUNT_C_InformationListPresent () {
            return this.rb_COUNT_C_InformationList != null;
        }
        

        public void setRb_COUNT_C_InformationList (RB_COUNT_C_InformationList value) {
            this.rb_COUNT_C_InformationList = value;
        }
        
  
        
        public LaterNonCriticalExtensionsSequenceType getLaterNonCriticalExtensions () {
            return this.laterNonCriticalExtensions;
        }

        
        public boolean isLaterNonCriticalExtensionsPresent () {
            return this.laterNonCriticalExtensions != null;
        }
        

        public void setLaterNonCriticalExtensions (LaterNonCriticalExtensionsSequenceType value) {
            this.laterNonCriticalExtensions = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(CounterCheckResponse.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            