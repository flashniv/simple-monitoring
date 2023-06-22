package ua.com.serverhelp.simplemonitoring.service.alert.alerters;

import org.junit.jupiter.api.Test;
import ua.com.serverhelp.simplemonitoring.AbstractTest;

class SlackAlertSenderTest extends AbstractTest {

    @Test
    void sendMessage() throws Exception {
//        String conf = """
//                {
//                    "slackWebHook":"______",
//                    "slackChannel":"dept_dev_admin_alerts",
//                    "slackUserName":"Simple monitoring"
//                }
//                """;
//        AlertSender alertSender = new SlackAlertSender();
//        alertSender.initialize(conf);
//        alertSender.sendMessage(
//                Alert.builder()
//                        .alertTimestamp(Instant.now())
//                        .trigger(Trigger.builder()
//                                .name("Free disk space less than 15% on exporter.test.scout-db.node{\"device\":\"/dev/md127\",\"fstype\":\"ext4\",\"mountpoint\":\"/mnt/data\"}")
//                                .lastStatus(TriggerStatus.OK)
//                                .build())
//                        .triggerStatus(TriggerStatus.OK)
//                        .build()
//        );
    }
}