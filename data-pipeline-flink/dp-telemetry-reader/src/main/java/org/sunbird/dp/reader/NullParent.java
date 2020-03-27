package org.sunbird.dp.reader;

import org.sunbird.dp.util.Logger;

import java.text.MessageFormat;

// import org.ekstep.ep.samza.core.Logger;

class NullParent implements ParentType {
    static Logger LOGGER = new Logger(NullParent.class);
    Object parent;
    String childKey;

    NullParent(Object parent, String childKey) {
        this.parent = parent;
        this.childKey = childKey;
    }

    @Override
    public <T> T readChild() {
        LOGGER.warn(null, MessageFormat.format("NULL PARENT READ CHILD INVOKED FOR PARENT: {0}, CHILD KEY: {1}", parent, childKey));
        return null;
    }

    @Override
    public void addChild(Object value) {
        LOGGER.warn(null, MessageFormat.format("NULL PARENT ADD CHILD INVOKED FOR PARENT: {0}, CHILD KEY: {1}", parent, childKey));
    }
}
