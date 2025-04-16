package org.cloudbus.cloudsim.examples.cloudComputing;

import org.cloudbus.cloudsim.*;
import java.util.*;

public class LoadBalancingVmAllocationPolicy extends VmAllocationPolicy {
    private final Map<Vm, Host> vmTable;
    private final double migrationThreshold;

    public LoadBalancingVmAllocationPolicy(List<? extends Host> list, double threshold) {
        super(list);
        vmTable = new HashMap<>();
        this.migrationThreshold = threshold;
    }

    @Override
    public boolean allocateHostForVm(Vm vm) {
        for (Host host : getHostList()) {
            if (host.isSuitableForVm(vm) && host.vmCreate(vm)) {
                vmTable.put(vm, host);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean allocateHostForVm(Vm vm, Host host) {
        if (host.vmCreate(vm)) {
            vmTable.put(vm, host);
            return true;
        }
        return false;
    }

    @Override
    public void deallocateHostForVm(Vm vm) {
        Host host = vmTable.remove(vm);
        if (host != null) {
            host.vmDestroy(vm);
        }
    }

    @Override
    public Host getHost(Vm vm) {
        return vmTable.get(vm);
    }

    @Override
    public Host getHost(int vmId, int userId) {
        for (Vm vm : vmTable.keySet()) {
            if (vm.getId() == vmId && vm.getUserId() == userId) {
                return vmTable.get(vm);
            }
        }
        return null;
    }

    public boolean rebalanceVms(double currentTime) {
        boolean migrationHappened = false;

        for (Host host : getHostList()) {
            double load = getHostCpuLoad(host);
            if (load > migrationThreshold) {
                List<Vm> vmsToMigrate = new ArrayList<>(host.getVmList());

                for (Vm vm : vmsToMigrate) {
                    Host targetHost = findUnderloadedHost(vm);
                    if (targetHost != null && targetHost != host) {
                        host.vmDestroy(vm);
                        targetHost.vmCreate(vm);
                        vmTable.put(vm, targetHost);
                        System.out.printf("%.2f: VM %d migrated from Host %d to Host %d\n",
                                currentTime, vm.getId(), host.getId(), targetHost.getId());
                        migrationHappened = true;
                    }
                }
            }
        }
        return migrationHappened;
    }

    public double getHostCpuLoad(Host host) {
        double totalMips = host.getTotalMips();
        double usedMips = 0;
        for (Vm vm : host.getVmList()) {
            usedMips += vm.getCurrentRequestedTotalMips();
        }
        return totalMips == 0 ? 0 : usedMips / totalMips;
    }

    private Host findUnderloadedHost(Vm vm) {
        for (Host host : getHostList()) {
            if (getHostCpuLoad(host) < migrationThreshold && host.isSuitableForVm(vm)) {
                return host;
            }
        }
        return null;
    }

    @Override
    public List<Map<String, Object>> optimizeAllocation(List<? extends Vm> vmList) {
        return null;
    }
    
    public void printResults() {
        System.out.println("\n=== HOST LOAD STATISTICS ===");
        for (Host host : getHostList()) {
            double load = getHostCpuLoad(host);
            System.out.printf("Host %d: Final Load = %.2f%% with %d VMs\n",
                    host.getId(), load * 100, host.getVmList().size());
        }
    }
}