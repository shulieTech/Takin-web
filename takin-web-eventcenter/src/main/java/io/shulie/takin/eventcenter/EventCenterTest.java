package io.shulie.takin.eventcenter;

import io.shulie.takin.eventcenter.annotation.IntrestFor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * -
 *
 * @author -
 */
@Component
@Slf4j
public class EventCenterTest {

    @IntrestFor(event = "start")
    public void doEvent(Event event) {
        log.info("" + event);
    }

}
