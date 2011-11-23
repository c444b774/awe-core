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

package org.amanzi.neo.loader.core.newsaver;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.amanzi.neo.loader.core.ConfigurationDataImpl;
import org.amanzi.neo.loader.core.newparser.CSVContainer;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferenceManager;
import org.amanzi.neo.services.NewAbstractService;
import org.amanzi.neo.services.NewDatasetService.DatasetTypes;
import org.amanzi.neo.services.NodeTypeManager;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.IDataModel;
import org.amanzi.neo.services.model.IModel;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.impl.DataElement;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 * class represent common actions for csv files savers. Used by: TEMS, ROMES, TRX, TRAFFIC,
 * NEIGHBORS, INTERFERENCE MATRIX, FREQUENCY CONSTRAINTS, NEMO1x, NEMO2x, SEPARATION CONSTRAINT
 * savers
 * 
 * @author Vladislav_Kondratenko
 */
public abstract class AbstractCSVSaver<T1 extends IModel> extends AbstractSaver<T1, CSVContainer, ConfigurationDataImpl> {
    private static final Logger LOGGER = Logger.getLogger(AbstractCSVSaver.class);
    protected final int MAX_TX_BEFORE_COMMIT = 1000;
    protected INetworkModel networkModel;
    protected IDataElement rootDataElement;
    /**
     * line number
     */
    protected Long lineCounter = 0l;
    /**
     * contains appropriation of header synonyms and name inDB
     * <p>
     * <b>key</b>- name in db ,<br>
     * <b>value</b>-file header key
     * </p>
     */
    protected Map<String, String> fileSynonyms = new HashMap<String, String>();
    /**
     * name inDB properties values
     */
    protected Map<String, Integer> columnSynonyms = new HashMap<String, Integer>();
    /**
     * collected parameters
     */
    protected Map<String, Object> params = new HashMap<String, Object>();
    /**
     * file headers
     */
    protected List<String> headers;

    /**
     * check value for null or empty or String value "NULL" or "?"
     * 
     * @param value
     * @return
     */
    protected boolean isCorrect(Object value) {
        if (value == null || value.toString().isEmpty() || value.toString().equals("?")
                || value.toString().equalsIgnoreCase("NULL") || value.toString().equalsIgnoreCase("default")
                || value.toString().equalsIgnoreCase("---") || value.toString().equalsIgnoreCase("N/A")) {
            return false;
        }
        return true;
    }

    /**
     * collect synonyms from element properties
     * 
     * @param nodeType
     * @param collectedName
     */
    protected void addSynonyms(IDataModel model, Map<String, Object> collectedName) {
        String string_type = collectedName.get(NewAbstractService.TYPE).toString();
        INodeType type = NodeTypeManager.getType(string_type);
        for (String name : collectedName.keySet()) {
            String headerName = getHeaderBySynonym(name);
            if (headerName != null && !name.equals(NewAbstractService.NAME) && !name.equals(NewAbstractService.TYPE)) {
                addedDatasetSynonyms(model, type, headerName, name);
            } else if (name.equals(NewAbstractService.NAME)) {
                headerName = getHeaderBySynonym(string_type);
                if (headerName != null) {
                    addedDatasetSynonyms(model, type, NewAbstractService.NAME, headerName);
                }
            }

        }
    }

    /**
     * @param service
     */
    public AbstractCSVSaver(GraphDatabaseService service) {
        super(service);
    }

    /**
     * 
     */
    public AbstractCSVSaver() {
        super();
    }

    @Override
    public void init(ConfigurationDataImpl configuration, CSVContainer dataElement) {
        preferenceStoreSynonyms = preferenceManager.getSynonyms(DatasetTypes.NETWORK);
        columnSynonyms = new HashMap<String, Integer>();
        setDbInstance();
        setTxCountToReopen(MAX_TX_BEFORE_COMMIT);
        commitTx();
        try {
            networkModel = getActiveProject().getNetwork(
                    configuration.getDatasetNames().get(ConfigurationDataImpl.NETWORK_PROPERTY_NAME));
            rootDataElement = new DataElement(networkModel.getRootNode());
            modelMap.put(configuration.getDatasetNames().get(ConfigurationDataImpl.NETWORK_PROPERTY_NAME), networkModel);
            createExportSynonymsForModels();
        } catch (AWEException e) {
            rollbackTx();
            LOGGER.error("Exception on creating root Model", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * get synonym row value and autoparse it
     * 
     * @param synonym
     * @param value
     * @return
     */
    protected Object getSynonymValueWithAutoparse(String synonym, List<String> value) {
        Object findedValue = getValueFromRow(synonym, value);
        if (findedValue == null) {
            return null;
        } else
            return autoParse(synonym, findedValue.toString());
    }

    @Override
    public void saveElement(CSVContainer dataElement) {
        commitTx();
        CSVContainer container = dataElement;
        try {
            if (fileSynonyms.isEmpty()) {
                headers = container.getHeaders();
                makeAppropriationWithSynonyms(headers);
                makeIndexAppropriation();
                lineCounter++;
            } else {
                lineCounter++;
                List<String> value = container.getValues();
                saveLine(value);
            }
        } catch (DatabaseException e) {
            LOGGER.error("Error while saving element on line " + lineCounter, e);
            rollbackTx();
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (Exception e) {
            LOGGER.error("Exception while saving element on line " + lineCounter, e);
            commitTx();
        }
    }

    /**
     * save parsed line from csv container
     * 
     * @param value
     * @throws AWEException
     */
    protected abstract void saveLine(List<String> value) throws AWEException;

    /**
     * get value from row without autoparse (like a string)
     * 
     * @param synonym
     * @param value
     * @return
     */
    protected String getValueFromRow(String synonym, List<String> value) {
        String requiredHeader = synonym;
        if (fileSynonyms.containsKey(synonym)) {
            requiredHeader = fileSynonyms.get(synonym);
        }
        return isCorrect(synonym, value) ? getSynonymValue(value, requiredHeader) : null;
    }

    /**
     * check if row value is correct
     * 
     * @param synonymName
     * @param row
     * @return
     */
    protected boolean isCorrect(String synonymName, List<String> row) {
        String requiredHeader = synonymName;
        if (fileSynonyms.containsKey(synonymName)) {
            requiredHeader = fileSynonyms.get(synonymName);
        }
        return requiredHeader != null && columnSynonyms.containsKey(requiredHeader) && row != null
                && isCorrect(row.get(columnSynonyms.get(requiredHeader)));
    }

    /**
     * get header name by synonymVale
     * 
     * @param synonymName
     * @return
     */
    protected String getHeaderBySynonym(String synonymName) {
        if (fileSynonyms.containsKey(synonymName)) {
            return headers.get(columnSynonyms.get((fileSynonyms.get(synonymName))));
        }
        return null;
    }

    /**
     * return synonym for header
     * 
     * @param header
     * @return
     */
    protected String getSynonymForHeader(String header) {
        for (String key : fileSynonyms.keySet()) {
            if (fileSynonyms.get(key).equals(header)) {
                return key;
            }
        }
        return null;
    }

    /**
     * Get row value by header
     * 
     * @param row
     * @param synonym
     * @return synonym value
     */
    private String getSynonymValue(List<String> row, String propertyName) {
        return row.get(columnSynonyms.get(propertyName));
    }

    /**
     * Null synonym value
     * 
     * @param row
     * @param synonymssaedwd
     */
    protected void resetRowValueBySynonym(List<String> row, String synonym) {
        row.set(columnSynonyms.get(fileSynonyms.get(synonym)), null);
    }

    protected int getHeaderId(String header) {
        return headers.indexOf(header);
    }

    protected void makeIndexAppropriation() {
        for (String synonyms : fileSynonyms.keySet()) {
            columnSynonyms.put(fileSynonyms.get(synonyms), getHeaderId(fileSynonyms.get(synonyms)));
        }
        for (String head : headers) {
            if (!columnSynonyms.containsKey(head)) {
                columnSynonyms.put(head, getHeaderId(head));
            }
        }
    }

    /**
     * make Appropriation with default synonyms and file header
     * 
     * @param keySet -header files;
     */
    protected void makeAppropriationWithSynonyms(List<String> keySet) {
        boolean isAppropriation = false;
        for (String header : keySet) {
            for (String posibleHeader : preferenceStoreSynonyms.keySet()) {
                for (String mask : preferenceStoreSynonyms.get(posibleHeader)) {
                    if (header.toLowerCase().matches(mask.toLowerCase())) {
                        isAppropriation = true;
                        String name = posibleHeader.substring(0, posibleHeader.indexOf(DataLoadPreferenceManager.INFO_SEPARATOR));
                        fileSynonyms.put(name, header);
                        break;
                    }
                }
                if (isAppropriation) {
                    isAppropriation = false;
                    break;
                }
            }
        }
    }

    /**
     * remove incorrect values from properties collection
     * 
     * @param params2
     */
    protected void removeEmpty(Map<String, Object> params2) {
        List<String> keyToDelete = new LinkedList<String>();
        for (String key : params.keySet()) {
            if (!isCorrect(params.get(key))) {
                keyToDelete.add(key);
            }
        }
        for (String key : keyToDelete) {
            params.remove(key);
        }
    }
}
