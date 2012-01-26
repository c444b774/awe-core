
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
    @ASN1BoxedType ( name = "CompressedModeMeasCapabFDDList_ext" )
    public class CompressedModeMeasCapabFDDList_ext implements IASN1PreparedElement {
                
            @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 8L 
		
	   )
	   
            @ASN1SequenceOf( name = "CompressedModeMeasCapabFDDList-ext" , isSetOf = false)
	    private java.util.Collection<CompressedModeMeasCapabFDD_ext> value = null; 
    
            public CompressedModeMeasCapabFDDList_ext () {
            }
        
            public CompressedModeMeasCapabFDDList_ext ( java.util.Collection<CompressedModeMeasCapabFDD_ext> value ) {
                setValue(value);
            }
                        
            public void setValue(java.util.Collection<CompressedModeMeasCapabFDD_ext> value) {
                this.value = value;
            }
            
            public java.util.Collection<CompressedModeMeasCapabFDD_ext> getValue() {
                return this.value;
            }            
            
            public void initValue() {
                setValue(new java.util.LinkedList<CompressedModeMeasCapabFDD_ext>()); 
            }
            
            public void add(CompressedModeMeasCapabFDD_ext item) {
                value.add(item);
            }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(CompressedModeMeasCapabFDDList_ext.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            