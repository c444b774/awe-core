
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
        name = "ReceivedMessageType"
    )
    public class ReceivedMessageType implements IASN1PreparedElement {        
        public enum EnumType {
            
            @ASN1EnumItem ( name = "activeSetUpdate", hasTag = true , tag = 0 )
            activeSetUpdate , 
            @ASN1EnumItem ( name = "cellChangeOrderFromUTRAN", hasTag = true , tag = 1 )
            cellChangeOrderFromUTRAN , 
            @ASN1EnumItem ( name = "cellUpdateConfirm", hasTag = true , tag = 2 )
            cellUpdateConfirm , 
            @ASN1EnumItem ( name = "counterCheck", hasTag = true , tag = 3 )
            counterCheck , 
            @ASN1EnumItem ( name = "downlinkDirectTransfer", hasTag = true , tag = 4 )
            downlinkDirectTransfer , 
            @ASN1EnumItem ( name = "interRATHandoverCommand", hasTag = true , tag = 5 )
            interRATHandoverCommand , 
            @ASN1EnumItem ( name = "measurementControl", hasTag = true , tag = 6 )
            measurementControl , 
            @ASN1EnumItem ( name = "pagingType2", hasTag = true , tag = 7 )
            pagingType2 , 
            @ASN1EnumItem ( name = "physicalChannelReconfiguration", hasTag = true , tag = 8 )
            physicalChannelReconfiguration , 
            @ASN1EnumItem ( name = "physicalSharedChannelAllocation", hasTag = true , tag = 9 )
            physicalSharedChannelAllocation , 
            @ASN1EnumItem ( name = "radioBearerReconfiguration", hasTag = true , tag = 10 )
            radioBearerReconfiguration , 
            @ASN1EnumItem ( name = "radioBearerRelease", hasTag = true , tag = 11 )
            radioBearerRelease , 
            @ASN1EnumItem ( name = "radioBearerSetup", hasTag = true , tag = 12 )
            radioBearerSetup , 
            @ASN1EnumItem ( name = "rrcConnectionRelease", hasTag = true , tag = 13 )
            rrcConnectionRelease , 
            @ASN1EnumItem ( name = "rrcConnectionReject", hasTag = true , tag = 14 )
            rrcConnectionReject , 
            @ASN1EnumItem ( name = "rrcConnectionSetup", hasTag = true , tag = 15 )
            rrcConnectionSetup , 
            @ASN1EnumItem ( name = "securityModeCommand", hasTag = true , tag = 16 )
            securityModeCommand , 
            @ASN1EnumItem ( name = "signallingConnectionRelease", hasTag = true , tag = 17 )
            signallingConnectionRelease , 
            @ASN1EnumItem ( name = "transportChannelReconfiguration", hasTag = true , tag = 18 )
            transportChannelReconfiguration , 
            @ASN1EnumItem ( name = "transportFormatCombinationControl", hasTag = true , tag = 19 )
            transportFormatCombinationControl , 
            @ASN1EnumItem ( name = "ueCapabilityEnquiry", hasTag = true , tag = 20 )
            ueCapabilityEnquiry , 
            @ASN1EnumItem ( name = "ueCapabilityInformationConfirm", hasTag = true , tag = 21 )
            ueCapabilityInformationConfirm , 
            @ASN1EnumItem ( name = "uplinkPhysicalChannelControl", hasTag = true , tag = 22 )
            uplinkPhysicalChannelControl , 
            @ASN1EnumItem ( name = "uraUpdateConfirm", hasTag = true , tag = 23 )
            uraUpdateConfirm , 
            @ASN1EnumItem ( name = "utranMobilityInformation", hasTag = true , tag = 24 )
            utranMobilityInformation , 
            @ASN1EnumItem ( name = "assistanceDataDelivery", hasTag = true , tag = 25 )
            assistanceDataDelivery , 
            @ASN1EnumItem ( name = "spare6", hasTag = true , tag = 26 )
            spare6 , 
            @ASN1EnumItem ( name = "spare5", hasTag = true , tag = 27 )
            spare5 , 
            @ASN1EnumItem ( name = "spare4", hasTag = true , tag = 28 )
            spare4 , 
            @ASN1EnumItem ( name = "spare3", hasTag = true , tag = 29 )
            spare3 , 
            @ASN1EnumItem ( name = "spare2", hasTag = true , tag = 30 )
            spare2 , 
            @ASN1EnumItem ( name = "spare1", hasTag = true , tag = 31 )
            spare1 , 
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

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(ReceivedMessageType.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            