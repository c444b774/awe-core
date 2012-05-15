
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
    @ASN1Choice ( name = "UL_TrCH_Identity" )
    public class UL_TrCH_Identity implements IASN1PreparedElement {
            
        @ASN1Element ( name = "dch", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private TransportChannelIdentity dch = null;
                
  
        @ASN1Null ( name = "rachorcpch" ) 
    
        @ASN1Element ( name = "rachorcpch", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private org.bn.types.NullObject rachorcpch = null;
                
  
        @ASN1Element ( name = "usch", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private TransportChannelIdentity usch = null;
                
  
        
        public TransportChannelIdentity getDch () {
            return this.dch;
        }

        public boolean isDchSelected () {
            return this.dch != null;
        }

        private void setDch (TransportChannelIdentity value) {
            this.dch = value;
        }

        
        public void selectDch (TransportChannelIdentity value) {
            this.dch = value;
            
                    setRachorcpch(null);
                
                    setUsch(null);
                            
        }

        
  
        
        public org.bn.types.NullObject getRachorcpch () {
            return this.rachorcpch;
        }

        public boolean isRachorcpchSelected () {
            return this.rachorcpch != null;
        }

        private void setRachorcpch (org.bn.types.NullObject value) {
            this.rachorcpch = value;
        }

        
        public void selectRachorcpch () {
            selectRachorcpch (new org.bn.types.NullObject());
	}
	
        public void selectRachorcpch (org.bn.types.NullObject value) {
            this.rachorcpch = value;
            
                    setDch(null);
                
                    setUsch(null);
                            
        }

        
  
        
        public TransportChannelIdentity getUsch () {
            return this.usch;
        }

        public boolean isUschSelected () {
            return this.usch != null;
        }

        private void setUsch (TransportChannelIdentity value) {
            this.usch = value;
        }

        
        public void selectUsch (TransportChannelIdentity value) {
            this.usch = value;
            
                    setDch(null);
                
                    setRachorcpch(null);
                            
        }

        
  

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(UL_TrCH_Identity.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            