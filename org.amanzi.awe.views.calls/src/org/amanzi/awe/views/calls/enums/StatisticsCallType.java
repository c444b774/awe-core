/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package org.amanzi.awe.views.calls.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.amanzi.neo.core.enums.CallProperties.CallType;

/**
 * <p>
 * Enumeration of call types for get statistics.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public enum StatisticsCallType {
    
    /**
     * Second level statistics.
     */
    AGGREGATION_STATISTICS("Second level",null,55,2,AggregationStatisticsHeaders.SC1,
                                AggregationStatisticsHeaders.SC2_ZW2_AVG,
                                AggregationStatisticsHeaders.SC2_ZW2_MIN,
                                AggregationStatisticsHeaders.SC2_ZW2_MAX,
                                AggregationStatisticsHeaders.SC3,
                                AggregationStatisticsHeaders.SC4,
                                AggregationStatisticsHeaders.SC4_ZW2_AVG,
                                AggregationStatisticsHeaders.SC4_ZW2_MIN,
                                AggregationStatisticsHeaders.SC4_ZW2_MAX,
                                /*AggregationStatisticsHeaders.SC5_ZW1_AVG,
                                AggregationStatisticsHeaders.SC5_ZW1_MIN,
                                AggregationStatisticsHeaders.SC5_ZW1_MAX,*/
                                AggregationStatisticsHeaders.GC1,
                                AggregationStatisticsHeaders.GC2_ZW2_AVG,
                                AggregationStatisticsHeaders.GC2_ZW2_MIN,
                                AggregationStatisticsHeaders.GC2_ZW2_MAX,
                                AggregationStatisticsHeaders.GC3,
                                AggregationStatisticsHeaders.GC4,
                                AggregationStatisticsHeaders.GC4_ZW2_AVG,
                                AggregationStatisticsHeaders.GC4_ZW2_MIN,
                                AggregationStatisticsHeaders.GC4_ZW2_MAX,
                                /*AggregationStatisticsHeaders.GC5_ZW1_AVG,
                                AggregationStatisticsHeaders.GC5_ZW1_MIN,
                                AggregationStatisticsHeaders.GC5_ZW1_MAX,*/
                                AggregationStatisticsHeaders.INH_CC,
                                AggregationStatisticsHeaders.TSM,
                                AggregationStatisticsHeaders.SDS,
                                AggregationStatisticsHeaders.INH_AT),
    /**
     * Individual calls.
     */
    INDIVIDUAL("SC (single call)",CallType.INDIVIDUAL,0, 1, StatisticsHeaders.CALL_ATTEMPT_COUNT,
                            StatisticsHeaders.SUCC_SETUP_COUNT,
                            StatisticsHeaders.SETUP_TM_Z1_P1,
                            StatisticsHeaders.SETUP_TM_Z1_P2,
                            StatisticsHeaders.SETUP_TM_Z1_P3,
                            StatisticsHeaders.SETUP_TM_Z1_P4,
                            StatisticsHeaders.SETUP_TM_Z1_L1,
                            StatisticsHeaders.SETUP_TM_Z1_L2,
                            StatisticsHeaders.SETUP_TM_Z1_L3,
                            StatisticsHeaders.SETUP_TM_Z1_L4,
                            StatisticsHeaders.SETUP_TIME_MIN,
                            StatisticsHeaders.SETUP_TIME_MAX,
                            StatisticsHeaders.SETUP_TOTAL_DUR,
                            StatisticsHeaders.SETUP_DUR_Z1_P1,
                            StatisticsHeaders.SETUP_DUR_Z1_P2,
                            StatisticsHeaders.SETUP_DUR_Z1_P3,
                            StatisticsHeaders.SETUP_DUR_Z1_P4,
                            StatisticsHeaders.SETUP_DUR_Z1_L1,
                            StatisticsHeaders.SETUP_DUR_Z1_L2,
                            StatisticsHeaders.SETUP_DUR_Z1_L3,
                            StatisticsHeaders.SETUP_DUR_Z1_L4,
                            StatisticsHeaders.CALL_DISC_TIME,
                            StatisticsHeaders.AUDIO_QUAL_SUCC,
                            StatisticsHeaders.AUDIO_QUAL_P1,
                            StatisticsHeaders.AUDIO_QUAL_P2,
                            StatisticsHeaders.AUDIO_QUAL_P3,
                            StatisticsHeaders.AUDIO_QUAL_P4,
                            StatisticsHeaders.AUDIO_QUAL_L1,
                            StatisticsHeaders.AUDIO_QUAL_L2,
                            StatisticsHeaders.AUDIO_QUAL_L3,
                            StatisticsHeaders.AUDIO_QUAL_L4,
                            StatisticsHeaders.AUDIO_QUAL_MIN,
                            StatisticsHeaders.AUDIO_QUAL_MAX,
                            StatisticsHeaders.AUDIO_QUAL_TOTAL,
                            StatisticsHeaders.AUDIO_QUAL_Z1_P1,
                            StatisticsHeaders.AUDIO_QUAL_Z1_P2,
                            StatisticsHeaders.AUDIO_QUAL_Z1_P3,
                            StatisticsHeaders.AUDIO_QUAL_Z1_P4,
                            StatisticsHeaders.AUDIO_QUAL_Z1_L1,
                            StatisticsHeaders.AUDIO_QUAL_Z1_L2,
                            StatisticsHeaders.AUDIO_QUAL_Z1_L3,
                            StatisticsHeaders.AUDIO_QUAL_Z1_L4/*,
                            StatisticsHeaders.IND_DELAY_COUNT_P1,
                            StatisticsHeaders.IND_DELAY_COUNT_P2,
                            StatisticsHeaders.IND_DELAY_COUNT_P3,
                            StatisticsHeaders.IND_DELAY_COUNT_P4,
                            StatisticsHeaders.IND_DELAY_COUNT_L1,
                            StatisticsHeaders.IND_DELAY_COUNT_L2,
                            StatisticsHeaders.IND_DELAY_COUNT_L3,
                            StatisticsHeaders.IND_DELAY_COUNT_L4,
                            StatisticsHeaders.IND_DELAY_MIN,
                            StatisticsHeaders.IND_DELAY_MAX,
                            StatisticsHeaders.IND_DELAY_TOTAL,
                            StatisticsHeaders.IND_DELAY_Z1_P1,
                            StatisticsHeaders.IND_DELAY_Z1_P2,
                            StatisticsHeaders.IND_DELAY_Z1_P3,
                            StatisticsHeaders.IND_DELAY_Z1_P4,
                            StatisticsHeaders.IND_DELAY_Z1_L1,
                            StatisticsHeaders.IND_DELAY_Z1_L2,
                            StatisticsHeaders.IND_DELAY_Z1_L3,
                            StatisticsHeaders.IND_DELAY_Z1_L4*/),
    
    /**
     * Group calls.
     */
    GROUP("GC (group call)",CallType.GROUP,5,1, StatisticsHeaders.CALL_ATTEMPT_COUNT,
                            StatisticsHeaders.SUCC_SETUP_COUNT,
                            StatisticsHeaders.SETUP_TM_Z1_P1,
                            StatisticsHeaders.SETUP_TM_Z1_P2,
                            StatisticsHeaders.SETUP_TM_Z1_P3,
                            StatisticsHeaders.SETUP_TM_Z1_P4,
                            StatisticsHeaders.SETUP_TM_Z1_L1,
                            StatisticsHeaders.SETUP_TM_Z1_L2,
                            StatisticsHeaders.SETUP_TM_Z1_L3,
                            StatisticsHeaders.SETUP_TM_Z1_L4,
                            StatisticsHeaders.SETUP_TIME_MIN,
                            StatisticsHeaders.SETUP_TIME_MAX,
                            StatisticsHeaders.SETUP_TOTAL_DUR,
                            StatisticsHeaders.SETUP_DUR_Z1_P1,
                            StatisticsHeaders.SETUP_DUR_Z1_P2,
                            StatisticsHeaders.SETUP_DUR_Z1_P3,
                            StatisticsHeaders.SETUP_DUR_Z1_P4,
                            StatisticsHeaders.SETUP_DUR_Z1_L1,
                            StatisticsHeaders.SETUP_DUR_Z1_L2,
                            StatisticsHeaders.SETUP_DUR_Z1_L3,
                            StatisticsHeaders.SETUP_DUR_Z1_L4,
                            StatisticsHeaders.CALL_DISC_TIME,
                            StatisticsHeaders.AUDIO_QUAL_SUCC,
                            StatisticsHeaders.AUDIO_QUAL_P1,
                            StatisticsHeaders.AUDIO_QUAL_P2,
                            StatisticsHeaders.AUDIO_QUAL_P3,
                            StatisticsHeaders.AUDIO_QUAL_P4,
                            StatisticsHeaders.AUDIO_QUAL_L1,
                            StatisticsHeaders.AUDIO_QUAL_L2,
                            StatisticsHeaders.AUDIO_QUAL_L3,
                            StatisticsHeaders.AUDIO_QUAL_L4,
                            StatisticsHeaders.AUDIO_QUAL_MIN,
                            StatisticsHeaders.AUDIO_QUAL_MAX,
                            StatisticsHeaders.AUDIO_QUAL_TOTAL,
                            StatisticsHeaders.AUDIO_QUAL_Z1_P1,
                            StatisticsHeaders.AUDIO_QUAL_Z1_P2,
                            StatisticsHeaders.AUDIO_QUAL_Z1_P3,
                            StatisticsHeaders.AUDIO_QUAL_Z1_P4,
                            StatisticsHeaders.AUDIO_QUAL_Z1_L1,
                            StatisticsHeaders.AUDIO_QUAL_Z1_L2,
                            StatisticsHeaders.AUDIO_QUAL_Z1_L3,
                            StatisticsHeaders.AUDIO_QUAL_Z1_L4/*,
                            StatisticsHeaders.GR_DELAY_COUNT_P1,
                            StatisticsHeaders.GR_DELAY_COUNT_P2,
                            StatisticsHeaders.GR_DELAY_COUNT_P3,
                            StatisticsHeaders.GR_DELAY_COUNT_P4,
                            StatisticsHeaders.GR_DELAY_COUNT_L1,
                            StatisticsHeaders.GR_DELAY_COUNT_L2,
                            StatisticsHeaders.GR_DELAY_COUNT_L3,
                            StatisticsHeaders.GR_DELAY_COUNT_L4,
                            StatisticsHeaders.GR_DELAY_MIN,
                            StatisticsHeaders.GR_DELAY_MAX,
                            StatisticsHeaders.GR_DELAY_TOTAL,
                            StatisticsHeaders.GR_DELAY_Z1_P1,
                            StatisticsHeaders.GR_DELAY_Z1_P2,
                            StatisticsHeaders.GR_DELAY_Z1_P3,
                            StatisticsHeaders.GR_DELAY_Z1_P4,
                            StatisticsHeaders.GR_DELAY_Z1_L1,
                            StatisticsHeaders.GR_DELAY_Z1_L2,
                            StatisticsHeaders.GR_DELAY_Z1_L3,
                            StatisticsHeaders.GR_DELAY_Z1_L4*/),
    /**
     * SDS messages.
     */
    SDS("SDS",CallType.SDS,10,1,StatisticsHeaders.SDS_MESSAGE_ATTEMPT,StatisticsHeaders.SDS_MESSAGE_SUCC),
    /**
     * TSM messages.
     */
    TSM("TSM",CallType.TSM,15,1,StatisticsHeaders.TSM_MESSAGE_ATTEMPT,StatisticsHeaders.TSM_MESSAGE_SUCC),
    /**
     * Alarm messages.
     */
    ALARM("Alarm",CallType.ALARM,40,1,StatisticsHeaders.ALM_ATTEMPT,
                         StatisticsHeaders.ALM_SUCCESS,
                         StatisticsHeaders.ALM_DELAY_TOTAL_SUM,
                         StatisticsHeaders.ALM_DELAY_TOTAL_MIN,
                         StatisticsHeaders.ALM_DELAY_TOTAL_MAX,
                         StatisticsHeaders.ALM_DELAY_FIRST_SUM,
                         StatisticsHeaders.ALM_DELAY_FIRST_MIN,
                         StatisticsHeaders.ALM_DELAY_FIRST_MAX,
                         StatisticsHeaders.ALM_DELAY_Z1_P1,
                         StatisticsHeaders.ALM_DELAY_Z1_P2,
                         StatisticsHeaders.ALM_DELAY_Z1_P3,
                         StatisticsHeaders.ALM_DELAY_Z1_P4,
                         StatisticsHeaders.ALM_DELAY_Z1_L1,
                         StatisticsHeaders.ALM_DELAY_Z1_L2,
                         StatisticsHeaders.ALM_DELAY_Z1_L3,
                         StatisticsHeaders.ALM_DELAY_Z1_L4,
                         StatisticsHeaders.ALM_DELAY_Z2_P1,
                         StatisticsHeaders.ALM_DELAY_Z2_P2,
                         StatisticsHeaders.ALM_DELAY_Z2_P3,
                         StatisticsHeaders.ALM_DELAY_Z2_P4,
                         StatisticsHeaders.ALM_DELAY_Z2_L1,
                         StatisticsHeaders.ALM_DELAY_Z2_L2,
                         StatisticsHeaders.ALM_DELAY_Z2_L3,
                         StatisticsHeaders.ALM_DELAY_Z2_L4,
                         StatisticsHeaders.ALM_DELAY_Z3_P1,
                         StatisticsHeaders.ALM_DELAY_Z3_P2,
                         StatisticsHeaders.ALM_DELAY_Z3_P3,
                         StatisticsHeaders.ALM_DELAY_Z3_P4,
                         StatisticsHeaders.ALM_DELAY_Z3_L1,
                         StatisticsHeaders.ALM_DELAY_Z3_L2,
                         StatisticsHeaders.ALM_DELAY_Z3_L3,
                         StatisticsHeaders.ALM_DELAY_Z3_L4,
                         StatisticsHeaders.ALM_DELAY_Z4_P1,
                         StatisticsHeaders.ALM_DELAY_Z4_P2,
                         StatisticsHeaders.ALM_DELAY_Z4_P3,
                         StatisticsHeaders.ALM_DELAY_Z4_P4,
                         StatisticsHeaders.ALM_DELAY_Z4_L1,
                         StatisticsHeaders.ALM_DELAY_Z4_L2,
                         StatisticsHeaders.ALM_DELAY_Z4_L3,
                         StatisticsHeaders.ALM_DELAY_Z4_L4,
                         StatisticsHeaders.ALM_DELAY_TOTAL_SUM_P1,
                         StatisticsHeaders.ALM_DELAY_TOTAL_SUM_P2,
                         StatisticsHeaders.ALM_DELAY_TOTAL_SUM_P3,
                         StatisticsHeaders.ALM_DELAY_TOTAL_SUM_P4,
                         StatisticsHeaders.ALM_DELAY_TOTAL_SUM_L1,
                         StatisticsHeaders.ALM_DELAY_TOTAL_SUM_L2,
                         StatisticsHeaders.ALM_DELAY_TOTAL_SUM_L3,
                         StatisticsHeaders.ALM_DELAY_TOTAL_SUM_L4),
    /**
     * Emergency call type 1.
     */
    EMERGENCY("EC1",CallType.EMERGENCY,20,1, StatisticsHeaders.EC1_ATTEMPT,StatisticsHeaders.EC1_SUCCESS),
    /**
     * Emergency call type 2.
     */
    HELP("EC2",CallType.HELP,25,1, StatisticsHeaders.EC2_ATTEMPT,StatisticsHeaders.EC2_SUCCESS),
    /**
     * ITSI attach call type.
     */
    ITSI_ATTACH("ITSI-attach",CallType.ITSI_ATTACH,35,1, StatisticsHeaders.ATT_ATTEMPTS,
                                        StatisticsHeaders.ATT_SUCCESS,
                                        StatisticsHeaders.ATT_DELAY_P1,
                                        StatisticsHeaders.ATT_DELAY_P2,
                                        StatisticsHeaders.ATT_DELAY_P3,
                                        StatisticsHeaders.ATT_DELAY_P4,
                                        StatisticsHeaders.ATT_DELAY_L1,
                                        StatisticsHeaders.ATT_DELAY_L2,
                                        StatisticsHeaders.ATT_DELAY_L3,
                                        StatisticsHeaders.ATT_DELAY_L4),
    /**
     * ITSI CC (Handover/cell change) call type.
     */
    ITSI_CC("HO/CC (Handover/cell change)",CallType.ITSI_CC,30,1, StatisticsHeaders.CC_HO_ATTEMPTS,
                                StatisticsHeaders.CC_RES_ATTEMPTS,
                                StatisticsHeaders.CC_HO_SUCCESS,
                                StatisticsHeaders.CC_RES_SUCCESS,
                                StatisticsHeaders.CC_HO_TIME_P1,
                                StatisticsHeaders.CC_HO_TIME_P2,
                                StatisticsHeaders.CC_HO_TIME_P3,
                                StatisticsHeaders.CC_HO_TIME_P4,
                                StatisticsHeaders.CC_HO_TIME_L1,
                                StatisticsHeaders.CC_HO_TIME_L2,
                                StatisticsHeaders.CC_HO_TIME_L3,
                                StatisticsHeaders.CC_HO_TIME_L4,
                                StatisticsHeaders.CC_RES_TIME_P1,
                                StatisticsHeaders.CC_RES_TIME_P2,
                                StatisticsHeaders.CC_RES_TIME_P3,
                                StatisticsHeaders.CC_RES_TIME_P4,
                                StatisticsHeaders.CC_RES_TIME_L1,
                                StatisticsHeaders.CC_RES_TIME_L2,
                                StatisticsHeaders.CC_RES_TIME_L3,
                                StatisticsHeaders.CC_RES_TIME_L4),
    /**
     * Circuit-switched data call type.                       
     */
    CS_DATA("Circuit-switched",CallType.CS_DATA,45,1,StatisticsHeaders.CSD_ATTEMPTS,
                                StatisticsHeaders.CSD_SUCCESS,
                                StatisticsHeaders.CSD_STABILITY,
                                StatisticsHeaders.CSD_THROUGHPUT_MIN,
                                StatisticsHeaders.CSD_THROUGHPUT_MAX,
                                StatisticsHeaders.CSD_CONNECT_TOTAL_DUR,
                                StatisticsHeaders.CSD_DATA_EXCH_SUCC,
                                StatisticsHeaders.CSD_THROUGHPUT_Z1_P1,
                                StatisticsHeaders.CSD_THROUGHPUT_Z1_P2,
                                StatisticsHeaders.CSD_THROUGHPUT_Z1_P3,
                                StatisticsHeaders.CSD_THROUGHPUT_Z1_P4,
                                StatisticsHeaders.CSD_THROUGHPUT_Z1_L1,
                                StatisticsHeaders.CSD_THROUGHPUT_Z1_L2,
                                StatisticsHeaders.CSD_THROUGHPUT_Z1_L3,
                                StatisticsHeaders.CSD_THROUGHPUT_Z1_L4,
                                StatisticsHeaders.CSD_THROUGHPUT_Z2_P1,
                                StatisticsHeaders.CSD_THROUGHPUT_Z2_P2,
                                StatisticsHeaders.CSD_THROUGHPUT_Z2_P3,
                                StatisticsHeaders.CSD_THROUGHPUT_Z2_P4,
                                StatisticsHeaders.CSD_THROUGHPUT_Z2_L1,
                                StatisticsHeaders.CSD_THROUGHPUT_Z2_L2,
                                StatisticsHeaders.CSD_THROUGHPUT_Z2_L3,
                                StatisticsHeaders.CSD_THROUGHPUT_Z2_L4,
                                StatisticsHeaders.CSD_DATA_SUM_Z1_P1,
                                StatisticsHeaders.CSD_DATA_SUM_Z1_P2,
                                StatisticsHeaders.CSD_DATA_SUM_Z1_P3,
                                StatisticsHeaders.CSD_DATA_SUM_Z1_P4,
                                StatisticsHeaders.CSD_DATA_SUM_Z1_L1,
                                StatisticsHeaders.CSD_DATA_SUM_Z1_L2,
                                StatisticsHeaders.CSD_DATA_SUM_Z1_L3,
                                StatisticsHeaders.CSD_DATA_SUM_Z1_L4,
                                StatisticsHeaders.CSD_TIME_SUM_Z1_P1,
                                StatisticsHeaders.CSD_TIME_SUM_Z1_P2,
                                StatisticsHeaders.CSD_TIME_SUM_Z1_P3,
                                StatisticsHeaders.CSD_TIME_SUM_Z1_P4,
                                StatisticsHeaders.CSD_TIME_SUM_Z1_L1,
                                StatisticsHeaders.CSD_TIME_SUM_Z1_L2,
                                StatisticsHeaders.CSD_TIME_SUM_Z1_L3,
                                StatisticsHeaders.CSD_TIME_SUM_Z1_L4),
    /**
     * Packet switched data call type.
     */
    PS_DATA("Packet switched",CallType.PS_DATA,50,1,StatisticsHeaders.IP_ATTEMPTS,
                                StatisticsHeaders.IP_SUCCESS,
                                StatisticsHeaders.IP_STABILITY,
                                StatisticsHeaders.IP_THROUGHPUT_MIN,
                                StatisticsHeaders.IP_THROUGHPUT_MAX,
                                StatisticsHeaders.IP_CONNECT_TOTAL_DUR,
                                StatisticsHeaders.IP_DATA_EXCH_SUCC,
                                StatisticsHeaders.IP_THROUGHPUT_Z1_P1,
                                StatisticsHeaders.IP_THROUGHPUT_Z1_P2,
                                StatisticsHeaders.IP_THROUGHPUT_Z1_P3,
                                StatisticsHeaders.IP_THROUGHPUT_Z1_P4,
                                StatisticsHeaders.IP_THROUGHPUT_Z1_L1,
                                StatisticsHeaders.IP_THROUGHPUT_Z1_L2,
                                StatisticsHeaders.IP_THROUGHPUT_Z1_L3,
                                StatisticsHeaders.IP_THROUGHPUT_Z1_L4,
                                StatisticsHeaders.IP_THROUGHPUT_Z2_P1,
                                StatisticsHeaders.IP_THROUGHPUT_Z2_P2,
                                StatisticsHeaders.IP_THROUGHPUT_Z2_P3,
                                StatisticsHeaders.IP_THROUGHPUT_Z2_P4,
                                StatisticsHeaders.IP_THROUGHPUT_Z2_L1,
                                StatisticsHeaders.IP_THROUGHPUT_Z2_L2,
                                StatisticsHeaders.IP_THROUGHPUT_Z2_L3,
                                StatisticsHeaders.IP_THROUGHPUT_Z2_L4,
                                StatisticsHeaders.IP_DATA_SUM_Z1_P1,
                                StatisticsHeaders.IP_DATA_SUM_Z1_P2,
                                StatisticsHeaders.IP_DATA_SUM_Z1_P3,
                                StatisticsHeaders.IP_DATA_SUM_Z1_P4,
                                StatisticsHeaders.IP_DATA_SUM_Z1_L1,
                                StatisticsHeaders.IP_DATA_SUM_Z1_L2,
                                StatisticsHeaders.IP_DATA_SUM_Z1_L3,
                                StatisticsHeaders.IP_DATA_SUM_Z1_L4,
                                StatisticsHeaders.IP_TIME_SUM_Z1_P1,
                                StatisticsHeaders.IP_TIME_SUM_Z1_P2,
                                StatisticsHeaders.IP_TIME_SUM_Z1_P3,
                                StatisticsHeaders.IP_TIME_SUM_Z1_P4,
                                StatisticsHeaders.IP_TIME_SUM_Z1_L1,
                                StatisticsHeaders.IP_TIME_SUM_Z1_L2,
                                StatisticsHeaders.IP_TIME_SUM_Z1_L3,
                                StatisticsHeaders.IP_TIME_SUM_Z1_L4);
    
    
    public static final Integer FIRST_LEVEL = 1;
    public static final Integer SECOND_LEVEL = 2;
    
    private CallType id;
    private String viewName;
    private Integer level;
    private Integer order;
    private List<IStatisticsHeader> headers;
    
    /**
     * Constructor.
     * @param anId CallType
     * @param statHeaders headers
     */
    private StatisticsCallType(String name, CallType anId,Integer orderValue, Integer aLevel, IStatisticsHeader... statHeaders ) {
        id = anId;
        level = aLevel;
        headers = Arrays.asList(statHeaders);
        viewName = name;
        order = orderValue;
    }
    
    /**
     * @return Returns the id.
     */
    public CallType getId() {
        return id;
    }
    
    /**
     * @return Returns the headers.
     */
    public List<IStatisticsHeader> getHeaders() {
        return headers;
    }
    
    /**
     * @return Returns the level.
     */
    public Integer getLevel() {
        return level;
    }
    
    public IStatisticsHeader getHeaderByTitle(String title){
        for(IStatisticsHeader header : headers){
            if(header.getTitle().equals(title)){
                return header;
            }
        }
        return null;
    }
    
    /**
     * Returns call type.
     *
     * @param id String
     * @return StatisticsCallTypes
     */
    public static StatisticsCallType getTypeById(String id){
        for(StatisticsCallType type : values()){
            if(type.toString().equals(id)){
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown call type <"+id+">");
    }
    
    /**
     * Returns call type.
     *
     * @param id String
     * @return StatisticsCallTypes
     */
    public static StatisticsCallType getTypeByViewName(String name){
        for(StatisticsCallType type : values()){
            if(type.viewName.equals(name)){
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown call type <"+name+">");
    }
    
    /**
     * Returns call type.
     *
     * @param id CallType
     * @return StatisticsCallTypes
     */
    public static StatisticsCallType getTypeById(CallType id){
        for(StatisticsCallType type : getTypesByLevel(FIRST_LEVEL)){
            if(type.id!=null&&type.id.equals(id)){
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown call type <"+id+">");
    }
    
    public static List<StatisticsCallType> getTypesByLevel(Integer level){
        List<StatisticsCallType> result = new ArrayList<StatisticsCallType>();
        for(StatisticsCallType type : values()){
            if(type.level.equals(level)){
                result.add(type);
            }
        }
        return result;
    }
    
    /**
     * @return Returns the viewName.
     */
    public String getViewName() {
        return viewName;
    }
    
    public static List<StatisticsCallType> getSortedTypesList(Set<StatisticsCallType> typesSet){
        List<StatisticsCallType> result = new ArrayList<StatisticsCallType>(typesSet);
        Collections.sort(result, new Comparator<StatisticsCallType>() {
            @Override
            public int compare(StatisticsCallType o1, StatisticsCallType o2) {
                return o1.order.compareTo(o2.order);
            }
        });
        return result;
    }
    
    
}
