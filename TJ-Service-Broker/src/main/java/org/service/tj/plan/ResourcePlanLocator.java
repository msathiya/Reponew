package org.service.tj.plan;

import static org.service.tj.data.ResourceConstant.DEDICATED_CLUSTER_INSTANCE;
import static org.service.tj.data.ResourceConstant.DEDICATED_SINGLE_INSTANCE;
import static org.service.tj.data.ResourceConstant.SANDBOX_INSTANCE;
import static org.service.tj.data.ResourceConstant.SHARED_CLUSTER_INSTANCE;

import org.apache.commons.lang3.StringUtils;
import org.service.tj.data.ResourceEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ResourcePlanLocator {

	@Autowired
	private ResourceEnvironment rEnvironment;

	@Autowired
	@Qualifier(SANDBOX_INSTANCE)
	private ResourcePlan sandbox;

	@Autowired
	@Qualifier(SHARED_CLUSTER_INSTANCE)
	private ResourcePlan sharedCluster;

	@Autowired
	@Qualifier(DEDICATED_SINGLE_INSTANCE)
	private ResourcePlan dedicatedSingle;

	@Autowired
	@Qualifier(DEDICATED_CLUSTER_INSTANCE)
	private ResourcePlan dedicatedCluster;

	public ResourcePlan lookup(String planUUID) {
		ResourcePlan plan = null;
		if (StringUtils.equalsIgnoreCase(rEnvironment.getPlanSandboxGUID(), planUUID)) {
			plan = sandbox;
		} else if (StringUtils.equalsIgnoreCase(rEnvironment.getPlanSharedClusterGUID(), planUUID)) {
			plan = sharedCluster;
		} else if (StringUtils.equalsIgnoreCase(rEnvironment.getPlanDedicatedSingleGUID(), planUUID)) {
			plan = dedicatedSingle;
		} else if (StringUtils.equalsIgnoreCase(rEnvironment.getPlanDedicatedClusterGUID(), planUUID)) {
			plan = dedicatedCluster;
		} else {
			throw new RuntimeException(String.format("Plan '%s' not implemented.", planUUID));
		}
		return plan;
	}
}
