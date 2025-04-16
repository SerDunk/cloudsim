package org.cloudbus.cloudsim.examples.cloudComputing.algorithms;

import org.cloudbus.cloudsim.Vm;
import java.util.List;

/**
 * Load Balancer Interface
 */
public interface LoadBalancer {
    int assignVm(List<Vm> vmList);
    void releaseVm(int vmId);
    void updateVmList(List<Vm> vms);
}