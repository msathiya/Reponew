package org.service;

import static org.service.data.ServiceConstant.DOT;
import static org.service.data.ServiceConstant.UNDERSCORE;

import org.apache.commons.lang3.StringUtils;
import org.service.json.HorizontalScale;
import org.service.json.VerticalScale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The <code>Scaling</code> class exposes scaling service call back methods.
 * 
 * @author Sandeep (sandeep.visvanathan@cognizant.com)
 * @author Deepthi (deepthi.g2@cognizant.com)
 * @author Sundar (sundarajan.srinivasan@cognizant.com)
 * @author Ramkumar(ramkumar.kandhakumar@cognizant.com)
 */
public abstract class Scaling {

	/**
	 * Holds LOGGER reference.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(Scaling.class);

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
	public String verticalScale(String deploymentYaml, VerticalScale verticalScale) throws Exception {

		LOGGER.debug("manifest - {}", deploymentYaml);
		/* Replace the new instance type and VM type in the manifest */
		String oldInstanceName = StringUtils.replace(verticalScale.getOldInstanceType(), DOT, UNDERSCORE);
		String newInstanceName = StringUtils.replace(verticalScale.getNewInstanceType(), DOT, UNDERSCORE);
		String updatedManifest = StringUtils.replace(deploymentYaml, verticalScale.getOldInstanceType(),
				verticalScale.getNewInstanceType());
		updatedManifest = StringUtils.replace(updatedManifest, oldInstanceName, newInstanceName);
		LOGGER.debug("udpatedManifest - {}", updatedManifest);
		return updatedManifest;
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
	public abstract HorizontalScale horizontalScale(String deploymentYaml, HorizontalScale horizontalScale)throws Exception;
}
