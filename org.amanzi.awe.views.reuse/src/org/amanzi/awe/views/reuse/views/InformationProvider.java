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

package org.amanzi.awe.views.reuse.views;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.network.FrequencyPlanModel;
import org.amanzi.neo.services.network.NetworkModel;
import org.amanzi.neo.services.node2node.Node2NodeSelectionInformation;
import org.amanzi.neo.services.node2node.NodeToNodeRelationModel;
import org.amanzi.neo.services.statistic.ISelectionInformation;
import org.amanzi.neo.services.statistic.IStatistic;
import org.amanzi.neo.services.statistic.StatisticManager;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * <p>
 * Handle root node and provide necessary ISelectionInformation models for
 * analyse this data set.
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class InformationProvider {

	private IStatistic statistic;
	private DatasetService ds;
	private final Node root;
	private String name;

	/**
	 * Instantiates a new information provider.
	 * 
	 * @param root
	 *            the root
	 */
	public InformationProvider(Node root) {
		this.root = root;
		statistic = StatisticManager.getStatistic(root);
		ds = NeoServiceFactory.getInstance().getDatasetService();
		name = ds.getNodeName(root);

	}

	/**
	 * Gets the statistic map.
	 * 
	 * @return the statistic map
	 */
	public Map<String, ISelectionInformation> getStatisticMap() {
		HashMap<String, ISelectionInformation> result = new HashMap<String, ISelectionInformation>();
		Set<String> rootKey = statistic.getRootKey();

		if (rootKey.isEmpty()&&NodeTypes.DATASET.checkNode(root) ) {
			return result;
		}

		if (NodeTypes.NETWORK.checkNode(root)) {
			Set<String> nodeTypeKey = statistic.getNodeTypeKey(name);
			if (nodeTypeKey.isEmpty()) {
				return result;
			}
			for (String nodeType : nodeTypeKey) {
				ISelectionInformation inf = new BaseNetworkSelectionInformation(
						root, statistic, name, nodeType);
				if (!inf.getPropertySet().isEmpty()) {
					result.put(inf.getDescription(), inf);
				}
			}
			NetworkModel networkModel = new NetworkModel(root);
			Set<NodeToNodeRelationModel> models = networkModel
					.findAllNode2NodeRoot();
			for (NodeToNodeRelationModel model : models) {
				String key = model.getName();
				nodeTypeKey = statistic.getNodeTypeKey(key);
				if (!nodeTypeKey.isEmpty()) {
					for (String nodeType : nodeTypeKey) {
						ISelectionInformation inf = new Node2NodeSelectionInformation(
								root, statistic, model, nodeType,
								getNode2NodeDescripttion(name, model));
						if (!inf.getPropertySet().isEmpty()) {
							result.put(inf.getDescription(), inf);
						}
					}
				}
			}
		} 
		else if (NodeTypes.DATASET.checkNode(root)) {

			Set<String> nodeTypeKey = statistic.getNodeTypeKey(name);
			// if (nodeTypeKey.isEmpty()) {
			// return result;
			// }
			for (String nodeType : nodeTypeKey) {
				ISelectionInformation inf = new BaseDatasetSelectionInforamation(
						root, statistic, name, nodeType);
				if (!inf.getPropertySet().isEmpty()) {
					result.put(inf.getDescription(), inf);
				}

			}
			Iterable<Relationship> iterVirtual = root
					.getRelationships(GeoNeoRelationshipTypes.VIRTUAL_DATASET,
							Direction.OUTGOING);
			for (Relationship rel : iterVirtual) {

				IStatistic statisticVirtual = StatisticManager.getStatistic(rel
						.getEndNode());
				String nameV = ds.getNodeName(rel.getEndNode());
				Set<String> nodeTypeKeyVirtual = statisticVirtual
						.getNodeTypeKey(nameV);

				for (String nodeType : nodeTypeKeyVirtual) {
					ISelectionInformation statInf = new BaseDatasetSelectionInforamation(
							rel.getEndNode(), statisticVirtual, nameV, nodeType);
					if (!statInf.getPropertySet().isEmpty()) {
						result.put(statInf.getDescription(), statInf);
					}
				}
			}
		}

		return result;
	}

	/**
	 * Gets the node 2 node descripttion.
	 * 
	 * @param networkName
	 *            the network name
	 * @param model
	 *            the model
	 * @return the node2 node descripttion
	 */
	private String getNode2NodeDescripttion(String networkName,
			NodeToNodeRelationModel model) {
		return model.getDescription();
		// return String.format("Network %s %s %s", networkName,
		// model.getName(), model.getType().name());
	}

	/**
	 * 
	 * @return
	 */
	public Map<String, ISelectionInformation> getFrequencyStatisticMap() {
		HashMap<String, ISelectionInformation> result = new HashMap<String, ISelectionInformation>();
		Set<String> rootKey = statistic.getRootKey();
		if (rootKey.isEmpty()) {
			return result;
		}
		if (NodeTypes.NETWORK.checkNode(root)) {
			NetworkModel networkModel = new NetworkModel(root);
			for (FrequencyPlanModel model : networkModel.findAllFrqModel()) {
				ISelectionInformation inf = new FreqPlanSelectionInformation(
						statistic, root, model);
				if (!inf.getPropertySet().isEmpty()) {
					result.put(inf.getDescription(), inf);
				}
			}
		}
		return result;
	}

}
