package com.ddlabs.atlassian.impl.config.model;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask;
import com.atlassian.activeobjects.external.ModelVersion;
import com.ddlabs.atlassian.impl.data.adapter.entity.MSConfigEntity;

public class MetricsAOUpgradeTask001 implements ActiveObjectsUpgradeTask {
    @Override
    public ModelVersion getModelVersion() {
        return ModelVersion.valueOf("1");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void upgrade(ModelVersion modelVersion, ActiveObjects activeObjects) {
        {
            activeObjects.migrate(MSConfigEntity.class); // (4)

            for (MSConfigEntity todo : activeObjects.find(MSConfigEntity.class)) // (5)
            {
                System.out.println("todo");
            }
        }
    }
}
