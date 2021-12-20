package ua.com.serverhelp.simplemonitoring.utils;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.serverhelp.simplemonitoring.queue.MetricsQueue;
import ua.com.serverhelp.simplemonitoring.storage.Storage;

@ExtendWith(MockitoExtension.class)
class CronTest {
    @Mock
    private Storage storage;
    @Mock
    private MetricsQueue metricsQueue;

    //TODO implement it
}