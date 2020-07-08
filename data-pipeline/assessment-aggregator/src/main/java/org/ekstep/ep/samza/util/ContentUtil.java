package org.ekstep.ep.samza.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.task.AssessmentAggregatorConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentUtil {

	private static Logger LOGGER = new Logger(ContentUtil.class);
	private static ObjectMapper mapper = new ObjectMapper();
	private AssessmentAggregatorConfig config;
	private ContentCache contentCache;
	private RestUtil restUtil;

	public ContentUtil(AssessmentAggregatorConfig config, ContentCache cache, RestUtil restUtil) {
		this.config = config;
		this.contentCache = cache;
		this.restUtil = restUtil;
	}

	public List<String> getLeafNodes(String courseId) throws Exception {
		String key = courseId + ":leafnodes";
		List<String> leafNodes = contentCache.getData(key);
		if (CollectionUtils.isEmpty(leafNodes)) {
			Map<String, Object> content = getContent(courseId, "leafNodes");
			leafNodes = (List<String>) content.getOrDefault("leafNodes", new ArrayList<String>());
			if (CollectionUtils.isNotEmpty(leafNodes))
				contentCache.saveData(key, leafNodes, config.getLeafNodesTtl());
		}
		return leafNodes;
	}

	public Map<String, Object> getContent(String courseId, String fields) throws Exception {
		String url = config.getContentServiceBaseUrl() + "/content/v3/read/" + courseId;
		if (StringUtils.isNotBlank(fields))
			url += "?fields=" + fields;
		String httpResponse = restUtil.get(url, new HashMap<String, String>() {{
			put("Content-Type", "application/json");
		}});
		Map<String, Object> response = mapper.readValue(httpResponse, Map.class);
		if (!StringUtils.equalsIgnoreCase("OK", (String) response.getOrDefault("responseCode", ""))) {
			LOGGER.error("ERR_READ_CONTENT", "Error while reading content from KP : " + courseId + " | response :" + httpResponse);
			throw new Exception("Error while reading content from KP : " + courseId);
		}
		return (Map<String, Object>) ((Map<String, Object>) response.getOrDefault("result", new HashMap<String, Object>())).get("content");
	}


}
