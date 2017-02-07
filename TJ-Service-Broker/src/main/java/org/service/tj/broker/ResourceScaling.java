package org.service.tj.broker;

import org.service.Scaling;
import org.service.json.HorizontalScale;
import org.service.json.VerticalScale;
import org.springframework.stereotype.Component;

/**
 * The <code>ResourceScaling</code> class override scaling service call back
 * implementation for specific resource.
 * 
 * @author Rajnish(rajnishkumar.pandey@cognizant.com)
 */
@Component
public class ResourceScaling extends Scaling {

	/**
	 * Method to change deployment manifest for vertical scaling.
	 * 
	 * @param deploymentYaml
	 *            holds deployment YAML manifest.
	 * @param verticalScale
	 *            holds vertical scale reference.
	 * @return String returns updated deployment manifest as String.
	 * @throws Exception 
	 */
	@Override
	public String verticalScale(String deploymentYaml, VerticalScale verticalScale) throws Exception {

		return super.verticalScale(deploymentYaml, verticalScale);
	}

	/**
	 * Method to change deployment manifest for horizontal scaling.
	 * 
	 * @param deploymentYaml
	 *            holds deployment YAML manifest.
	 * @param horizontalScale
	 *            holds horizontal scale reference.
	 * @return String returns updated deployment manifest as String.
	 */
	@Override
	@SuppressWarnings("unused")
	public HorizontalScale horizontalScale(String deploymentYaml, HorizontalScale horizontalScale) {
		if (true) {
			throw new RuntimeException("Method not implemented.");
		}
		return null;
	}

}
