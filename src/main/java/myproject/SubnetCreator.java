package myproject;

import com.pulumi.Context;
import com.pulumi.aws.AwsFunctions;
import com.pulumi.aws.ec2.*;
import com.pulumi.aws.inputs.GetAvailabilityZonesPlainArgs;
import com.pulumi.aws.outputs.GetAvailabilityZonesResult;
import com.pulumi.aws.rds.SubnetGroup;
import com.pulumi.aws.rds.SubnetGroupArgs;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SubnetCreator {

    public static SubnetGroup createSubnetGroupRDS(List<String> subnetIdList) {
        return new SubnetGroup("default", SubnetGroupArgs.builder()
                .subnetIds(subnetIdList)
                .tags(Map.of("Name", "My DB subnet group"))
                .build());
    }

    public static ArrayList<Subnet> createThreeSubnetWithRouteTable(Vpc myvpc, String subnetCiderList, String subnetTagNameList, RouteTable rt, Context ctx) {

        String[] ciderList = subnetCiderList.split(",\\s*");
        ArrayList<String> listOfCider = new ArrayList<>(Arrays.asList(ciderList));

        String[] nameList = subnetTagNameList.split(",\\s*");
        ArrayList<String> listOfName = new ArrayList<>(Arrays.asList(nameList));

        int numOfSubnets = listOfCider.size();
        ArrayList<Subnet> subnetList = new ArrayList<>();

        try {
            final var available = AwsFunctions.getAvailabilityZonesPlain(GetAvailabilityZonesPlainArgs.builder()
                    .state("available")
                    .build());

            final GetAvailabilityZonesResult getAvailabilityZonesResult = available.get();

            final List<String> zoneNameList = getAvailabilityZonesResult.names();

            if (numOfSubnets > zoneNameList.size()) {
                numOfSubnets = zoneNameList.size();
            }

            for (int i = 0; i < numOfSubnets; i++) {

                String subnetCiderBlockValue = listOfCider.get(i);
                String subnetTagNameValue = listOfName.get(i);
                final int zoneIndex = i;
                Subnet mysubnet = new Subnet(subnetTagNameValue, SubnetArgs.builder()
                        .tags(Map.of("Name", subnetTagNameValue))
                        .vpcId(myvpc.id())
                        .cidrBlock(subnetCiderBlockValue)
                        .availabilityZone(zoneNameList.get(i))
                        .mapPublicIpOnLaunch(true)
                        .build());
                associateRouteTableToSubnet(mysubnet, rt);
                subnetList.add(mysubnet);
            }
        } catch (Exception e) {
            ctx.log().error(e.getMessage());
        }

        return subnetList;
    }

    private static void associateRouteTableToSubnet(Subnet mysubnet, RouteTable rt) {
        String randomName = generateRandomString(10);
        new RouteTableAssociation(randomName, RouteTableAssociationArgs.builder()
                .subnetId(mysubnet.id())
                .routeTableId(rt.id())
                .build()
        );
    }

    public static String generateRandomString(int length) {
        final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }

        return sb.toString();
    }

    public static List<String> getSubnetIdListFromSubnets(ArrayList<Subnet> subnetList) {
        List<String> subnetIdList = new ArrayList<>();
        for (Subnet subnet : subnetList) {
            subnetIdList.add(subnet.id().toString());
        }
        return  subnetIdList;
    }
}
