package com.example.referentiel.controller;

import com.example.referentiel.exception.ResourceNotFoundException;
import com.example.referentiel.model.Az;
import com.example.referentiel.model.Cidr;
import com.example.referentiel.model.Region;
import com.example.referentiel.model.Subnet;
import com.example.referentiel.model.SubnetCidr;
import com.example.referentiel.model.Vpc;
import com.example.referentiel.repository.AzRepository;
import com.example.referentiel.repository.SubnetCidrRepository;
import com.example.referentiel.repository.SubnetRepository;
import com.example.referentiel.repository.VpcRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
public class SubnetController {

    @Autowired
    private VpcRepository vpcRepository;

    @Autowired
    private SubnetRepository subnetRepository;
    
    @Autowired
    private SubnetCidrRepository subnetCidrRepository;
    
    @Autowired
    private AzRepository azRepository;

    @GetMapping("/vpcs/{vpcId}/subnets")
    public List<Subnet> getSubnetsByVpcId(@PathVariable Long vpcId) {
        return subnetRepository.findByVpcId(vpcId);
    }
    
    @GetMapping("/subnets")
    Collection<Subnet> subnets() {
    	
        return subnetRepository.findAll();
    }
    
    @GetMapping("/subnets/{id}")
    ResponseEntity<?> getSubnet(@PathVariable Long id) {
        Optional<Subnet> subnet = subnetRepository.findById(id);
        
        System.out.println("subnet:" +  subnet.toString());
        
        return subnet.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
/*
    @PostMapping("/vpcs/{vpcId}/subnets")
    public Subnet addSubnet(@PathVariable Long vpcId,
                            @Valid @RequestBody Subnet subnet) {
        return vpcRepository.findById(vpcId)
                .map(vpc -> {
                    subnet.setVpc(vpc);
                    return subnetRepository.save(subnet);
                }).orElseThrow(() -> new ResourceNotFoundException("Vpc not found with id " + vpcId));
    }
*/
    @PostMapping("/vpcs/{vpcId}/subnets")
    public Subnet addSubnet(@PathVariable String vpcId,
                            @Valid @RequestBody Subnet subnet) {
    	
    	long vpId = Long.valueOf(vpcId);
    	
    	Optional<SubnetCidr> sCidr = subnetCidrRepository.findById(subnet.getsCidr().getId());
    	
    	Optional<Az> az = azRepository.findById(subnet.getAz().getId());
    	
        return vpcRepository.findById(vpId)
                .map(vpc -> {
                    subnet.setVpc(vpc);
                    subnet.setAz(az.get());
                    subnet.setsCidr(sCidr.get());
                    sCidr.get().setSubnet(subnet);
                    return subnetRepository.save(subnet);
                }).orElseThrow(() -> new ResourceNotFoundException("Vpc not found with id " + vpcId));
    }
    
    @PutMapping("/vpcs/{vpcId}/subnets/{subnetId}")
    public Subnet updateSubnet(@PathVariable Long vpcId,
                               @PathVariable Long subnetId,
                               @Valid @RequestBody Subnet subnetRequest) {
        if(!vpcRepository.existsById(vpcId)) {
            throw new ResourceNotFoundException("Vpc not found with id " + vpcId);
        }
       
        Optional<Vpc> vpc = vpcRepository.findById(vpcId);
        //Optional<Az> az = azRepository.findById(subnetRequest.getAz().getId());
        
        return subnetRepository.findById(subnetId)
                .map(subnet -> {
                    subnet.setText(subnetRequest.getText());
                    subnet.setType(subnetRequest.getType());
                    subnet.setName(subnetRequest.getName());
                    subnet.setsCidr(subnetRequest.getsCidr());//doute
                    subnet.setVpc(vpc.get());
                    subnet.setAccount(subnetRequest.getAccount());
                    subnet.setAz(subnetRequest.getAz());
                    return subnetRepository.save(subnet);
                }).orElseThrow(() -> new ResourceNotFoundException("Subnet not found with id " + subnetId));
    }

    @DeleteMapping("/vpcs/{vpcId}/subnets/{subnetId}")
    public ResponseEntity<?> deleteSubnet(@PathVariable Long vpcId,
                                          @PathVariable Long subnetId) {
        if(!vpcRepository.existsById(vpcId)) {
            throw new ResourceNotFoundException("Vpc not found with id " + vpcId);
        }

        return subnetRepository.findById(subnetId)
                .map(subnet -> {
                	
                	Az az = subnet.getAz();
                	az.getSubnets().remove(subnet);
                	azRepository.save(az);
                	
                    subnetRepository.delete(subnet);
                    return ResponseEntity.ok().build();
                }).orElseThrow(() -> new ResourceNotFoundException("Subnet not found with id " + subnetId));

    }
}
