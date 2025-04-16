package org.cloudbus.cloudsim.examples.cloudComputing.utils;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;

import java.util.ArrayList;
import java.util.List;

public class CloudletGenerator {

    public static List<Cloudlet> generateCloudlets(int brokerId, int count) {
        List<Cloudlet> list = new ArrayList<>();

        long length = 400;
        int pesNumber = 1;
        long fileSize = 300;
        long outputSize = 300;
        UtilizationModel utilizationModel = new UtilizationModelFull();

        for (int i = 0; i < count; i++) {
            Cloudlet cloudlet = new Cloudlet(i, length, pesNumber, fileSize, outputSize,
                    utilizationModel, utilizationModel, utilizationModel);
            cloudlet.setUserId(brokerId);
            list.add(cloudlet);
        }

        return list;
    }
}
