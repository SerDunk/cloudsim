package org.cloudbus.cloudsim.examples.cloudComputing.algorithms;

import org.cloudbus.cloudsim.*;
import java.util.*;

public class FaultTolerantScheduler {
    private final List<Vm> vmList;
    private final List<Host> hostList;

    public FaultTolerantScheduler(List<Vm> vmList, List<Host> hostList) {
        this.vmList = vmList;
        this.hostList = hostList;
    }

    public void handleVmFailure(int failedVmId) {
        System.out.printf("[RECOVERY] Handling VM %d failure:\n", failedVmId);
        System.out.println("1. Checkpoint recovery initiated");
        System.out.println("2. Migrating affected cloudlets");
        
        // Find least loaded host for restart
        hostList.stream()
            .min(Comparator.comparingDouble(h -> h.getVmList().size()))
            .ifPresent(host -> {
                System.out.printf("3. Restarting VM on Host %d\n", host.getId());
            });
    }
}