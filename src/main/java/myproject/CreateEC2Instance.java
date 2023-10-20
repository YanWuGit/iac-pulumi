package myproject;


import com.pulumi.aws.ec2.*;
import com.pulumi.aws.ec2.inputs.InstanceRootBlockDeviceArgs;
import java.util.List;
import java.util.Map;

public class CreateEC2Instance {

    public static Instance createEC2Instance(SecurityGroup sg, String ami, Subnet mysubnet, String keyName) {
        return new Instance("MyEc2Instance", InstanceArgs.builder()
                .ami(ami)
                .instanceType("t2.micro")
                .subnetId(mysubnet.id())
                .vpcSecurityGroupIds(sg.id().applyValue(List::of))
                .keyName(keyName)
                .rootBlockDevice(InstanceRootBlockDeviceArgs.builder()
                        .volumeType("gp2")
                        .volumeSize(25)
                        .deleteOnTermination(true)
                        .build())
                .tags(Map.of("Name", "MyEC2Instance"))
                .build());
    }
}
