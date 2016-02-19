/*
 * Licensed to the University of California, Berkeley under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package alluxio.master.lineage.checkpoint;

import alluxio.Configuration;
import alluxio.Constants;
import alluxio.exception.FileDoesNotExistException;
import alluxio.heartbeat.HeartbeatExecutor;
import alluxio.master.MasterContext;
import alluxio.master.file.FileSystemMaster;
import alluxio.master.lineage.LineageMaster;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * Schedules a checkpoint plan.
 */
@NotThreadSafe
public final class CheckpointSchedulingExcecutor implements HeartbeatExecutor {
  private static final Logger LOG = LoggerFactory.getLogger(Constants.LOGGER_TYPE);

  private final Configuration mConfiguration;
  private final LineageMaster mLineageMaster;
  private final FileSystemMaster mFileSystemMaster;
  private final CheckpointPlanner mPlanner;

  /**
   * @param lineageMaster the master for lineage
   * @param fileSystemMaster the master for the file system
   */
  public CheckpointSchedulingExcecutor(LineageMaster lineageMaster,
      FileSystemMaster fileSystemMaster) {
    mLineageMaster = Preconditions.checkNotNull(lineageMaster);
    mFileSystemMaster = Preconditions.checkNotNull(fileSystemMaster);
    mConfiguration = MasterContext.getConf();
    mPlanner =
        CheckpointPlanner.Factory.create(mConfiguration, mLineageMaster.getLineageStoreView(),
            mFileSystemMaster.getFileSystemMasterView());
  }

  @Override
  public void heartbeat() {
    CheckpointPlan plan = mPlanner.generatePlan(mLineageMaster.getLineageStoreView(),
        mFileSystemMaster.getFileSystemMasterView());
    if (!plan.isEmpty()) {
      LOG.info("Checkpoint scheduler created the plan: {}", plan);
    }
    try {
      mLineageMaster.scheduleForCheckpoint(plan);
    } catch (FileDoesNotExistException e) {
      LOG.error("Checkpoint scheduling failed: {}", e);
    }
  }

  @Override
  public void close() {
    // Nothing to clean up
  }
}