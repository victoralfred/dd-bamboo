package com.ddlabs.atlassian.metrics.model;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask;
import com.atlassian.activeobjects.external.ModelVersion;

public class MetricsAOUpgradeTask001 implements ActiveObjectsUpgradeTask {
    @Override
    public ModelVersion getModelVersion() {
        return ModelVersion.valueOf("1");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void upgrade(ModelVersion modelVersion, ActiveObjects activeObjects) {
        {
            activeObjects.migrate(MSConfig.class); // (4)

            for (MSConfig todo : activeObjects.find(MSConfig.class)) // (5)
            {
                System.out.println("todo");
            }
        }
    }
}
