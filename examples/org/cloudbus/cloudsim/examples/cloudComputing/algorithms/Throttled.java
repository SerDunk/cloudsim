package org.cloudbus.cloudsim.examples.cloudComputing.algorithms;

import org.cloudbus.cloudsim.Vm;
import java.util.*;

/**
 * Throttled Load Balancer - Maintains a map of VM availability states
 */
public class Throttled implements LoadBalancer {
    private final Map<Integer, Boolean> vmAvailabilityMap;
    private final List<Vm> vmList;

    public Throttled(List<Vm> vmList) {
        this.vmList = vmList;
        this.vmAvailabilityMap = new HashMap<>();
        vmList.forEach(vm -> vmAvailabilityMap.put(vm.getId(), true));
    }

    @Override
    public int assignVm(List<Vm> vmList) {
        // Find first available VM
        for (Vm vm : vmList) {
            if (vmAvailabilityMap.getOrDefault(vm.getId(), false)) {
                vmAvailabilityMap.put(vm.getId(), false); // Mark as busy
                return vm.getId();
            }
        }
        return -1; // No available VMs
    }

    @Override
    public void releaseVm(int vmId) {
        vmAvailabilityMap.put(vmId, true); // Mark as available
    }

    @Override
    public void updateVmList(List<Vm> vms) {
        this.vmList.clear();
        this.vmList.addAll(vms);
        vms.forEach(vm -> vmAvailabilityMap.putIfAbsent(vm.getId(), true));
    }
}