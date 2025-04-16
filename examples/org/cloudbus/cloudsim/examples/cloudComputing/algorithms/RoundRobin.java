package org.cloudbus.cloudsim.examples.cloudComputing.algorithms;

import org.cloudbus.cloudsim.Vm;
import java.util.List;

/**
 * Round Robin Load Balancer - Cycles through VMs sequentially
 */
public class RoundRobin implements LoadBalancer {
    private int lastAssignedVmIndex = -1;
    private List<Vm> vmList;

    public RoundRobin(List<Vm> vmList) {
        this.vmList = vmList;
    }

    @Override
    public int assignVm(List<Vm> vmList) {
        lastAssignedVmIndex = (lastAssignedVmIndex + 1) % vmList.size();
        return vmList.get(lastAssignedVmIndex).getId();
    }

    @Override
    public void releaseVm(int vmId) {
        // No state maintenance needed for round robin
    }

    @Override
    public void updateVmList(List<Vm> vms) {
        this.vmList = vms;
        this.lastAssignedVmIndex = -1; // Reset counter
    }
}