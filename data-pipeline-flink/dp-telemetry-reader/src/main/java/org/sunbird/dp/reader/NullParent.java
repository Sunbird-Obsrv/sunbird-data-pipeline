package org.sunbird.dp.reader;

import org.sunbird.dp.util.Logger;
import java.text.MessageFormat;

class NullParent implements ParentType {
	private Logger logger = new Logger(NullParent.class);
	Object parent;
	String childKey;

	NullParent(Object parent, String childKey) {
		this.parent = parent;
		this.childKey = childKey;
	}

	@Override
	public <T> T readChild() {
		logger.warn(null, MessageFormat.format("NULL PARENT READ CHILD INVOKED FOR PARENT: {0}, CHILD KEY: {1}", parent,
				childKey));
		return null;
	}

	@Override
	public void addChild(Object value) {
		logger.warn(null, MessageFormat.format("NULL PARENT ADD CHILD INVOKED FOR PARENT: {0}, CHILD KEY: {1}", parent,
				childKey));
	}
}
