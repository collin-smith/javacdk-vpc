package com.myorg;

import software.constructs.Construct;

import java.util.ArrayList;
import java.util.List;

import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.DefaultInstanceTenancy;
import software.amazon.awscdk.services.ec2.ISubnet;
import software.amazon.awscdk.services.ec2.IpAddresses;
import software.amazon.awscdk.services.ec2.NatProvider;
import software.amazon.awscdk.services.ec2.SubnetConfiguration;
import software.amazon.awscdk.services.ec2.SubnetType;
import software.amazon.awscdk.services.ec2.Vpc;

public class JavacdkVpcStack extends Stack {
    public JavacdkVpcStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public JavacdkVpcStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);


      // Environment variable to separate the environments
      String application = "RDS";
      String environment = "dev";
      String vpcName = application+"-"+environment+"VPC";
        
      SubnetConfiguration s1 = SubnetConfiguration.builder()
                        .cidrMask(24)
                        .name(vpcName+"-"+environment+"Public")
                        .subnetType(SubnetType.PUBLIC)
                        .build();
        
      SubnetConfiguration s2 = SubnetConfiguration.builder()
                .cidrMask(24)
                .name(vpcName+"-"+environment+"PrivateIsolated")
                .subnetType(SubnetType.PRIVATE_ISOLATED)
                .build();
 
              
      ArrayList<SubnetConfiguration> subnets = new ArrayList<SubnetConfiguration>();
      subnets.add(s1);
      subnets.add(s2);

      
      //Availability zones for the VPC in this region
      List<String> azs = new ArrayList<String>();
      azs.add(this.getRegion()+"a");
      azs.add(this.getRegion()+"b");
      azs.add(this.getRegion()+"c");

      //Can use https://cidr.xyz/ to better design internet addresses
      
      Vpc vpc = Vpc.Builder.create(this, vpcName+"-"+environment)
      .ipAddresses(IpAddresses.cidr("10.0.0.0/20"))
      .defaultInstanceTenancy(DefaultInstanceTenancy.DEFAULT)
      .enableDnsSupport(true)
      .enableDnsHostnames(true)
      .availabilityZones(azs)
      .subnetConfiguration(subnets)
      //.natGateways(0)
      //.natGatewayProvider(NatProvider.gateway())
      .build();
        
      CfnOutput.Builder.create(this, "ZA Region")
      .description("")
       .value("Region:"+ this.getRegion())
       .build();
      
      CfnOutput.Builder.create(this, "ZB VPC Created:")
      .description("")
       .value("VPC Id:"+ vpc.getVpcId())
       .build();
       

      String vpcAzString = "VPC Availability Zones: ";
      List<String> vpcAzs = vpc.getAvailabilityZones();
      for (int i=0;i<vpcAzs.size();i++)
      {
      	String vpcAzItem = vpcAzs.get(i);
      	vpcAzString += " ("+vpcAzItem+") ";
      }

      CfnOutput.Builder.create(this, "ZC VPC Availability Zones:")
      .description("")
       .value("VPC Availability Zones:"+ vpcAzString)
       .build();
      
      String subnetDescription = "Public Subnets: ";
      List<ISubnet> publicSubnets = vpc.getPublicSubnets();
      for (int i=0;i<publicSubnets.size();i++)
      {
      	ISubnet publicSubnet = publicSubnets.get(i);
      	subnetDescription += " ("+publicSubnet.getSubnetId()+") ";
      }
      CfnOutput.Builder.create(this, "ZD VPC Public subnets Created:")
      .description("")
       .value("# Public subnets:"+ vpc.getPublicSubnets().size()+":"+subnetDescription)
       .build();
          
      subnetDescription = "Private Isolated Subnets: ";
      List<ISubnet> isolatedSubnets = vpc.getIsolatedSubnets();
      for (int i=0;i<isolatedSubnets.size();i++)
      {
      	ISubnet isolatedSubnet = isolatedSubnets.get(i);
      	subnetDescription += " ("+isolatedSubnet.getSubnetId()+") ";
      }

      CfnOutput.Builder.create(this, "ZF VPC Private Isolated subnets Created:")
      .description("")
       .value("# Private Isolated subnets:"+ vpc.getIsolatedSubnets().size()+":"+subnetDescription)
       .build();
      
    }
}
