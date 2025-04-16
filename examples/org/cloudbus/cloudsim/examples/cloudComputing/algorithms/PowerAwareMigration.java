package org.cloudbus.cloudsim.examples.cloudComputing.algorithms;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;

import java.util.*;

public class PowerAwareMigration implements VmMigrationPolicy {
    @Override
    public Optional<Migration> findMigration(List<Host> hosts) {
        // Find most overloaded host
        Optional<Host> overloaded = hosts.stream()
            .max(Comparator.comparingDouble(this::getHostCpuUtilization));

        // Find most underloaded host
        Optional<Host> underloaded = hosts.stream()
            .min(Comparator.comparingDouble(this::getHostCpuUtilization));

        if (overloaded.isPresent() && underloaded.isPresent() && 
            getHostCpuUtilization(overloaded.get()) > 0.8 &&
            getHostCpuUtilization(underloaded.get()) < 0.3) {
            
            return overloaded.get().getVmList().stream()
                .findFirst()
                .map(vm -> new Migration(vm, overloaded.get(), underloaded.get()));
        }
        return Optional.empty();
    }

    private double getHostCpuUtilization(Host host) {
        return host.getVmList().stream()
            .mapToDouble(vm -> vm.getTotalUtilizationOfCpu(CloudSim.clock()))
            .sum() / host.getNumberOfPes();
    }
}