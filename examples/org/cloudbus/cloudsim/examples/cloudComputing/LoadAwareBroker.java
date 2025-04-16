package org.cloudbus.cloudsim.examples.cloudComputing;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.lists.VmList;
import java.util.*;

public class LoadAwareBroker extends DatacenterBroker {
    private static final int LOAD_BALANCE_INTERVAL = 10;
    private final Map<Integer, Integer> vmsToDatacenters;
    
    public LoadAwareBroker(String name) throws Exception {
        super(name);
        this.vmsToDatacenters = new HashMap<>();
    }

    @Override
    protected void processVmCreate(SimEvent ev) {
        int[] data = (int[]) ev.getData();
        int vmId = data[1];
        int datacenterId = data[0];
        
        if (vmId != -1) {
            Vm vm = VmList.getById(getVmList(), vmId);
            if (vm != null) {
                getVmsCreatedList().add(vm);
                vmsToDatacenters.put(vmId, datacenterId);
                System.out.printf("%.1f: %s: VM #%d created in Datacenter #%d on Host #%d\n",
                    CloudSim.clock(), getName(), vmId, datacenterId, vm.getHost().getId());
            }
        }

        if (getVmsCreatedList().size() == getVmList().size()) {
            submitCloudlets();
        }
    }

    @Override
    protected void submitCloudlets() {
        for (Cloudlet cloudlet : getCloudletList()) {
            Vm vm = getVmsCreatedList().get(cloudlet.getCloudletId() % getVmsCreatedList().size());
            sendNow(vmsToDatacenters.get(vm.getId()), CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
            cloudlet.setVmId(vm.getId());
        }
        getCloudletList().clear();
    }

    @Override
    protected void processCloudletReturn(SimEvent ev) {
        Cloudlet cloudlet = (Cloudlet) ev.getData();
        getCloudletReceivedList().add(cloudlet);
        
        if (CloudSim.clock() % LOAD_BALANCE_INTERVAL == 0) {
            balanceLoad();
        }

        if (getCloudletList().isEmpty() && getCloudletReceivedList().size() == 20) {
            finishExecution();
        }
    }

    private void balanceLoad() {
        for (int datacenterId : new HashSet<>(vmsToDatacenters.values())) {
            Datacenter dc = (Datacenter) CloudSim.getEntity(datacenterId);
            if (dc != null && dc.getVmAllocationPolicy() instanceof LoadBalancingVmAllocationPolicy) {
                LoadBalancingVmAllocationPolicy policy = 
                    (LoadBalancingVmAllocationPolicy) dc.getVmAllocationPolicy();
                
                System.out.printf("\n%.2f: Checking load...\n", CloudSim.clock());
                for (Host host : dc.getHostList()) {
                    double load = policy.getHostCpuLoad(host);
                    System.out.printf("Host %d: Load = %.2f%% with %d VMs\n",
                            host.getId(), load * 100, host.getVmList().size());
                }

                if (policy.rebalanceVms(CloudSim.clock())) {
                    System.out.printf("%.2f: Load balancing performed\n", CloudSim.clock());
                }
            }
        }
    }
}