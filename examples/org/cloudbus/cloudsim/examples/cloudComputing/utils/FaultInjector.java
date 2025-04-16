package org.cloudbus.cloudsim.examples.cloudComputing.utils;

import org.cloudbus.cloudsim.Vm;
import java.util.*;

public class FaultInjector {
    private final Random rand;
    private final double failureProb;

    public FaultInjector(double failureProb) {
        this.rand = new Random();
        this.failureProb = failureProb;
    }

    public Optional<Integer> simulateFailure(List<Vm> vms) {
        if (!vms.isEmpty() && rand.nextDouble() < failureProb) {
            int vmId = vms.get(rand.nextInt(vms.size())).getId();
            System.out.printf("[FAULT] Injected failure in VM %d\n", vmId);
            return Optional.of(vmId);
        }
        return Optional.empty();
    }
}