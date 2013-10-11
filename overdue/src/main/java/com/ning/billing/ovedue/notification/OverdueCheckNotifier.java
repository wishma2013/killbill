/*
 * Copyright 2010-2013 Ning, Inc.
 *
 * Ning licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.ning.billing.ovedue.notification;

import java.util.UUID;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ning.billing.notificationq.api.NotificationEvent;
import com.ning.billing.notificationq.api.NotificationQueueService;
import com.ning.billing.overdue.OverdueProperties;
import com.ning.billing.overdue.listener.OverdueListener;

import com.google.inject.Inject;

public class OverdueCheckNotifier extends DefaultOverdueNotifierBase implements OverdueNotifier {

    private static final Logger log = LoggerFactory.getLogger(OverdueCheckNotifier.class);

    public static final String OVERDUE_CHECK_NOTIFIER_QUEUE = "overdue-check-queue";


    @Inject
    public OverdueCheckNotifier(final NotificationQueueService notificationQueueService, final OverdueProperties config,
                                final OverdueListener listener) {
        super(notificationQueueService, config, listener);
    }

    @Override
    public Logger getLogger() {
        return log;
    }

    @Override
    public String getQueueName() {
        return OVERDUE_CHECK_NOTIFIER_QUEUE;
    }

    @Override
    public void handleReadyNotification(final NotificationEvent notificationKey, final DateTime eventDate, final UUID userToken, final Long accountRecordId, final Long tenantRecordId) {
        try {
            if (!(notificationKey instanceof OverdueCheckNotificationKey)) {
                getLogger().error("Overdue service received Unexpected notificationKey {}", notificationKey.getClass().getName());
                return;
            }

            final OverdueCheckNotificationKey key = (OverdueCheckNotificationKey) notificationKey;
            listener.handleProcessOverdueForAccount(key.getUuidKey(), userToken, accountRecordId, tenantRecordId);
        } catch (IllegalArgumentException e) {
            log.error("The key returned from the NextBillingNotificationQueue is not a valid UUID", e);
        }
    }
}
