package org.cloudbus.cloudsim.examples.cloudComputing;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.lists.VmList;
import org.cloudbus.cloudsim.provisioners.*;
import java.util.*;

public class MySimulation {
    private static final int NUM_HOSTS = 5;
    private static final int NUM_VMS = 10;
    private static final int NUM_CLOUDLETS = 20;
    private static final double MIGRATION_THRESHOLD = 0.8;
    
    public static void main(String[] args) {
        System.out.println("Starting Dynamic Load Balancing Simulation with VM Migration");

        try {
            CloudSim.init(1, Calendar.getInstance(), false);

            Datacenter datacenter = createDatacenter();
            LoadAwareBroker broker = new LoadAwareBroker("LoadAwareBroker");
            
            List<Vm> vms = createVms(broker.getId());
            broker.submitVmList(vms);

            List<Cloudlet> cloudlets = createCloudlets(broker.getId());
            broker.submitCloudletList(cloudlets);

            CloudSim.startSimulation();
            printResults(broker, datacenter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Datacenter createDatacenter() {
        List<Host> hostList = new ArrayList<>();
        
        for (int i = 0; i < NUM_HOSTS; i++) {
            List<Pe> peList = new ArrayList<>();
            int mips = (i < 2) ? 3000 : 2000;
            int cores = (i < 2) ? 4 : 2;
            for (int j = 0; j < cores; j++) {
                peList.add(new Pe(j, new PeProvisionerSimple(mips)));
            }

            hostList.add(new Host(
                i,
                new RamProvisionerSimple((i < 2) ? 32768 : 16384),
                new BwProvisionerSimple((i < 2) ? 20000 : 10000),
                1000000,
                peList,
                new VmSchedulerTimeShared(peList)));
        }

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
            "x86", "Linux", "Xen", hostList, 10.0, 3.0, 0.05, 0.001, 0.0);

        try {
            return new Datacenter(
                "DynamicLoadBalancedDC", 
                characteristics, 
                new LoadBalancingVmAllocationPolicy(hostList, MIGRATION_THRESHOLD),
                new LinkedList<Storage>(), 
                0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static List<Vm> createVms(int brokerId) {
        List<Vm> vms = new ArrayList<>();
        Random rand = new Random();
        
        for (int i = 0; i < NUM_VMS; i++) {
            vms.add(new Vm(
                i, 
                brokerId,
                500 + rand.nextInt(1500),
                1 + rand.nextInt(2),
                512 + rand.nextInt(1536),
                500 + rand.nextInt(1500),
                5000 + rand.nextInt(15000),
                "Xen",
                new CloudletSchedulerTimeShared()));
        }
        return vms;
    }

    private static List<Cloudlet> createCloudlets(int brokerId) {
        List<Cloudlet> cloudlets = new ArrayList<>();
        Random rand = new Random();
        UtilizationModel utilizationModel = new UtilizationModelStochastic();
        
        for (int i = 0; i < NUM_CLOUDLETS; i++) {
            Cloudlet cloudlet = new Cloudlet(
                i, 
                5000 + rand.nextInt(15000),
                1,
                200 + rand.nextInt(800),
                200 + rand.nextInt(800),
                utilizationModel,
                utilizationModel,
                utilizationModel);
            cloudlet.setUserId(brokerId);
            cloudlets.add(cloudlet);
        }
        return cloudlets;
    }

    private static void printResults(DatacenterBroker broker, Datacenter datacenter) {
        List<Cloudlet> finishedCloudlets = broker.getCloudletReceivedList();
        
        System.out.println("\n=== FINAL RESULTS ===");
        System.out.println("Cloudlet ID\tStatus\tVM ID\tHost ID\tTime\tWorkload");
        
        for (Cloudlet cloudlet : finishedCloudlets) {
            Vm vm = VmList.getById(broker.getVmList(), cloudlet.getVmId());
            Host host = vm.getHost();
            
            System.out.printf("%d\t\t%s\t%d\t%d\t%.2f\t%d MI\n",
                cloudlet.getCloudletId(),
                cloudlet.getCloudletStatus() == Cloudlet.SUCCESS ? "SUCCESS" : "FAILED",
                cloudlet.getVmId(),
                host.getId(),
                cloudlet.getActualCPUTime(),
                cloudlet.getCloudletLength());
        }
        
        ((LoadBalancingVmAllocationPolicy)datacenter.getVmAllocationPolicy()).printResults();
    }
}