
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
    @ASN1BoxedType ( name = "InterFreqCellList_LCR_r4_ext" )
    public class InterFreqCellList_LCR_r4_ext implements IASN1PreparedElement {
                
            @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 8L 
		
	   )
	   
            @ASN1SequenceOf( name = "InterFreqCellList-LCR-r4-ext" , isSetOf = false)
	    private java.util.Collection<InterFreqCell_LCR_r4> value = null; 
    
            public InterFreqCellList_LCR_r4_ext () {
            }
        
            public InterFreqCellList_LCR_r4_ext ( java.util.Collection<InterFreqCell_LCR_r4> value ) {
                setValue(value);
            }
                        
            public void setValue(java.util.Collection<InterFreqCell_LCR_r4> value) {
                this.value = value;
            }
            
            public java.util.Collection<InterFreqCell_LCR_r4> getValue() {
                return this.value;
            }            
            
            public void initValue() {
                setValue(new java.util.LinkedList<InterFreqCell_LCR_r4>()); 
            }
            
            public void add(InterFreqCell_LCR_r4 item) {
                value.add(item);
            }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(InterFreqCellList_LCR_r4_ext.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            