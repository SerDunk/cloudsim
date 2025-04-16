package org.cloudbus.cloudsim.examples.cloudComputing.algorithms;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;

import java.util.List;
import java.util.Optional;

public interface VmMigrationPolicy {
    class Migration {
        public final Vm vm;
        public final Host sourceHost;
        public final Host targetHost;
        
        public Migration(Vm vm, Host sourceHost, Host targetHost) {
            this.vm = vm;
            this.sourceHost = sourceHost;
            this.targetHost = targetHost;
        }
    }

    Optional<Migration> findMigration(List<Host> hosts);
}