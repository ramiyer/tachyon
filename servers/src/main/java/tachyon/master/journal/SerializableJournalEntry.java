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

package tachyon.master.journal;

import java.util.Map;

/**
 * This kind of JournalEntry will have a parameter field named transactionId.
 * TODO (Gene): didn't see transactionId in the class (HY).
 */
public class SerializableJournalEntry implements JournalEntry {
  private final long mSequenceNumber;
  private final JournalEntry mEntry;

  protected SerializableJournalEntry(long sequenceNumber, JournalEntry entry) {
    mSequenceNumber = sequenceNumber;
    mEntry = entry;
  }

  public long getSequenceNumber() {
    return mSequenceNumber;
  }

  @Override
  public JournalEntryType getType() {
    return mEntry.getType();
  }

  @Override
  public Map<String, Object> getParameters() {
    return mEntry.getParameters();
  }
}