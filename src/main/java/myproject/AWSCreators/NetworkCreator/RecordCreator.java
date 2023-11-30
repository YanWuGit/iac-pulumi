package myproject.AWSCreators.NetworkCreator;

import com.pulumi.aws.alb.LoadBalancer;
import com.pulumi.aws.route53.Record;
import com.pulumi.aws.route53.RecordArgs;
import com.pulumi.aws.route53.Route53Functions;
import com.pulumi.aws.route53.inputs.GetZoneArgs;
import com.pulumi.aws.route53.inputs.RecordAliasArgs;
import com.pulumi.aws.route53.outputs.GetZoneResult;

public class RecordCreator {
    public static Record createRecord(LoadBalancer appLoadBalancer, String domainName) {

        final var selected = Route53Functions.getZone(GetZoneArgs.builder()
                .name(domainName)
                .build());


        return new Record(".", RecordArgs.builder()
                .zoneId(selected.applyValue(GetZoneResult::zoneId))
                .name(".")
                .type("A")
                .aliases(RecordAliasArgs.builder()
                        .name(appLoadBalancer.dnsName())
                        .zoneId(appLoadBalancer.zoneId())
                        .evaluateTargetHealth(true)
                        .build())
                .build());
    }
}
