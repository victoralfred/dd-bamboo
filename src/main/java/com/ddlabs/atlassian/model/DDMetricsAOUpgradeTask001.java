package com.ddlabs.atlassian.model;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask;
import com.atlassian.activeobjects.external.ModelVersion;

public class DDMetricsAOUpgradeTask001 implements ActiveObjectsUpgradeTask {
    @Override
    public ModelVersion getModelVersion() {
        return ModelVersion.valueOf("1");
    }

    @Override
    public void upgrade(ModelVersion modelVersion, ActiveObjects activeObjects) {
//        {
//            activeObjects.migrate(MetricsServerConfig.class); // (4)
//
//            for (MetricsServerConfig todo : activeObjects.find(MetricsServerConfig.class)) // (5)
//            {
//                todo.save();
//            }
//        }
    }
}
