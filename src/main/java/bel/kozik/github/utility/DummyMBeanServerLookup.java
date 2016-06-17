package bel.kozik.github.utility;

import org.infinispan.jmx.MBeanServerLookup;

import javax.management.MBeanServer;
import java.util.Properties;

/**
 *
 * Dummy MBean Server Lookup. Use it only in tests.
 *
 * Created by anton on 17.06.16.
 */
public class DummyMBeanServerLookup implements MBeanServerLookup {

    @Override
    public MBeanServer getMBeanServer(Properties properties) {
        return null;
    }

}
